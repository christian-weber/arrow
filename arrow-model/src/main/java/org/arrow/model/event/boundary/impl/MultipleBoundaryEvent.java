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

import java.util.Set;

import akka.dispatch.Futures;
import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.arrow.model.definition.EventDefinition;
import org.arrow.model.definition.multiple.MultipleEventAware;
import org.arrow.model.event.boundary.AbstractBoundaryEvent;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import scala.concurrent.Future;

/**
 * BPMN 2.0 multiple boundary event implementation.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("MultipleBoundaryEvent")
public class MultipleBoundaryEvent extends AbstractBoundaryEvent implements
		MultipleEventAware {

	/** The event definitions. */
	@Fetch
	@RelatedTo(type = "EVENT_DEFINITION", direction = Direction.OUTGOING)
	private Set<EventDefinition> eventDefinitions;

	/**
	 * Sets the event definitions.
	 * 
	 * @param eventDefinitions
	 *            the new event definitions
	 */
	public void setEventDefinitions(Set<EventDefinition> eventDefinitions) {
		this.eventDefinitions = eventDefinitions;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<EventDefinition> getEventDefinitions() {
		return eventDefinitions;
	}

	@Deprecated
	@Override
	public EventDefinition getStartedBy() {
		return null;
	}

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
	public boolean isThrowing() {
		return false;
	}

}
