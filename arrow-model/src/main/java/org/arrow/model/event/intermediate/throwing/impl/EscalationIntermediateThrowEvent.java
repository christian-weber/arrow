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
import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.arrow.model.definition.escalation.EscalationEventDefinition;
import org.arrow.model.definition.escalation.EscalationEventDefinitionAware;
import org.arrow.model.event.intermediate.throwing.AbstractIntermediateThrowEvent;
import org.arrow.model.process.event.Escalation;
import org.arrow.util.FutureUtil;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.Future;

/**
 * BPMN 2.0 escalation intermediate throw event implememtation.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("EscalationIntermediateThrowEvent")
public class EscalationIntermediateThrowEvent extends AbstractIntermediateThrowEvent
        implements EscalationEventDefinitionAware {

	@Fetch
	@RelatedTo(type = "EVENT_DEFINITION", direction = Direction.OUTGOING)
	private EscalationEventDefinition eventDefinition;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Iterable<EventMessage>> executeIntermediateEvent(Execution execution, ExecutionService service) {

		ActorSystem actorSystem = service.getBean(ActorSystem.class);
		ExecutionContextExecutor ec = actorSystem.dispatcher();

		final Escalation escalation = eventDefinition.getEscalationRef();
		final String escalationCode = escalation.getEscalationCode();

		// marks the intermediate event as finished
		execution.setState(State.WAITING);

		// prepare future result
		Future<Iterable<EventMessage>> future1 = finish(execution, service);
		Future<Iterable<EventMessage>> future2 = service.publishEscalationEvent(escalationCode, execution);
		return FutureUtil.sequenceResult(ec, future1, future2);
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EscalationEventDefinition getEscalationEventDefinition() {
		return eventDefinition;
	}

	/**
	 * Sets the escalation event definition.
	 * 
	 * @param eventDefinition
	 *            the new escalation event definition
	 */
	public void setEscalationEventDefinition(
			EscalationEventDefinition eventDefinition) {
		this.eventDefinition = eventDefinition;
	}

	@Override
	protected void finishIntermediateEvent(Execution execution, ExecutionService service) {
		execution.setState(State.SUCCESS);
		super.finishIntermediateEvent(execution, service);
	}
}
