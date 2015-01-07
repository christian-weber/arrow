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

import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.arrow.model.definition.message.MessageEventDefinition;
import org.arrow.model.definition.message.MessageEventDefinitionAware;
import org.arrow.model.event.endevent.AbstractEndEvent;
import org.arrow.model.event.endevent.EndEvent;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import scala.concurrent.Future;

/**
 * {@link EndEvent} implementation used for BPMN 2 'Message' end event type.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("MessageEndEvent")
public class MessageEndEvent extends AbstractEndEvent implements
		MessageEventDefinitionAware {

	@Fetch
	@RelatedTo(type="EVENT_DEFINITION", direction=Direction.OUTGOING)
	private MessageEventDefinition eventDefinition;

    /**
     * {@inheritDoc}
     */
	@Override
	public Future<Iterable<EventMessage>> executeEndEvent(Execution execution,
			ExecutionService service) {

        execution.setState(State.SUCCESS);
        finish(execution, service);
        return service.publishMessageEvent(eventDefinition.getMessageRef());
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MessageEventDefinition getMessageEventDefinition() {
		return eventDefinition;
	}

	/**
	 * Sets the message event definition.
	 * 
	 * @param eventDefinition
	 *            the new message event definition
	 */
	public void setMessageEventDefinition(MessageEventDefinition eventDefinition) {
		this.eventDefinition = eventDefinition;
	}

}
