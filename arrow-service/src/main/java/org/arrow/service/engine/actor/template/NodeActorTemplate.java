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

import akka.actor.ActorSystem;
import akka.japi.Procedure;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StopWatch;
import org.arrow.model.definition.multiple.MultipleEventAware;
import org.arrow.model.event.boundary.BoundaryEvent;
import org.arrow.model.event.boundary.BoundaryEventAware;
import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.logger.LoggerFacade;
import org.arrow.runtime.message.*;
import org.arrow.runtime.message.impl.*;
import org.arrow.runtime.support.EngineSynchronizationManager;
import org.arrow.service.engine.actor.AbstractActor;
import org.arrow.service.engine.actor.receive.DefaultMessageHandlerRegistry;
import org.arrow.service.engine.actor.receive.MessageHandlerRegistry;
import scala.concurrent.ExecutionContextExecutor;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.arrow.util.Predicates.when;

/**
 * Actor template class designed to handle {@link org.arrow.runtime.message.EventMessage} messages.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public abstract class NodeActorTemplate extends AbstractActor {

    private final Procedure<Object> DEFAULT_BEHAVIOR = getDefaultBehavior();

    public NodeActorTemplate(ApplicationContext context, Map<String, Object> scopeMap) {
        super(context, scopeMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initEngineSynchronizationManager(EventMessage msg) {
        super.initEngineSynchronizationManager(msg);
        EngineSynchronizationManager.setCurrentActor(getSender());
    }

    protected Procedure<Object> getDefaultBehavior() {
        return message -> {
            //@formatter:off
            MessageHandlerRegistry registry = new DefaultMessageHandlerRegistry();
            registry.register(when(noEventMessage()),                                             this::unhandled);
            registry.register(when(SignalEventMessage.class, this::hasMultipleEventDefinitions) , this::onReceiveSignalEventMessage);
            registry.register(when(MessageEventMessage.class, this::hasMultipleEventDefinitions), this::onReceiveMessageEventMessage);
            registry.register(when(EscalationEventMessage.class),                                 this::onReceiveEscalationEventMessage);
            registry.register(when(ExecuteEventMessage.class),                                    executeEventMessageConsumer);
            registry.register(when(ContinueEventMessage.class),                                   this::onReceiveContinueMessage);
            registry.register(when(FinishEventMessage.class),                                     this::onReceiveFinishMessage);
            registry.register(when(msg -> true),                                                  this::unhandled);
            //@formatter:on

            registry.handle(message);
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) throws Exception {
        DEFAULT_BEHAVIOR.apply(message);
    }

    private void executeBoundaryEventsIfPresent(ExecuteEventMessage message) {

        final BpmnNodeEntitySpecification entity = message.getEntity();
        final ProcessInstance pi = message.getProcessInstance();
        if (entity instanceof BoundaryEventAware) {
            BoundaryEventAware aware = (BoundaryEventAware) entity;
            for (BoundaryEvent boundaryEvent : aware.getBoundaryEvents()) {
                ExecuteEventMessage msg = new DefaultExecuteEventMessage(boundaryEvent, pi);
                onReceiveExecuteMessage(msg);
            }
        }
    }

    /**
     * Returns the execution context executor from the actor system.
     *
     * @return ExecutionContextExecutor
     */
    protected ExecutionContextExecutor getExecutionContextExecutor() {
        ActorSystem actorSystem = getBean(ActorSystem.class);
        return actorSystem.dispatcher();
    }



    /**
     * Handles a {@link org.arrow.runtime.message.ExecuteEventMessage} message.
     *
     * @param msg the message object
     */
    protected abstract void onReceiveExecuteMessage(EntityEventMessage msg);

    /**
     * Handles a {@link org.arrow.runtime.message.ContinueEventMessage} message.
     *
     * @param msg the message object
     */
    protected abstract void onReceiveContinueMessage(EntityEventMessage msg);

    /**
     * Handles a {@link org.arrow.runtime.message.FinishEventMessage} event message.
     *
     * @param msg the message object
     */
    protected abstract void onReceiveFinishMessage(FinishEventMessage msg);

    /**
     * Handles a {@link org.arrow.runtime.message.impl.MessageEventMessage} event message.
     *
     * @param msg the message object
     */
    protected abstract void onReceiveMessageEventMessage(MessageEventMessage msg);

    protected abstract void onReceiveSignalEventMessage(SignalEventMessage msg);

    protected abstract void onReceiveEscalationEventMessage(EscalationEventMessage msg);

    private boolean hasMultipleEventDefinitions(EntityEventMessage message) {
        BpmnNodeEntitySpecification entity = message.getEntity();
        return entity instanceof MultipleEventAware && ((MultipleEventAware) entity).getEventDefinitions().size() > 1;
    }

    private Predicate<Object> noEventMessage() {
        return msg -> !(msg instanceof EventMessage);
    }

    private Consumer<ExecuteEventMessage> executeEventMessageConsumer = msg -> {
            executeBoundaryEventsIfPresent(msg);
            onReceiveExecuteMessage(msg);
    };

}
