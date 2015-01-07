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
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.arrow.model.definition.terminate.TerminateEventDefinition;
import org.arrow.model.definition.terminate.TerminateEventDefinitionAware;
import org.arrow.model.event.endevent.AbstractEndEvent;
import org.arrow.model.event.endevent.EndEvent;
import org.arrow.runtime.TokenRegistry;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.infrastructure.TokenEventMessage;
import scala.concurrent.Future;

/**
 * {@link EndEvent} implementation used for BPMN 2 'Terminate' end event type.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
public class TerminateEndEvent extends AbstractEndEvent implements
		TerminateEventDefinitionAware {

	@Fetch
	@RelatedTo(type="EVENT_DEFINITION", direction=Direction.OUTGOING)
	private TerminateEventDefinition eventDefinition;

    @Override
    protected Future<Iterable<EventMessage>> executeEndEvent(Execution execution, ExecutionService service) {
        return Futures.successful(iterableOf());
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TokenEventMessage.TokenAction getTokenAction() {
		return TokenEventMessage.TokenAction.TERMINATE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TerminateEventDefinition getTerminateEventDefinition() {
		return eventDefinition;
	}

	/**
	 * Sets the terminate event definition.
	 * 
	 * @param eventDefinition
	 *            the new terminate event definition
	 */
	public void setTerminateEventDefinition(
			TerminateEventDefinition eventDefinition) {
		this.eventDefinition = eventDefinition;
	}

}
