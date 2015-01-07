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

package org.arrow.model.event.startevent.impl;

import akka.dispatch.Futures;
import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.arrow.model.definition.signal.SignalEventDefinition;
import org.arrow.model.definition.signal.introduction.SignalEventHandler;
import org.arrow.model.event.endevent.EndEvent;
import org.arrow.model.event.startevent.AbstractStartEvent;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import scala.concurrent.Future;

/**
 * {@link EndEvent} implementation used for BPMN 2 'Signal' start event type.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("SignalStartEvent")
public class SignalStartEvent extends AbstractStartEvent implements
		SignalEventHandler {

	/** The event definition. */
	@Fetch
	@RelatedTo(type = "EVENT_DEFINITION", direction = Direction.OUTGOING)
	private SignalEventDefinition eventDefinition;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Iterable<EventMessage>> executeStartEvent(Execution execution, ExecutionService service) {
		// mark the event as finished
		execution.setState(State.SUCCESS);
		finish(execution, service);
        return Futures.successful(iterableOf());
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SignalEventDefinition getSignalEventDefinition() {
		return eventDefinition;
	}

	/**
	 * Sets the signal event definition.
	 * 
	 * @param eventDefinition
	 *            the new signal event definition
	 */
	public void setSignalEventDefinition(SignalEventDefinition eventDefinition) {
		this.eventDefinition = eventDefinition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Iterable<EventMessage>> handleSignalEvent(Execution execution, ExecutionService service) {
		execution.setState(State.SUCCESS);
		execute(execution, service);

		return Futures.successful(iterableOf());
	}

}
