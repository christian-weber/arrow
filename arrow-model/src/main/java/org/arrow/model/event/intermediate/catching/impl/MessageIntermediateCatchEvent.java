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

package org.arrow.model.event.intermediate.catching.impl;

import akka.dispatch.Futures;
import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.arrow.model.definition.message.MessageEventDefinition;
import org.arrow.model.definition.message.introduction.MessageEventHandler;
import org.arrow.model.event.intermediate.catching.AbstractIntermediateCatchEvent;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import scala.concurrent.Future;

/**
 * BPMN 2.0 message intermediate catch event implementation.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("MessageIntermediateCatchEvent")
public class MessageIntermediateCatchEvent extends
		AbstractIntermediateCatchEvent implements MessageEventHandler {

	@Fetch
	@RelatedTo(type = "EVENT_DEFINITION", direction = Direction.OUTGOING)
	private MessageEventDefinition eventDefinition;

    /**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Iterable<EventMessage>> executeIntermediateEvent(Execution execution, ExecutionService service) {
		
		if (execution.getState() != null) {
			info(execution.getEntity().getId() + " already executed, skip");
            return Futures.successful(iterableOf());
		}
		
		// avoid to finish the intermediate event
		execution.setState(State.WAITING);

        return Futures.successful(iterableOf());
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Iterable<EventMessage>> handleMessageEvent(Execution execution, ExecutionService service) {
		// mark the intermediate event as finished
		execution.setState(State.SUCCESS);
		return finish(execution, service);
	}

	@Override
	protected void finishIntermediateEvent(Execution execution, ExecutionService service) {
		execution.setState(State.SUCCESS);
		super.finishIntermediateEvent(execution, service);
	}
}
