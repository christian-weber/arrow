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
import org.arrow.model.definition.signal.SignalEventDefinition;
import org.arrow.model.definition.signal.SignalEventDefinitionAware;
import org.arrow.model.event.intermediate.throwing.AbstractIntermediateThrowEvent;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import scala.concurrent.Future;

/**
 * BPMN 2.0.0 signal intermediate throw event implementation.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("SignalIntermediateThrowingEvent")
public class SignalIntermediateThrowEvent extends
		AbstractIntermediateThrowEvent implements SignalEventDefinitionAware {

	@Fetch
	@RelatedTo(type = "EVENT_DEFINITION", direction = Direction.OUTGOING)
	private SignalEventDefinition eventDefinition;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Iterable<EventMessage>> executeIntermediateEvent(final Execution execution, final ExecutionService service) {

        // get the actor system from the IoC container
        ActorSystem system = service.getBean(ActorSystem.class);

        Future<Iterable<EventMessage>> future = service.signal(eventDefinition.getSignalRef());

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

}
