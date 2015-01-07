/*
 * Copyright 2014 Christian Weber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.arrow.service.engine.actor.receive;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.dispatch.Futures;
import akka.dispatch.OnSuccess;

import org.springframework.context.ApplicationContext;
import org.arrow.runtime.TokenRegistry;
import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionDataService;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.EntityEventMessage;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.impl.SynchronizeExecutionEventMessage;
import org.arrow.runtime.message.infrastructure.*;
import org.arrow.service.engine.actor.template.MasterTemplate.MessageReceiveContext;
import org.arrow.service.engine.concurrent.SaveNodeCallable;
import org.arrow.service.engine.concurrent.dispatch.onsuccess.TellActorForEachMessage;
import org.arrow.service.engine.concurrent.dispatch.recover.Recovers;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.Future;

import java.util.Set;
import java.util.function.Consumer;

public class InfrastructureMessageConsumer {

    /**
     * Handles a {@link org.arrow.runtime.message.infrastructure.PersistEventMessage} message.
     *
     * @param context the message receive context instance
     * @return PartialFunction
     */
    public static Consumer<PersistEventMessage> persist(MessageReceiveContext context) {
        return msg -> {
            final ActorContext ac = context.getActorContext();
            final Future<Iterable<Object>> futures = context.getFutureSequence();
            futures.onSuccess(new PersistOnSuccess(msg, context), ac.dispatcher());
        };
    }

    public static Consumer<SynchronizeEventMessage> synchronize(MessageReceiveContext context) {

        final ActorRef self = context.getActorContext().self();
        final ExecutionContextExecutor dispatcher = context.getActorContext().dispatcher();
        final ExecutionService executionService = context.getService();
        final ApplicationContext applicationContext = context.getAppContext();

        return msg -> {
            ExecutionDataService.SynchronisationResult synchronisationResult = msg.getSynchronisationResult();

            if (!synchronisationResult.isSynchronised()) {
                return;
            }

            final BpmnNodeEntitySpecification entity = msg.getExecution().getEntity();

            final String piId = msg.getProcessInstance().getId();
            final String entityId = msg.getExecution().getEntity().getId();

            int expected = synchronisationResult.getExpectedFlows();

            Set<Execution> executions;
            executions = executionService.data().execution().findJoiningExecutions(piId, entityId);

            if (executions.size() >= expected) {
                // set the execution state to SUCCESS
                msg.getExecution().setState(State.SUCCESS);
                SaveNodeCallable callable = new SaveNodeCallable(msg, applicationContext);
                Future<EntityEventMessage> future = Futures.future(callable, dispatcher);

                self.tell(new FutureAdapter(future, msg), self);


                boolean first = true;
                for (Execution execution : executions) {
                    if (first) {
                        first = false;
                        continue;
                    }
                    self.tell(new TokenEventMessage(execution, TokenEventMessage.TokenAction.CONSUME), self);
                }
                executionService.data().execution().synchronizeGatewayExecutions(piId, entityId);
                EventMessage message = new SynchronizeExecutionEventMessage(entity, msg.getProcessInstance());
                message = new PersistEventMessage(msg.getExecution(), message);
                self.tell(message, self);
            }

        };
    }

    /**
     * Invokes the callable instance of the callable event message.
     *
     * @param context the message receive context
     * @return Consumer
     */
    public static Consumer<CallableEventMessage> callable(MessageReceiveContext context) {

        final TokenRegistry tokenRegistry = context.getTokenRegistry();
        final ActorRef self = context.getActorContext().self();
        final ExecutionContextExecutor dispatcher = context.getActorContext().dispatcher();

        return msg -> {
            if (tokenRegistry.hasToken()) {
                Future<Iterable<EventMessage>> future = Futures.future(msg.getCallable(), dispatcher);

                future.onSuccess(new TellActorForEachMessage(self, self), dispatcher);
                future.recover(Recovers.logAndThrow(), dispatcher);
            }
        };
    }

    public static Consumer<TokenEventMessage> tokenAction(MessageReceiveContext context) {

        final TokenRegistry tokenRegistry = context.getTokenRegistry();

        return msg -> {
            switch (msg.getTokenAction()) {
                case PRODUCE:
                    tokenRegistry.produce();
                    break;
                case CONSUME:
                    tokenRegistry.consume();
                    break;
                case TERMINATE:
                    tokenRegistry.terminate();
                    break;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static Consumer<FutureAdapter> futureAdapter(MessageReceiveContext context) {
        return msg -> context.getFutures().add((Future) msg);
    }

    @SuppressWarnings("unchecked")
    public static Consumer<FutureEventMessage> futureEvent(MessageReceiveContext context) {
        return msg -> context.getFutures().add((Future<Object>) msg.getFuture());
    }

    public static class PersistOnSuccess extends OnSuccess<Iterable<Object>> {

        private final PersistEventMessage message;
        private final ActorContext actorContext;

        public PersistOnSuccess(PersistEventMessage message, MessageReceiveContext context) {
            this.message = message;
            this.actorContext = context.getActorContext();
        }

        @Override
        public void onSuccess(Iterable<Object> objects) {
            if (message.getMessage() != null) {
                actorContext.self().tell(message.getMessage(), actorContext.self());
            }
        }

    }

}
