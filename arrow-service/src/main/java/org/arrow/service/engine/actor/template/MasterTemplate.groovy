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

package org.arrow.service.engine.actor.template

import akka.actor.ActorRef
import akka.dispatch.Futures
import akka.japi.Procedure
import org.arrow.runtime.TokenRegistry
import org.arrow.runtime.api.BpmnNodeEntitySpecification
import org.arrow.runtime.execution.Execution
import org.arrow.runtime.execution.State
import org.arrow.runtime.execution.service.ExecutionDataService
import org.arrow.runtime.execution.service.ExecutionService
import org.arrow.runtime.logger.LoggerFacade
import org.arrow.runtime.message.*
import org.arrow.runtime.message.impl.*
import org.arrow.runtime.message.infrastructure.*
import org.arrow.service.engine.actor.AbstractActor
import org.arrow.service.engine.actor.MasterActor
import org.arrow.service.engine.concurrent.SaveNodeCallable
import org.arrow.service.engine.concurrent.dispatch.onsuccess.PersistOnSuccess
import org.arrow.service.engine.concurrent.dispatch.onsuccess.TellActorForEachMessage
import org.arrow.service.engine.concurrent.dispatch.recover.Recovers
import org.springframework.context.ApplicationContext
import scala.PartialFunction
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.Future
import scala.runtime.BoxedUnit

/**
 * Template pattern implementation for {@link org.arrow.service.engine.actor.MasterActor} actor instances.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public abstract class MasterTemplate extends AbstractActor {

    /**
     * List of futures used to block until the background tasks
     * are completed.
     */
    private List<Future<Object>> storedFutures = Collections.synchronizedList(new ArrayList<>());

    protected Future<Iterable<Object>> getStoredFutures() {
        ExecutionContext ec = getContext().system().dispatcher();
        return Futures.sequence(storedFutures, ec);
    }

    private final TokenRegistry tokenRegistry = new TokenRegistry();

    private final static LoggerFacade LOGGER = new LoggerFacade(MasterTemplate.class);

    private final Procedure<Object> DEFAULT_BEHAVIOR = new DefaultProcedure()

    public MasterTemplate(ApplicationContext context, Map<String, Object> scopeMap) {
        super(context, scopeMap);
    }

    private class DefaultProcedure implements Procedure<Object> {

        @Override
        void apply(Object param) throws Exception {
            // @formatter:off
            switch (param) {
                case FutureAdapter:               futureAdapter(param);                   break
                case noEventMessage:              unhandled(param);                       break
                case isSuspended:                 unhandled(param);                       break
                case PersistEventMessage:         persist(param);                         break
                case SynchronizeEventMessage:     synchronize(param);                     break
                case CallableEventMessage:        callable(param);                        break
                case TokenEventMessage:           tokenAction(param);                     break
                case FutureEventMessage:          futureEvent(param);                     break
                case StartEventMessage:           onReceiveStart(param);                  break
                case StartSubProcessEventMessage: onReceiveStartSubProcess(param);        break
                case EndSubProcessEventMessage:   onReceiveEndSubProcess(param);          break
                case AbortEventMessage:           onReceiveAbort(param);                  break
                case EndEventMessage:             onReceiveEnd(param);                    break
                case SignalEventMessage:          onReceiveSignalEvent(param);            break
                case MessageEventMessage:         onReceiveMessageEvent(param);           break
                case TimerEventMessage:           onReceiveTimerEvent(param);             break
                case ConditionEventMessage:       onReceiveConditionalEvent(param);       break
                case ErrorEventMessage:           onReceiveErrorEvent(param);             break
                case EscalationEventMessage:      onReceiveEscalationEvent(param);        break
                case CancelEventMessage:          onReceiveCancelEvent(param);            break
                case EventSubProcessEventMessage: onReceiveSubProcessEventMessage(param); break
                case FinishEventMessage:          onReceiveFinishMessage(param);          break
                case ContinueEventMessage:        onReceiveContinue(param);               break
                case ExecuteEventMessage:         onReceiveExecute(param);                break
                default: unhandled(param)
            }
            // @formatter:on
        }
    }

    /**
     * Returns the {@link TokenRegistry} instance.
     *
     * @return TokenRegistry
     */
    protected final TokenRegistry getTokenRegistry() {
        return tokenRegistry;
    }

    def isSuspended = { Object msg ->
        switch (msg) {
            case InfrastructureEventMessage: return false
            case FinishEventMessage: return false
        }

        def pi = ((EventMessage) msg).processInstance
        getExecutionService().fetchEntity(pi);

        switch (msg) {
            case {!pi.isSuspend()}: return false
            case EndEventMessage: return !((EndEventMessage) msg).isForce()
            default: return !(msg instanceof SubProcessEventMessage)
        }
    }

    def noEventMessage = {!(it instanceof EventMessage);}

    @Override
    public void unhandled(Object message) {
        LOGGER.warn("unhandled message %s", message);
        super.unhandled(message);
    }

    @Override
    public void aroundReceive(PartialFunction<Object, BoxedUnit> receive, Object msg) {
        try {
            LOGGER.debug("handle " + msg);
            super.aroundReceive(receive, msg);
        } catch (Throwable throwable) {
            LOGGER.error(throwable);
            throwable.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) throws Exception {
        DEFAULT_BEHAVIOR.apply(message);
    }

    protected void becomeBehavior(Procedure<Object> behavior) {
        getContext().become(composite(DEFAULT_BEHAVIOR, behavior));
    }

    protected void unbecomeBehavior() {
        getContext().unbecome();
    }

    public static Procedure<Object> composite(Procedure... procedures) {
        return new Procedure<Object>() {

            @Override
            public void apply(Object message) throws Exception {
                for (Procedure procedure : procedures) {
                    procedure.apply(message);
                }
            }
        };
    }

    /**
     * Handles a {@link org.arrow.runtime.message.impl.StartEventMessage} message.
     *
     * @param start the start message
     */
    protected abstract void onReceiveStart(StartEventMessage start);

    /**
     * Handles a {@link org.arrow.runtime.message.ContinueEventMessage} message.
     *
     * @param continueWith the continue with message
     */
    protected abstract void onReceiveContinue(ContinueEventMessage continueWith);

    /**
     * Handles a {@link ExecuteEventMessage} message.
     *
     * @param executeEventMessage the execute event message
     */
    protected abstract void onReceiveExecute(ExecuteEventMessage executeEventMessage);

    /**
     * Handles a {@link org.arrow.runtime.message.impl.AbortEventMessage} message.
     *
     * @param abort the abort message
     */
    protected abstract void onReceiveAbort(AbortEventMessage abort);

    /**
     * Handles a {@link org.arrow.runtime.message.impl.EndEventMessage} message.
     *
     * @param end the end message
     */
    protected abstract void onReceiveEnd(EndEventMessage end);

    /**
     * Handles a {@link org.arrow.runtime.message.impl.SignalEventMessage} message.
     *
     * @param signalEvent the signal event message
     */
    protected abstract void onReceiveSignalEvent(SignalEventMessage signalEvent);

    /**
     * Handles a {@link org.arrow.runtime.message.impl.MessageEventMessage} message.
     *
     * @param messageEvent the message event message
     */
    protected abstract void onReceiveMessageEvent(MessageEventMessage messageEvent);

    /**
     * Handles a {@link org.arrow.runtime.message.impl.TimerEventMessage} message.
     *
     * @param timerEvent the timer event message
     */
    protected abstract void onReceiveTimerEvent(TimerEventMessage timerEvent);

    /**
     * Handles a {@link org.arrow.runtime.message.impl.ConditionEventMessage} message.
     *
     * @param conditionalEvent the conditional event message
     */
    protected abstract void onReceiveConditionalEvent(
            ConditionEventMessage conditionalEvent);

    /**
     * Handles a {@link org.arrow.runtime.message.impl.ErrorEventMessage} message.
     *
     * @param errorEvent the error event message
     */
    protected abstract void onReceiveErrorEvent(ErrorEventMessage errorEvent);

    /**
     * Handles a {@link org.arrow.runtime.message.impl.EscalationEventMessage} message.
     *
     * @param escalationEvent the escalation event message
     */
    protected abstract void onReceiveEscalationEvent(EscalationEventMessage escalationEvent);

    /**
     * Handles a {@link org.arrow.runtime.message.impl.EscalationEventMessage} message.
     *
     * @param escalationEvent the escalation event message
     */
    protected abstract void onReceiveCancelEvent(CancelEventMessage escalationEvent);

    /**
     * Handles a {@link org.arrow.runtime.message.impl.CompensateEventMessage} message.
     *
     * @param escalationEvent the compensate event message
     */
    protected abstract void onReceiveCompensateEvent(CompensateEventMessage escalationEvent);

    /**
     * Handles a {@link org.arrow.runtime.message.impl.StartSubProcessEventMessage} message.
     *
     * @param continueWith the continue with message
     */
    protected abstract void onReceiveStartSubProcess(StartSubProcessEventMessage continueWith);

    protected abstract void onReceiveFinishMessage(FinishEventMessage message);

    /**
     * Handles a {@link org.arrow.runtime.message.impl.EndSubProcessEventMessage} message.
     *
     * @param continueWith the continue with message
     */
    protected abstract void onReceiveEndSubProcess(EndSubProcessEventMessage continueWith);

    protected abstract void onReceiveSubProcessEventMessage(EventSubProcessEventMessage message);

    public static class CompensateBehavior implements Procedure {

        private final MasterTemplate template;

        public CompensateBehavior(Object template) {
            this.template = template as MasterTemplate
        }

        @Override
        public void apply(Object message) throws Exception {
            if (message instanceof CompensateEventMessage) {
                template.onReceiveCompensateEvent(message)
            }
        }

    }

    /**
     * Handles a {@link org.arrow.runtime.message.infrastructure.PersistEventMessage} message.
     *
     * @return PartialFunction
     */
    public void persist(Object msg) {
        getStoredFutures().onSuccess(new PersistOnSuccess(msg, context()), context().dispatcher());
    }

    public void futureAdapter(Object msg) {
        storedFutures.add((Future) msg);
    }

    /**
     * Invokes the callable instance of the callable event message.
     *
     * @return Consumer
     */
    public void callable(Object msg) {
        if (tokenRegistry.hasToken()) {
            Future<Iterable<EventMessage>> future = Futures.future(msg.getCallable(), context().dispatcher());

            future.onSuccess(new TellActorForEachMessage(self(), self()), context().dispatcher());
            future.recover(Recovers.logAndThrow(), context().dispatcher());
        }
    }

    public void tokenAction(TokenEventMessage msg) {
        switch (msg.getTokenAction()) {
            case TokenEventMessage.TokenAction.PRODUCE:
                tokenRegistry.produce();
                break;
            case TokenEventMessage.TokenAction.CONSUME:
                tokenRegistry.consume();
                break;
            case TokenEventMessage.TokenAction.TERMINATE:
                tokenRegistry.terminate();
                break;
        }
    }

    public void futureEvent(Object msg) {
        storedFutures.add((Future<Object>) msg.getFuture());
    }

    public void synchronize(Object msg) {

        final ActorRef self = context().self();
        final ExecutionContextExecutor dispatcher = context().dispatcher();
        final ExecutionService executionService = getExecutionService();
        final ApplicationContext applicationContext = getApplicationContext();

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

    }

}
