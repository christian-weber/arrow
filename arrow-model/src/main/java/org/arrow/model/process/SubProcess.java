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

package org.arrow.model.process;

import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedToVia;
import org.arrow.model.event.boundary.BoundaryEvent;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.model.transition.Flow;
import org.arrow.model.transition.impl.SequenceFlow;
import org.arrow.model.visitor.BpmnNodeEntityVisitor;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.Messages;
import org.arrow.runtime.message.impl.StartSubProcessEventMessage;
import org.arrow.util.FutureUtil;
import scala.concurrent.Future;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * BPMN 2.0 sub process implementation.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("SubProcess")
public class SubProcess extends Process implements SubProcessEntity {

    /**
     * The completion quantity property.
     */
    private int completionQuantity;

    /**
     * The is for compensation property.
     */
    private boolean isForCompensation;

    /**
     * The start quantity property.
     */
    private int startQuantity;

    /**
     * The triggered by event property.
     * Indicates whether the sub process is a event based sub process or not.
     */
    private boolean triggeredByEvent;

    /**
     * The incoming flows.
     */
    @Fetch
    @RelatedToVia(type = "SEQUENCE_FLOW", direction = Direction.INCOMING)
    private Set<SequenceFlow> incomingFlows;

    /**
     * The outgoing flows.
     */
    @Fetch
    @RelatedToVia(type = "SEQUENCE_FLOW", direction = Direction.OUTGOING)
    private Set<SequenceFlow> outgoingFlows;

    private Set<BoundaryEvent> boundaryEvents;

    {
        incomingFlows = new HashSet<>();
        outgoingFlows = new HashSet<>();
        boundaryEvents = new HashSet<>();
    }

    /**
     * {@inheritDoc}
     */
    public Set<SequenceFlow> getIncomingFlows() {
        return incomingFlows;
    }

    /**
     * Sets the incoming flows.
     *
     * @param incomingFlows the new incoming flows
     */
    @SuppressWarnings("unused")
    public void setIncomingFlows(Set<SequenceFlow> incomingFlows) {
        this.incomingFlows = incomingFlows;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Flow> getOutgoingFlows() {
        return new HashSet<>(outgoingFlows);
    }

    /**
     * Sets the outgoing flows.
     *
     * @param outgoingFlows the new outgoing flows
     */
    @SuppressWarnings("unused")
    public void setOutgoingFlows(Set<SequenceFlow> outgoingFlows) {
        this.outgoingFlows = outgoingFlows;
    }

    /**
     * Gets the completion quantity.
     *
     * @return the completion quantity
     */
    @SuppressWarnings("unused")
    public int getCompletionQuantity() {
        return completionQuantity;
    }

    /**
     * Sets the completion quantity.
     *
     * @param completionQuantity the new completion quantity
     */
    @SuppressWarnings("unused")
    public void setCompletionQuantity(int completionQuantity) {
        this.completionQuantity = completionQuantity;
    }

    /**
     * Checks if is for compensation.
     *
     * @return true, if is for compensation
     */
    @SuppressWarnings("unused")
    public boolean isForCompensation() {
        return isForCompensation;
    }

    /**
     * Sets the for compensation.
     *
     * @param isForCompensation the new for compensation
     */
    @SuppressWarnings("unused")
    public void setForCompensation(boolean isForCompensation) {
        this.isForCompensation = isForCompensation;
    }

    /**
     * Gets the start quantity.
     *
     * @return the start quantity
     */
    @SuppressWarnings("unused")
    public int getStartQuantity() {
        return startQuantity;
    }

    /**
     * Sets the start quantity.
     *
     * @param startQuantity the new start quantity
     */
    @SuppressWarnings("unused")
    public void setStartQuantity(int startQuantity) {
        this.startQuantity = startQuantity;
    }

    /**
     * Checks if is triggered by event.
     *
     * @return true, if is triggered by event
     */
    @SuppressWarnings("unused")
    public boolean isTriggeredByEvent() {
        return triggeredByEvent;
    }

    /**
     * Sets the triggered by event.
     *
     * @param triggeredByEvent the new triggered by event
     */
    @SuppressWarnings("unused")
    public void setTriggeredByEvent(boolean triggeredByEvent) {
        this.triggeredByEvent = triggeredByEvent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addOutgoingFlow(SequenceFlow flow) {
        if (outgoingFlows == null) {
            outgoingFlows = new HashSet<>();
        }
        outgoingFlows.add(flow);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addIncomingFlow(SequenceFlow flow) {
        if (incomingFlows == null) {
            incomingFlows = new HashSet<>();
        }
        incomingFlows.add(flow);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> executeNode(Execution execution, ExecutionService service) {
        execution.setState(State.WAITING);

        if (isTriggeredByEvent()) {
            return executeEventSubProcess();
        } else {
            return executeSubProcess(execution);
        }

    }

    @SuppressWarnings("unused")
    private Future<Iterable<EventMessage>> executeEventSubProcess() {
        return FutureUtil.result();
    }

    public Future<Iterable<EventMessage>> executeSubProcess(Execution execution) {
        EventMessage msg = new StartSubProcessEventMessage(execution, getSubProcessStartEvent());
        return FutureUtil.result(msg);
    }

    /**
     * Returns the none start event of the given sub process.
     *
     * @return StartEvent
     */
    private StartEvent getSubProcessStartEvent() {
        return getStartEvents().iterator().next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> finishNode(Execution execution, ExecutionService service) {

        if (isTriggeredByEvent()) {
            return finishEventSubProcess(execution, service);
        }

        super.finishNode(execution, service);
        execution.setState(State.SUCCESS);

        return FutureUtil.result();
    }

    /**
     * Finishes the event sub process instance.
     *
     * @param execution the execution instance
     * @param service   the execution service instance
     * @return Future
     */
    private Future<Iterable<EventMessage>> finishEventSubProcess(Execution execution, ExecutionService service) {

        super.finishNode(execution, service);
        execution.setState(State.SUCCESS);

        // notify threads if process is suspended
        // and sub process was triggered by event
        // **************************************
        final ProcessInstance pi = execution.getProcessInstance();
        service.fetchEntity(pi);

        if (isTriggeredByEvent() && pi.isSuspend()) {
            EventMessage msg1 = Messages.terminateToken(execution);
            EventMessage msg2 = Messages.forceEndEvent(execution);

            return FutureUtil.result(msg1, msg2);
        }
        service.saveEntity(execution);
        Set<Execution> executions = service.data().execution().findEndEventExecutionsInWaitState(pi.getId());

        // invoke the end event
        List<EventMessage> result = executions.stream().map(Messages::finishAndContinue).collect(Collectors.toList());
        return FutureUtil.result(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<BoundaryEvent> getBoundaryEvents() {
        return boundaryEvents;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBoundaryEvents(Set<BoundaryEvent> boundaryEvents) {
        this.boundaryEvents = boundaryEvents;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public void addBoundaryEvent(BoundaryEvent event) {
        if (boundaryEvents == null) {
            boundaryEvents = new HashSet<>();
        }
        this.boundaryEvents.add(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(BpmnNodeEntityVisitor visitor) {
        visitor.visitSubProcess(this);
    }

}
