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

package org.arrow.model.event.boundary;

import akka.dispatch.Futures;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.annotation.RelatedToVia;
import org.arrow.model.AbstractBpmnNodeEntity;
import org.arrow.model.BpmnNodeEntity;
import org.arrow.model.event.endevent.EndEvent;
import org.arrow.model.transition.Flow;
import org.arrow.model.transition.OutgoingFlowAware;
import org.arrow.model.transition.impl.Association;
import org.arrow.model.transition.impl.SequenceFlow;
import org.arrow.model.visitor.BpmnNodeEntityVisitor;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.AbstractCancelAwareEventMessage;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.impl.CancelAwareFinishEventMessage;
import org.arrow.runtime.message.impl.DefaultContinueEventMessage;
import org.arrow.runtime.message.impl.DefaultFinishEventMessage;
import org.arrow.runtime.message.impl.ErrorEventMessage;
import org.arrow.runtime.message.infrastructure.TokenEventMessage;
import static org.arrow.runtime.message.infrastructure.TokenEventMessage.TokenAction.*;
import scala.concurrent.Future;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract BPMN {@link EndEvent} implementation.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public abstract class AbstractBoundaryEvent extends AbstractBpmnNodeEntity
		implements BoundaryEvent, OutgoingFlowAware {

	/** The attached to ref. */
	@Fetch
	@RelatedTo(type = "BOUNDARY_EVENT", direction = Direction.INCOMING)
	private BpmnNodeEntity attachedToRef;

	/** The outgoing flows. */
	@Fetch
	@RelatedToVia(type = "SEQUENCE_FLOW", direction = Direction.OUTGOING)
	private Set<SequenceFlow> outgoingFlows = new HashSet<>();

	/** The association flows. */
	@Fetch
	@RelatedToVia(type = "ASSOCIATION", direction = Direction.OUTGOING)
	private Set<Association> associations = new HashSet<>();

	/** The cancel activity. */
	private boolean cancelActivity;

	/** The parallel multiple. */
	private boolean parallelMultiple;

	/**
	 * Checks if is cancel activity.
	 * 
	 * @return true, if is cancel activity
	 */
	public boolean isCancelActivity() {
		return cancelActivity;
	}

	/**
	 * Sets the cancel activity.
	 * 
	 * @param cancelActivity
	 *            the new cancel activity
	 */
	public void setCancelActivity(boolean cancelActivity) {
		this.cancelActivity = cancelActivity;
	}

	/**
	 * Checks if is parallel multiple.
	 * 
	 * @return true, if is parallel multiple
	 */
    @SuppressWarnings("unused")
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
	public Set<Flow> getOutgoingFlows() {
		Set<Flow> flows = new HashSet<>(outgoingFlows);
		flows.addAll(associations);
		return flows;
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
		outgoingFlows.add(flow);
	}

	@Override
	public void addOutgoingFlow(Association flow) {
		associations.add(flow);
	}

	/**
	 * {@inheritDoc}
	 */
	public BpmnNodeEntity getAttachedToRef() {
		return attachedToRef;
	}

	/**
	 * Sets the attached to ref.
	 * 
	 * @param attachedToRef
	 *            the new attached to ref
	 */
	public void setAttachedToRef(BpmnNodeEntity attachedToRef) {
		this.attachedToRef = attachedToRef;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Iterable<EventMessage>> finishNode(Execution execution, ExecutionService service) {

		execution.setState(State.RUNNING);
		service.saveEntity(execution);

		BpmnNodeEntity attachedToRef = getAttachedToRef();

        // cancel the task execution if property 'cancelActivity' is true
		// and if task execution is running, sub process entities will
		// not be interrupted
		if (isCancelActivity()) {
			// set EXECUTION to 'SUSPEND'
			Execution target = service.data().execution().findByEntity(attachedToRef, execution.getProcessInstance().getId());
			if (target != null) {
				target.setState(State.SUSPEND);
				service.saveEntity(target);
			}
		}

		// enable the outgoing flows
		for (Flow flow : getOutgoingFlows()) {
			service.enableFlow(execution, flow);
		}

		// error case strategy
		// produce a token and continue
		if (isErrorEvent(execution)) {
//			EventMessage msg = new TokenEventMessage(execution, PRODUCE);

			execution.setState(State.SUCCESS);
			return Futures.successful(iterableOf());
		}

		// produce token if property 'cancelActivity' is false and therefore
		// a fork is triggered
		else if (!isCancelActivity() && isCancelAwareMessage(execution)) {
			AbstractCancelAwareEventMessage message = (AbstractCancelAwareEventMessage) execution.getCurrentEventMessage();
			Execution target = message.getCancelTarget();

			info("produce token due to non interrupting boundary event");
			EventMessage msg1 = new TokenEventMessage(execution, PRODUCE);
			EventMessage msg2 = new DefaultFinishEventMessage(target);
			EventMessage msg3 = new DefaultContinueEventMessage(target);

			execution.setState(State.SUCCESS);
			return Futures.successful(iterableOf(msg1, msg2, msg3));
		} else if (!isCancelActivity()) {
			EventMessage msg1 = new TokenEventMessage(execution, PRODUCE);

			execution.setState(State.SUCCESS);
			return Futures.successful(iterableOf(msg1));
		}

		execution.setState(State.SUCCESS);
        return Futures.successful(iterableOf());
    }

	private boolean isErrorEvent(Execution execution) {
		return execution.getCurrentEventMessage() instanceof ErrorEventMessage;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(BpmnNodeEntityVisitor visitor) {
		visitor.visitBoundaryEvent(this);
	}

    @Override
    protected Future<Iterable<EventMessage>> executeNode(Execution execution, ExecutionService service) {
        return executeBoundaryEvent(execution, service);
    }

    protected abstract Future<Iterable<EventMessage>> executeBoundaryEvent(Execution execution, ExecutionService service);

	private boolean isCancelAwareMessage(Execution execution) {
		return execution.getCurrentEventMessage() instanceof AbstractCancelAwareEventMessage;
	}

	public Set<Association> getAssociations() {
		return associations;
	}

	public void setAssociations(Set<Association> associations) {
		this.associations = associations;
	}
}
