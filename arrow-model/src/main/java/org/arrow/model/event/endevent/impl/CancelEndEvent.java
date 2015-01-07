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

import akka.dispatch.Futures;
import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.arrow.model.definition.cancel.CancelEventDefinition;
import org.arrow.model.definition.cancel.CancelEventDefinitionAware;
import org.arrow.model.event.endevent.AbstractEndEvent;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.impl.CancelEventMessage;
import org.arrow.runtime.message.infrastructure.PersistEventMessage;
import scala.concurrent.Future;

/**
 * {@link org.arrow.model.event.endevent.EndEvent} implementation used for BPMN 2 'Compensate' end event type.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("CancelEndEvent")
public class CancelEndEvent extends AbstractEndEvent implements
        CancelEventDefinitionAware {

	@Fetch
	@RelatedTo(type="EVENT_DEFINITION", direction=Direction.OUTGOING)
	private CancelEventDefinition eventDefinition;

    /**
     * {@inheritDoc}
     */
	@Override
	public Future<Iterable<EventMessage>> executeEndEvent(Execution execution,
			ExecutionService service) {

        execution.setState(State.WAITING);
//        finish(execution, service);

        EventMessage message = new PersistEventMessage(execution, new CancelEventMessage(execution));
        return Futures.successful(iterableOf(message));
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CancelEventDefinition getCancelEventDefinition() {
		return eventDefinition;
	}

	/**
	 * Sets the message event definition.
	 * 
	 * @param eventDefinition
	 *            the new message event definition
	 */
	public void setCancelEventDefinition(CancelEventDefinition eventDefinition) {
		this.eventDefinition = eventDefinition;
	}

}
