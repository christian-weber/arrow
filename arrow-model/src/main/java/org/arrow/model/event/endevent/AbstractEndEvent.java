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

package org.arrow.model.event.endevent;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelatedToVia;
import org.springframework.util.Assert;
import org.arrow.model.AbstractBpmnNodeEntity;
import org.arrow.model.transition.Flow;
import org.arrow.model.transition.impl.SequenceFlow;
import org.arrow.model.visitor.BpmnNodeEntityVisitor;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.infrastructure.TokenEventMessage;
import org.arrow.runtime.meta.ProcessMetaData;
import org.arrow.util.FutureUtil;
import scala.concurrent.Future;

import java.util.HashSet;
import java.util.Set;

import static org.arrow.util.Predicates.waitingState;
import static org.arrow.util.StreamUtils.*;

/**
 * Abstract {@link EndEvent} implementation.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public abstract class AbstractEndEvent extends AbstractBpmnNodeEntity implements EndEvent {

    private int incomingFlowsReceived;

    @Fetch
    @RelatedToVia(type = "SEQUENCE_FLOW", direction = Direction.INCOMING)
    private Set<SequenceFlow> incomingFlows = new HashSet<>();

    public Set<? extends Flow> getIncomingFlows() {
        return incomingFlows;
    }

    @SuppressWarnings("unused")
    public void setIncomingFlows(Set<SequenceFlow> incomingFlows) {
        this.incomingFlows = incomingFlows;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addIncomingFlow(SequenceFlow flow) {
        incomingFlows.add(flow);
    }

    @Override
    public Future<Iterable<EventMessage>> executeNode(Execution execution, ExecutionService service) {

        info("execute end event with id " + getId());
        incomingFlowsReceived++;

        boolean joined = join(execution);
        if (joined && (incomingFlowsReceived == getExpectedIncomingFlows())) {
            execution.setState(State.SUCCESS);
        }
        return executeEndEvent(execution, service);
    }

    /**
     * Returns the token action which is used to handle the token at the end event.
     *
     * @return TokenAction
     */
    protected TokenEventMessage.TokenAction getTokenAction() {
        return TokenEventMessage.TokenAction.CONSUME;
    }

    /**
     * Executes the end event.
     *
     * @param execution the execution instance
     * @param service   the execution service instance
     * @return Future
     */
    protected Future<Iterable<EventMessage>> executeEndEvent(Execution execution, ExecutionService service) {
        return FutureUtil.result();
    }

    private int getExpectedIncomingFlows() {
        return (int) getIncomingFlows().stream().filter(Flow::isEnabled).count();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> finish(Execution execution, ExecutionService service) {

        ProcessMetaData metaData = service.data().processMetaData().findByExecution(execution);

        // set state to WAITING if event sub process is currently running
        if (metaData != null && metaData.hasEventSubProcess()) {
            // find all event sub processes of the process
            Set<Execution> executions = service.data().execution().findAllEventSubProcessExecutions(execution.getProcessInstance().getId());
            if (anyMatch(of(executions), in(waitingState()))) {
                execution.setState(State.WAITING);
                return FutureUtil.result();
            }
        }

        // create the event message to continue with
        EventMessage msg = new TokenEventMessage(execution, getTokenAction());
        // finish the execution
        execution.setState(State.SUCCESS);
        super.finish(execution, service);
        // return the event message result
        return FutureUtil.result(msg);
    }

    private boolean join(@SuppressWarnings("unused") Execution execution) {

        Assert.notNull(getIncomingFlows(), "no incoming flows for " + getId());
        Assert.state(!getIncomingFlows().isEmpty());

        for (Flow flow : getIncomingFlows()) {

            if (!flow.isEnabled()) {
                warn("flow with id " + flow.getId() + " is disabled");
            } else if (!flow.isFinished()) {
                return false;
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(BpmnNodeEntityVisitor visitor) {
        visitor.visitEndEvent(this);
    }

}
