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

package org.arrow.model.gateway.impl;

import akka.dispatch.Futures;
import org.arrow.model.gateway.AbstractGateway;
import org.arrow.model.transition.OutgoingFlowAware;
import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.service.ExecutionDataService.SynchronisationResult;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.impl.DefaultContinueEventMessage;
import org.arrow.runtime.message.infrastructure.TokenEventMessage;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.util.Assert;
import scala.concurrent.Future;

import java.util.*;

import static org.arrow.runtime.execution.State.*;
import static org.arrow.runtime.message.infrastructure.TokenEventMessage.TokenAction.CONSUME;

/**
 * BPMN 2.0 event based gateway.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
public class EventBasedGateway extends AbstractGateway {

    /**
     * The type.
     */
    private EventBasedGatewayType eventGatewayType;

    /**
     * Sets the event gateway type.
     *
     * @param eventGatewayType the new event gateway type
     */
    public void setEventGatewayType(EventBasedGatewayType eventGatewayType) {
        this.eventGatewayType = eventGatewayType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventMessage> fork(Execution execution, ExecutionService service) {
        execution.setState(RUNNING);
        getOutgoingFlows().stream().forEach(flow -> flow.enableRelation(execution));

        return new ArrayList<>();
    }

    /**
     * Handles the received event. If type is 'EXCLUSIVE' continue the process
     * flow. When type is 'PARALLEL' verify if all monitored events are received
     * and continue the process flow when.
     *
     * @param execution the execution instance
     * @param service   the execution service instance
     */
    @Override
    public Future<Iterable<EventMessage>> finish(Execution execution, ExecutionService service) {

        Assert.notNull(eventGatewayType, "event gateway type must not be null");

        switch (eventGatewayType) {

            // handle exclusive event based gateway
            // ====================================
            case Exclusive:
                return Futures.successful(handleExclusiveType(service));

            // handle parallel event based gateway
            // ===================================
            case Parallel:
                return Futures.successful(handleParallelType(execution, service));

            default:
                return Futures.successful(iterableOf());
        }

    }

    /**
     * Handles the exclusive gateway type mode.
     *
     * @param service   the execution service instance
     * @return Iterable
     */
    private Iterable<EventMessage> handleExclusiveType(ExecutionService service) {

        Set<Execution> executions1 = service.data().followingExecutions(this, WAITING);
        Set<Execution> executions2 = service.data().followingExecutions(this, SUCCESS);

        List<EventMessage> messages = new ArrayList<>();
        Map<String, Execution> ids = new HashMap<>();

        // suspend all other waiting events
        for (Execution exec : executions1) {
            ids.put(exec.getId(), exec);

            exec.setState(SUSPEND);
            service.saveEntity(exec);
        }

        for (String str : ids.keySet()) {
            messages.add(new TokenEventMessage(ids.get(str), CONSUME));
        }


        for (Execution exec : executions2) {
            enableFlows(exec);
            messages.add(new DefaultContinueEventMessage(exec));
        }

        return messages;
    }

    /**
     * Handles the exclusive gateway type mode.
     *
     * @param execution the execution instance
     * @param service   the execution service instance
     * @return Iterable
     */
    private Iterable<EventMessage> handleParallelType(Execution execution, ExecutionService service) {

        Set<Execution> executions = service.findFollowingByState(execution, SUCCESS);

        int expected = getOutgoingFlows().size();
        int handled = executions.size();

        List<EventMessage> messages = new ArrayList<>();

        if (expected == handled) {

            // continue with all events
            for (Execution exec : executions) {
                enableFlows(exec);
                messages.add(new DefaultContinueEventMessage(exec));
            }

        }

        return messages;
    }

    /**
     * Enables all outgoing flows of the given execution instance.
     *
     * @param execution the execution instance
     */
    private void enableFlows(Execution execution) {
        BpmnNodeEntitySpecification entity = execution.getEntity();

        if (entity instanceof OutgoingFlowAware) {
            OutgoingFlowAware ofa = (OutgoingFlowAware) entity;
            ofa.getOutgoingFlows().stream().forEach(flow -> flow.enableRelation(execution));
        }
    }

    public JoinResult join(Execution execution, ExecutionService service) {
        SynchronisationResult result = service.data().breadthFirstSynchronization(this);

        if (result.isSynchronised()) {
            return new JoinResult(true);
        }

        // avoid gateway finish
        EventMessage tokenMsg = new TokenEventMessage(execution, CONSUME);
        return new JoinResult(false, tokenMsg);
    }

    /**
     * Event based gateway type enumeration.
     *
     * @author christian.weber
     * @since 1.0.0
     */
    public enum EventBasedGatewayType {

        /**
         * The exclusive.
         */
        Exclusive,
        /**
         * The parallel.
         */
        Parallel
    }

}
