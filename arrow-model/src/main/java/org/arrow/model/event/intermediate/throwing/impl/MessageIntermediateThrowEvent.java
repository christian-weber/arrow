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

package org.arrow.model.event.intermediate.throwing.impl;

import akka.actor.ActorSystem;
import akka.dispatch.OnSuccess;
import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.arrow.model.definition.message.MessageEventDefinition;
import org.arrow.model.definition.message.MessageEventDefinitionAware;
import org.arrow.model.event.intermediate.throwing.AbstractIntermediateThrowEvent;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import scala.concurrent.Future;

/**
 * BPMN 2.0 message intermediate throw event implememtation.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("MessageIntermediateThrowEvent")
public class MessageIntermediateThrowEvent extends
		AbstractIntermediateThrowEvent implements MessageEventDefinitionAware {

	@Fetch
	@RelatedTo(type = "EVENT_DEFINITION", direction = Direction.OUTGOING)
	private MessageEventDefinition eventDefinition;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.arrow.modelBpmnNodeEntity#execute(org.springframework
	 * .workflow.runtime.Execution,
	 * org.arrow.service.facade.ExecutionService)
	 */
	@Override
	public Future<Iterable<EventMessage>> executeIntermediateEvent(final Execution execution, final ExecutionService service) {

        ActorSystem system = service.getBean(ActorSystem.class);

        Future<Iterable<EventMessage>> future = service.publishMessageEvent(eventDefinition.getMessageRef());

        future.onSuccess(new OnSuccess<Iterable<EventMessage>>() {
            @Override
            public void onSuccess(Iterable<EventMessage> eventMessages) throws Throwable {
                // marks the intermediate event as finished
                execution.setState(State.SUCCESS);
                finish(execution, service);
            }
        }, system.dispatcher());

        return future;
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
