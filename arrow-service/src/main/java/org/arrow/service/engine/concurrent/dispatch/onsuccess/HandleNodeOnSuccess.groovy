/*
 * Copyright 2014 Christian Weber
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

package org.arrow.service.engine.concurrent.dispatch.onsuccess

import akka.actor.ActorRef
import akka.actor.UntypedActorContext
import akka.dispatch.OnSuccess
import org.arrow.model.definition.escalation.EscalationEventDefinitionAware
import org.arrow.model.event.boundary.impl.CompensateBoundaryEvent
import org.arrow.model.event.endevent.EndEvent
import org.arrow.model.gateway.impl.InclusiveGateway
import org.arrow.model.gateway.impl.ParallelGateway
import org.arrow.model.transition.Flow
import org.arrow.model.transition.IncomingFlowAware
import org.arrow.runtime.api.BpmnNodeEntitySpecification
import org.arrow.runtime.execution.ExecutionGroup
import org.arrow.runtime.execution.State
import org.arrow.runtime.logger.LoggerFacade
import org.arrow.runtime.message.EntityEventMessage
import org.arrow.runtime.message.EventMessage
import org.arrow.runtime.message.impl.DefaultExecuteEventMessage
import org.arrow.runtime.message.impl.EndEventMessage
import org.arrow.runtime.message.infrastructure.PersistEventMessage
import org.arrow.service.engine.execution.ExecutionGroupEnhancer
import org.arrow.service.engine.util.NodeUtils

/**
 * {@link OnSuccess} implementation used to handle {@link org.arrow.runtime.message.EventMessage} instances after
 * execution.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class HandleNodeOnSuccess extends OnSuccess<Iterable<EventMessage>> {

    private static final LoggerFacade LOGGER = new LoggerFacade(HandleNodeOnSuccess.class)

    private final ActorRef sender
    private final ActorRef self
    private final EntityEventMessage msg

    public HandleNodeOnSuccess(UntypedActorContext context, EntityEventMessage msg) {
        this.msg = msg
        this.sender = context.sender()
        this.self = context.self()
    }

    // conditions
    // ##########
    def isNotFinished = {!msg.execution.isFinished()}
    def isSuspended = {msg.execution.state.compareTo(State.SUSPEND) == 0}
    def isCompensation = {isCompensationTask(msg.entity)}
    def isEndEvent = {msg.entity instanceof EndEvent}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSuccess(Iterable<EventMessage> events) {

        LOGGER.debug("handle node " + msg)

        def map = ExecutionGroupEnhancer.enhance(msg.execution, msg.executionGroup)

        switch (msg) {
            case isNotFinished:  return
            case isSuspended:    return
            case isCompensation: return
            case isEndEvent:     sender.tell(handleEndEvent(msg), self); return
        }

        def outgoingFlows = NodeUtils.getRelevantFlows(msg)
        outgoingFlows.each { flow ->
            flow.enable()
            flow.targetRef.findAll { it.id == flow.id }.each { it.enable() }
            sender.tell(handleNode(flow, msg, map), self)
        }
    }

    /**
     * Indices if the given entity is a compensation task.
     *
     * @param entity
     * @return boolean
     */
    private static boolean isCompensationTask(BpmnNodeEntitySpecification entity) {
        if (!(entity instanceof IncomingFlowAware)) return false
        entity.incomingFlows.matchAny { it.sourceRef instanceof CompensateBoundaryEvent }
    }

    /**
     * Prepares a {@link org.arrow.runtime.message.impl.EndEventMessage} message.
     *
     * @param msg the continue message
     * @return End
     */
    private static EventMessage handleEndEvent(EntityEventMessage msg) {
        EventMessage eventMessage = new EndEventMessage(msg.entity, msg.processInstance)
        return doPersist(msg, null) ? new PersistEventMessage(msg.execution, eventMessage) : eventMessage
    }

    /**
     * Prepares a {@link org.arrow.runtime.message.ContinueEventMessage} message.
     *
     * @param flow the flow instance
     * @param msg the continue message
     * @return Continue
     */
    private static EventMessage handleNode(Flow flow, EntityEventMessage msg, Map<String, ExecutionGroup> map) {
        def event = new DefaultExecuteEventMessage(flow.targetRef, msg.processInstance, map.get(flow.id))
        return doPersist(msg, flow) ? new PersistEventMessage(msg.execution, event) : event
    }

    /**
     * Indicates if the given message should be persisted.
     *
     * @param msg
     * @param flow
     * @return boolean
     */
    private static boolean doPersist(EntityEventMessage msg, Flow flow) {
        switch (msg) {
            case { it.entity instanceof EscalationEventDefinitionAware }: return true
            case { flow?.targetRef instanceof InclusiveGateway }: return true
            case { flow?.targetRef instanceof ParallelGateway }: return true
            default: return false
        }
    }

}
