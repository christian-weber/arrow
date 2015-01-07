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
import org.arrow.model.definition.error.ErrorEventDefinition;
import org.arrow.model.definition.error.ErrorEventDefinitionAware;
import org.arrow.model.event.endevent.AbstractEndEvent;
import org.arrow.model.event.endevent.EndEvent;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.service.ExecutionService;
import scala.concurrent.Future;

/**
 * {@link EndEvent} implementation used for BPMN 2 'Error' end event type.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
public class ErrorEndEvent extends AbstractEndEvent implements
		ErrorEventDefinitionAware {

	@Fetch
	@RelatedTo(type="EVENT_DEFINITION", direction=Direction.OUTGOING)
	private ErrorEventDefinition eventDefinition;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ErrorEventDefinition getErrorEventDefinition() {
		return eventDefinition;
	}

	/**
	 * Sets the error event definition.
	 * 
	 * @param eventDefinition
	 *            the new error event definition
	 */
	public void setErrorEventDefinition(ErrorEventDefinition eventDefinition) {
		this.eventDefinition = eventDefinition;
	}

    @Override
    protected Future<Iterable<EventMessage>> executeEndEvent(Execution execution, ExecutionService service) {
        return Futures.successful(iterableOf());
    }

}
