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
import org.arrow.model.definition.timer.TimerEventDefinition;
import org.arrow.model.definition.timer.introduction.TimerEventHandler;
import org.arrow.model.event.intermediate.catching.AbstractIntermediateCatchEvent;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.impl.TimerEventMessage;
import scala.concurrent.Future;

@NodeEntity
@TypeAlias("TimerIntermediateCatchEvent")
public class TimerIntermediateCatchEvent extends AbstractIntermediateCatchEvent
		implements TimerEventHandler {

	@Fetch
	@RelatedTo(type = "EVENT_DEFINITION", direction = Direction.OUTGOING)
	private TimerEventDefinition eventDefinition;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Iterable<EventMessage>> executeIntermediateEvent(final Execution execution, ExecutionService service) {
		execution.setState(State.WAITING);
        service.saveEntity(execution);

        return Futures.successful(iterableOf(new TimerEventMessage(execution)));
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TimerEventDefinition getTimerEventDefinition() {
		return eventDefinition;
	}

	public void setTimerEventDefinition(TimerEventDefinition definition) {
		this.eventDefinition = definition;
	}

	/**
	 * {@inheritDoc}
	 */
    @Override
    public void handleTimerEvent(Execution execution, ExecutionService service) {
        service.fetchEntity(execution);

        execution.setState(State.SUCCESS);
        finish(execution, service);
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void finishIntermediateEvent(Execution execution, ExecutionService service) {
		execution.setState(State.SUCCESS);
		super.finishIntermediateEvent(execution, service);
	}
}
