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

package org.arrow.service.engine.actor.template;

import akka.actor.ActorContext;
import akka.dispatch.Futures;
import akka.japi.Procedure;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StopWatch;
import org.arrow.runtime.TokenRegistry;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.logger.LoggerFacade;
import org.arrow.runtime.message.*;
import org.arrow.runtime.message.impl.*;
import org.arrow.runtime.message.infrastructure.*;
import org.arrow.service.engine.actor.AbstractActor;
import org.arrow.service.engine.actor.receive.DefaultMessageHandlerRegistry;
import org.arrow.service.engine.actor.receive.MessageHandlerRegistry;
import org.arrow.util.ActorUtils;
import scala.PartialFunction;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.runtime.BoxedUnit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.arrow.service.engine.actor.receive.InfrastructureMessageConsumer.*;
import static org.arrow.util.Predicates.when;
import static org.arrow.util.Predicates.when;

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

    private final Procedure<Object> DEFAULT_BEHAVIOR = getDefaultBehavior();

    public MasterTemplate(ApplicationContext context, Map<String, Object> scopeMap) {
        super(context, scopeMap);
    }

    private Procedure<Object> getDefaultBehavior() {

        //@formatter:off
        MessageHandlerRegistry registry = new DefaultMessageHandlerRegistry();
        registry.register(when(FutureAdapter.class),               futureAdapter(new MessageReceiveContext()));
        registry.register(when(noEventMessage()),                  this::unhandled);
        registry.register(when(processIsSuspend()),                this::unhandled);
        registry.register(when(PersistEventMessage.class),         persist(new MessageReceiveContext()));
        registry.register(when(SynchronizeEventMessage.class),     synchronize(new MessageReceiveContext()));
        registry.register(when(CallableEventMessage.class),        callable(new MessageReceiveContext()));
        registry.register(when(TokenEventMessage.class),           tokenAction(new MessageReceiveContext()));
        registry.register(when(FutureEventMessage.class),          futureEvent(new MessageReceiveContext()));
        registry.register(when(StartEventMessage.class),           this::onReceiveStart);
        registry.register(when(StartSubProcessEventMessage.class), this::onReceiveStartSubProcess);
        registry.register(when(EndSubProcessEventMessage.class),   this::onReceiveEndSubProcess);
        registry.register(when(AbortEventMessage.class),           this::onReceiveAbort);
        registry.register(when(EndEventMessage.class),             this::onReceiveEnd);
        registry.register(when(SignalEventMessage.class),          this::onReceiveSignalEvent);
        registry.register(when(MessageEventMessage.class),         this::onReceiveMessageEvent);
        registry.register(when(TimerEventMessage.class),           this::onReceiveTimerEvent);
        registry.register(when(ConditionEventMessage.class),       this::onReceiveConditionalEvent);
        registry.register(when(ErrorEventMessage.class),           this::onReceiveErrorEvent);
        registry.register(when(EscalationEventMessage.class),      this::onReceiveEscalationEvent);
        registry.register(when(CancelEventMessage.class),          this::onReceiveCancelEvent);
        registry.register(when(EventSubProcessEventMessage.class), this::onReceiveSubProcessEventMessage);
        registry.register(when(FinishEventMessage.class),          this::onReceiveFinishMessage);
        registry.register(when(ContinueEventMessage.class),        this::onReceiveContinue);
        registry.register(when(ExecuteEventMessage.class),         this::onReceiveExecute);
        registry.register(when(msg -> true),                       this::unhandled);
        //@formatter:on

        return registry::handle;
    }

    /**
     * Returns the {@link TokenRegistry} instance.
     *
     * @return TokenRegistry
     */
    protected final TokenRegistry getTokenRegistry() {
        return tokenRegistry;
    }

    private Predicate<Object> processIsSuspend() {

        return msg -> {
            if (msg instanceof InfrastructureEventMessage) {
                return false;
            }

            if (msg instanceof FinishEventMessage) {
                return false;
            }

            EventMessage message = (EventMessage) msg;

            final ProcessInstance pi = message.getProcessInstance();
            getExecutionService().fetchEntity(pi);

            if (pi.isSuspend() && msg instanceof EndEventMessage) {
                return !((EndEventMessage) msg).isForce();
            }



            return pi.isSuspend() && !(msg instanceof SubProcessEventMessage);
        };

    }

    private Predicate<Object> noEventMessage() {
        return msg -> !(msg instanceof EventMessage);
    }

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

    protected void becomeBehavior(Procedure<Object>behavior) {
        getContext().become(ActorUtils.composite(DEFAULT_BEHAVIOR, behavior));
    }

    protected void unbecomeBehavior() {
        getContext().unbecome();
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

    public class CompensateBehavior implements Procedure<Object> {

        @Override
        public void apply(Object message) throws Exception {

            if (message instanceof CompensateEventMessage) {
                onReceiveCompensateEvent((CompensateEventMessage) message);
            }

        }

    }



    public class MessageReceiveContext {

        public ActorContext getActorContext() {
            return getContext();
        }

        public Future<Iterable<Object>> getFutureSequence() {
            return getStoredFutures();
        }

        public ExecutionService getService() {
            return getExecutionService();
        }

        public ApplicationContext getAppContext() {
            return getApplicationContext();
        }

        public TokenRegistry getTokenRegistry() {
            return tokenRegistry;
        }

        public List<Future<Object>> getFutures() {
            return storedFutures;
        }

    }

}
