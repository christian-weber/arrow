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

package org.arrow.model.event.intermediate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelatedToVia;
import org.arrow.model.AbstractBpmnNodeEntity;
import org.arrow.model.transition.Flow;
import org.arrow.model.transition.impl.LinkFlow;
import org.arrow.model.transition.impl.SequenceFlow;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.service.ExecutionService;
import scala.concurrent.Future;

/**
 * Abstract {@link IntermediateEvent} implementation.
 * 
 * @author christian.weber
 * @since 1.0
 */
public abstract class AbstractIntermediateEvent extends AbstractBpmnNodeEntity
		implements IntermediateEvent {

	@Fetch
	@RelatedToVia(type = "SEQUENCE_FLOW", direction = Direction.INCOMING)
	private Set<SequenceFlow> incomingFlows;

	@Fetch
	@RelatedToVia(type = "SEQUENCE_FLOW", direction = Direction.OUTGOING)
	private Set<SequenceFlow> outgoingFlows = new HashSet<SequenceFlow>();

	@Fetch
	@RelatedToVia(type = "LINK_FLOW", direction = Direction.OUTGOING)
	private Set<LinkFlow> outgoingLinkFlows = new HashSet<>();

	public Set<SequenceFlow> getIncomingFlows() {
		return incomingFlows;
	}

	public void setIncomingFlows(Set<SequenceFlow> incomingFlows) {
		this.incomingFlows = incomingFlows;
	}

	public Set<Flow> getOutgoingFlows() {
		if (outgoingFlows == null && outgoingLinkFlows == null) {
			return Collections.emptySet();
		}
		Set<Flow> flows = new HashSet<>();
		flows.addAll(outgoingFlows);
		flows.addAll(outgoingLinkFlows);
		return flows;
	}

	public void setOutgoingFlows(Set<SequenceFlow> outgoingFlows) {
		this.outgoingFlows = outgoingFlows;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Iterable<EventMessage>> finish(Execution execution, ExecutionService service) {

		finishIntermediateEvent(execution, service);

		for (Flow flow : getOutgoingFlows()) {
//			execution.addEnabledFlowId(flow.getId());
            service.enableFlow(execution, flow);
		}

		return super.finish(execution, service);
	}

	/**
	 * Template method designed to give the {@link IntermediateEvent}
	 * implementation the ability to process logic when finishing the entity.
	 * 
	 * @param execution the execution instance
	 * @param service the execution service instance
	 */
	protected void finishIntermediateEvent(Execution execution,
			ExecutionService service) {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Future<Iterable<EventMessage>> executeNode(Execution execution, ExecutionService service) {
		return executeIntermediateEvent(execution, service);
	}

	/**
	 * Template method designed to give the {@link IntermediateEvent}
	 * implementation the ability to process logic when executing the entity.
	 *
	 * @param execution the execution instance
	 * @param service the execution service instance
	 */
	protected abstract Future<Iterable<EventMessage>> executeIntermediateEvent(Execution execution,
			ExecutionService service);

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
	public void addOutgoingFlow(SequenceFlow flow) {
		if (outgoingFlows == null) {
			outgoingFlows = new HashSet<>();
		}
		outgoingFlows.add(flow);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addOutgoingLinkFlow(LinkFlow flow) {
		if (outgoingLinkFlows == null) {
			outgoingLinkFlows = new HashSet<>();
		}
		outgoingLinkFlows.add(flow);
	}

}
