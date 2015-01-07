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

package org.arrow.model.event.startevent;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelatedToVia;
import org.arrow.model.AbstractBpmnNodeEntity;
import org.arrow.model.transition.Flow;
import org.arrow.model.transition.OutgoingFlowAware;
import org.arrow.model.transition.impl.SequenceFlow;
import org.arrow.model.visitor.BpmnNodeEntityVisitor;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.service.ExecutionService;
import scala.concurrent.Future;

/**
 * Abstract BPMN 2.0 {@link StartEvent} implementation.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public abstract class AbstractStartEvent extends AbstractBpmnNodeEntity
		implements StartEvent, OutgoingFlowAware {

	/** The outgoing flows. */
	@Fetch
	@RelatedToVia(type = "SEQUENCE_FLOW", direction = Direction.OUTGOING)
	private Set<SequenceFlow> outgoingFlows = new HashSet<>();

	/** The parallel multiple. */
	private boolean parallelMultiple;

	/** The is interrupting. */
	private boolean isInterrupting;

	/**
	 * {@inheritDoc}
	 */
	public Set<Flow> getOutgoingFlows() {
		return new HashSet<Flow>(outgoingFlows);
	}

	/**
	 * Sets the outgoing flows.
	 * 
	 * @param outgoingFlows
	 *            the new outgoing flows
	 */
    @SuppressWarnings("unused")
	public void setOutgoingFlows(Set<SequenceFlow> outgoingFlows) {
		this.outgoingFlows = outgoingFlows;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addOutgoingFlow(SequenceFlow flow) {
		outgoingFlows.add((SequenceFlow) flow);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Iterable<EventMessage>> finish(Execution execution, ExecutionService service) {
		finishStartEvent(execution, service);
		return super.finish(execution, service);
	}

	/**
	 * Template method designed to give the {@link StartEvent} implementation
	 * the ability to process logic when finishing the entity.
	 * 
	 * @param execution
	 *            the execution
	 * @param service
	 *            the service
	 */
    @SuppressWarnings("unused")
	protected void finishStartEvent(Execution execution,
			ExecutionService service) {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Iterable<EventMessage>> executeNode(Execution execution, ExecutionService service) {
		return executeStartEvent(execution, service);
	}

	/**
	 * Template method designed to give the {@link StartEvent} implementation
	 * the ability to process logic when executing the entity.
	 * 
	 * @param execution
	 *            the execution
	 * @param service
	 *            the service
	 */
	protected abstract Future<Iterable<EventMessage>> executeStartEvent(Execution execution,
			ExecutionService service);

	/**
	 * {@inheritDoc}
	 */
	public boolean isParallelMultiple() {
		return parallelMultiple;
	}

	/**
	 * Sets the parallel multiple.
	 * 
	 * @param parallelMultiple
	 *            the new parallel multiple
	 */
	public void setParallelMultiple(boolean parallelMultiple) {
		this.parallelMultiple = parallelMultiple;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isInterrupting() {
		return isInterrupting;
	}

	/**
	 * Sets the interrupting.
	 * 
	 * @param isInterrupting
	 *            the new interrupting
	 */
	public void setInterrupting(boolean isInterrupting) {
		this.isInterrupting = isInterrupting;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(BpmnNodeEntityVisitor visitor) {
		visitor.visitStartEvent(this);
	}

}
