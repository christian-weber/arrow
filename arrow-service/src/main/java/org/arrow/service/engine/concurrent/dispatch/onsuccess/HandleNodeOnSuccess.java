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

package org.arrow.service.engine.concurrent.dispatch.onsuccess;

import akka.actor.ActorRef;
import akka.actor.UntypedActorContext;
import akka.dispatch.OnSuccess;
import org.arrow.model.BpmnNodeEntity;
import org.arrow.model.definition.escalation.EscalationEventDefinitionAware;
import org.arrow.model.event.boundary.impl.CompensateBoundaryEvent;
import org.arrow.model.event.endevent.EndEvent;
import org.arrow.model.gateway.impl.InclusiveGateway;
import org.arrow.model.gateway.impl.ParallelGateway;
import org.arrow.model.transition.Flow;
import org.arrow.model.transition.IncomingFlowAware;
import org.arrow.model.transition.impl.SequenceFlow;
import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.message.impl.DefaultExecuteEventMessage;
import org.arrow.runtime.message.EntityEventMessage;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ExecutionGroup;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.message.impl.EndEventMessage;
import org.arrow.runtime.logger.LoggerFacade;
import org.arrow.runtime.message.infrastructure.PersistEventMessage;
import org.arrow.service.engine.execution.ExecutionGroupEnhancer;
import org.arrow.service.engine.util.NodeUtils;

import java.util.Map;
import java.util.Set;

/**
 * {@link OnSuccess} implementation used to handle {@link org.arrow.runtime.message.EventMessage} instances after
 * execution.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class HandleNodeOnSuccess extends OnSuccess<Iterable<EventMessage>> {

    private static final LoggerFacade LOGGER = new LoggerFacade(HandleNodeOnSuccess.class);

    private final ActorRef sender;
    private final ActorRef self;
    private final EntityEventMessage msg;

    public HandleNodeOnSuccess(UntypedActorContext context, EntityEventMessage msg) {
        this.msg = msg;
        this.sender = context.sender();
        this.self = context.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSuccess(Iterable<EventMessage> events) {

        LOGGER.debug("handle node " + msg);

        final BpmnNodeEntitySpecification entity = msg.getEntity();
        final Execution execution = msg.getExecution();
        final ExecutionGroup executionGroup = msg.getExecutionGroup();

        Map<String, ExecutionGroup> map = ExecutionGroupEnhancer.enhance(execution, executionGroup);
//        Map<String, ExecutionGroup> map = new HashMap<>();

        if (!execution.isFinished()) {
            return;
        }

        if (execution.getState().compareTo(State.SUSPEND) == 0) {
            return;
        }

        if (isCompensationTask(entity)) {
            return;
        }

        // handle END message
        if (entity instanceof EndEvent) {
            sender.tell(handleEndEvent(msg), self);
        }

        // handle CONTINUE message
        else {
            Set<Flow> outgoingFlows = NodeUtils.getRelevantFlows(msg);
            for (Flow flow : outgoingFlows) {
                flow.enable();

                // enable the incoming flows
                IncomingFlowAware ifa = (IncomingFlowAware) flow.getTargetRef();
                for (Flow incomingFlow : ifa.getIncomingFlows()) {
                    if (incomingFlow.getId().equals(flow.getId())) {
                        flow.enable();
                    }
                }

                sender.tell(handleNode(flow, msg, map), self);
            }
        }

    }

    private boolean isCompensationTask(BpmnNodeEntitySpecification entity) {
        if (!(entity instanceof IncomingFlowAware)) {
            return false;
        }
        Set<? extends Flow> flows = ((IncomingFlowAware) entity).getIncomingFlows();
        return flows.stream().anyMatch(flow -> flow.getSourceRef() instanceof CompensateBoundaryEvent);
    }

    /**
     * Prepares a {@link org.arrow.runtime.message.impl.EndEventMessage} message.
     *
     * @param msg the continue message
     * @return End
     */
    private EventMessage handleEndEvent(EntityEventMessage msg) {
        EndEvent event = (EndEvent) msg.getEntity();

        ProcessInstance pi = msg.getProcessInstance();
        EventMessage eventMessage = new EndEventMessage(event, pi);

        return doPersist(msg, null) ? new PersistEventMessage(msg.getExecution(), eventMessage) : eventMessage;
    }

    /**
     * Prepares a {@link org.arrow.runtime.message.ContinueEventMessage} message.
     *
     * @param flow the flow instance
     * @param msg  the continue message
     * @return Continue
     */
    private EventMessage handleNode(Flow flow, EntityEventMessage msg, Map<String, ExecutionGroup> map) {
        final BpmnNodeEntity targetRef = flow.getTargetRef();

        EntityEventMessage event = new DefaultExecuteEventMessage(targetRef, msg.getProcessInstance(), map.get(flow.getId()));
        return doPersist(msg, flow) ? new PersistEventMessage(msg.getExecution(), event) : event;
    }

    private boolean doPersist(EntityEventMessage msg, Flow flow) {
        if (msg.getEntity() instanceof EscalationEventDefinitionAware) {
            return true;
        }
        if (flow != null && flow.getTargetRef() instanceof InclusiveGateway) {
            return true;
        }
        if (flow != null && flow.getTargetRef() instanceof ParallelGateway) {
            return true;
        }
        return false;
    }

}
