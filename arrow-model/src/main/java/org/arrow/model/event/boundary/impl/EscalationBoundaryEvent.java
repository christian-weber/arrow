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

package org.arrow.model.event.boundary.impl;

import akka.dispatch.Futures;
import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.arrow.model.definition.escalation.EscalationEventDefinition;
import org.arrow.model.definition.escalation.introduction.EscalationEventHandler;
import org.arrow.model.event.boundary.AbstractBoundaryEvent;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import scala.concurrent.Future;

/**
 * BPMN 2.0 escalation boundary event implementation.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("EscalationBoundaryEvent")
public class EscalationBoundaryEvent extends AbstractBoundaryEvent implements
		EscalationEventHandler {

	@Fetch
	@RelatedTo(type = "EVENT_DEFINITION", direction = Direction.OUTGOING)
	private EscalationEventDefinition eventDefinition;

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> executeBoundaryEvent(Execution execution, ExecutionService service) {
        // mark the boundary event as finished
        execution.setState(State.WAITING);
        return Futures.successful(iterableOf());
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EscalationEventDefinition getEscalationEventDefinition() {
		return eventDefinition;
	}

	/**
	 * Sets the escalation event definition.
	 * 
	 * @param eventDefinition
	 *            the new escalation event definition
	 */
	public void setEscalationEventDefinition(EscalationEventDefinition eventDefinition) {
		this.eventDefinition = eventDefinition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Iterable<EventMessage>> handleEscalationEvent(Execution execution, ExecutionService service) {
		// mark the boundary event as finished
        execution.setState(State.SUCCESS);
		return finish(execution, service);
	}

	@Override
	public Future<Iterable<EventMessage>> finishNode(Execution execution, ExecutionService service) {
		execution.setState(State.SUCCESS);
		return super.finishNode(execution, service);
	}
}
