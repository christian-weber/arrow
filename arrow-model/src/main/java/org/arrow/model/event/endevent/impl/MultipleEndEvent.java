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

package org.arrow.model.event.endevent.impl;

import java.util.Set;

import akka.dispatch.Futures;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.arrow.model.definition.EventDefinition;
import org.arrow.model.definition.multiple.MultipleEventAware;
import org.arrow.model.event.endevent.AbstractEndEvent;
import org.arrow.model.event.endevent.EndEvent;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.service.ExecutionService;
import scala.concurrent.Future;

/**
 * {@link EndEvent} implementation used for BPMN 2 'Signal' end event type.
 * 
 * @author christian.weber
 * @since 1.0
 */
@NodeEntity
public class MultipleEndEvent extends AbstractEndEvent implements
		MultipleEventAware {

	@Fetch
	@RelatedTo(type = "EVENT_DEFINITION", direction = Direction.OUTGOING)
	private Set<EventDefinition> eventDefinitions;

	@Override
	public Future<Iterable<EventMessage>> executeEndEvent(Execution execution,
			ExecutionService service) {
        return Futures.successful(iterableOf());
    }

	/**
	 * Sets the event definitions.
	 * 
	 * @param eventDefinitions the event definitions
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

	@Override
	public EventDefinition getStartedBy() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isThrowing() {
		return true;
	}

}
