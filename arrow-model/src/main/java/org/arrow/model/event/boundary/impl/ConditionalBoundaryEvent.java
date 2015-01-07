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
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.arrow.model.definition.conditional.ConditionalEventDefinition;
import org.arrow.model.definition.conditional.introduction.ConditionalEventHandler;
import org.arrow.model.event.boundary.AbstractBoundaryEvent;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import scala.concurrent.Future;

/**
 * BPMN 2.0 conditional boundary event implementation.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
public class ConditionalBoundaryEvent extends AbstractBoundaryEvent implements
		ConditionalEventHandler {

	@RelatedTo(type = "EVENT_DEFINITION", direction = Direction.OUTGOING)
	private ConditionalEventDefinition eventDefinition;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConditionalEventDefinition getConditionalEventDefinition() {
		return eventDefinition;
	}

	/**
	 * Sets the {@link ConditionalEventDefinition} instance.
	 * 
	 * @param eventDefinition the conditional event definition
	 */
	public void setConditionalEventDefinition(
			ConditionalEventDefinition eventDefinition) {
		this.eventDefinition = eventDefinition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Iterable<EventMessage>> executeBoundaryEvent(Execution execution, ExecutionService service) {
		execution.setState(State.WAITING);
        return Futures.successful(iterableOf());
    }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleConditionalEvent(Execution execution,
			ExecutionService service) {

		// mark the boundary event as finished
		execution.setState(State.SUCCESS);
		finish(execution, service);
	}

}
