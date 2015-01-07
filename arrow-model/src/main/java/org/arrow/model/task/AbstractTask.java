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

package org.arrow.model.task;

import akka.dispatch.Futures;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.annotation.RelatedToVia;
import org.springframework.util.Assert;
import org.arrow.model.AbstractBpmnNodeEntity;
import org.arrow.model.event.boundary.BoundaryEvent;
import org.arrow.model.task.multi.MultiInstanceLoopCharacteristics;
import org.arrow.model.transition.Flow;
import org.arrow.model.transition.impl.Association;
import org.arrow.model.transition.impl.SequenceFlow;
import org.arrow.model.visitor.BpmnNodeEntityVisitor;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.util.IterableUtils;
import scala.concurrent.Future;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract BPMN 2.0 task implementation.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public abstract class AbstractTask extends AbstractBpmnNodeEntity implements Task {

    @Fetch
    @RelatedTo(type = "BOUNDARY_EVENT", direction = Direction.OUTGOING)
    private Set<BoundaryEvent> boundaryEvents = new HashSet<>();

    @Fetch
    @RelatedToVia(type = "SEQUENCE_FLOW", direction = Direction.INCOMING)
    private Set<SequenceFlow> incomingFlows = new HashSet<>();

    @Fetch
    @RelatedToVia(type = "SEQUENCE_FLOW", direction = Direction.OUTGOING)
    private Set<SequenceFlow> outgoingFlows = new HashSet<>();

    @Fetch
    @RelatedToVia(type = "ASSOCIATION", direction = Direction.INCOMING)
    private Set<Association> associations;

    @Fetch
    @RelatedTo(type = "MULTI_INSTANCE_LOOP_CHARACTERISTICS", direction = Direction.OUTGOING)
    private MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics;


    private boolean isForCompensation;

    public Set<? extends Flow> getIncomingFlows() {
        Set<Flow> flows = new HashSet<>(emptyIfNull(incomingFlows));
        flows.addAll(emptyIfNull(associations));
        return flows;
    }

    @SuppressWarnings("unused")
    public void setIncomingFlows(Set<SequenceFlow> incomingFlows) {
        this.incomingFlows = incomingFlows;
    }

    /**
     * Returns the outgoing flows.
     *
     * @return Set
     */
    public Set<Flow> getOutgoingFlows() {
        Set<Flow> flows = new HashSet<>(outgoingFlows);
        flows.addAll(associations);

        return flows;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> finish(Execution execution, ExecutionService service) {
        execution.setState(State.SUCCESS);
        for (Flow flow : getOutgoingFlows()) {
//            execution.addEnabledFlowId(flow.getId());
            service.enableFlow(execution, flow);
        }
        return super.finish(execution, service);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addIncomingFlow(Association flow) {
        if (associations == null) {
            associations = new HashSet<>();
        }
        associations.add(flow);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void addIncomingFlow(SequenceFlow flow) {
        outgoingFlows.add(flow);
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

    public MultiInstanceLoopCharacteristics getMultiInstanceLoopCharacteristics() {
        return multiInstanceLoopCharacteristics;
    }

    @SuppressWarnings("unused")
    public void setMultiInstanceLoopCharacteristics(
            MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics) {
        this.multiInstanceLoopCharacteristics = multiInstanceLoopCharacteristics;
    }

    @Override
    public Future<Iterable<EventMessage>> executeNode(Execution execution, ExecutionService service) {

        boolean joined = join();

        if (joined) {
            info("execute task " + getId());
            return executeTask(execution, service);
        }

        return Futures.successful(iterableOf());
    }

    public abstract Future<Iterable<EventMessage>> executeTask(Execution execution, ExecutionService service);

    private boolean join() {

        Assert.notNull(getIncomingFlows(), "no incoming flows for " + getId());
//        Assert.state(!getIncomingFlows().isEmpty());

        for (Flow flow : IterableUtils.emptyIfNull(getIncomingFlows())) {

            if (!flow.isEnabled()) {
                warn("flow with id " + flow.getId() + " is disabled");
            }

            if (flow.isEnabled() && !flow.isFinished()) {
                return false;
            }
        }
        return true;
    }

    public Set<BoundaryEvent> getBoundaryEvents() {
        return boundaryEvents;
    }

    public void setBoundaryEvents(Set<BoundaryEvent> boundaryEvents) {
        this.boundaryEvents = boundaryEvents;
    }

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
        visitor.visitTask(this);
    }

    public boolean isForCompensation() {
        return isForCompensation;
    }

    public void setForCompensation(boolean isForCompensation) {
        this.isForCompensation = isForCompensation;
    }
}
