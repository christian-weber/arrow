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

package org.arrow.model.event.boundary.impl;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.arrow.model.definition.timer.TimerEventDefinition;
import org.arrow.model.definition.timer.introduction.TimerEventHandler;
import org.arrow.model.event.boundary.AbstractBoundaryEvent;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.EventMessage;
import org.arrow.util.FutureUtil;
import scala.concurrent.Future;

/**
 * BPMN 2.0 timer boundary event implementation.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
public class TimerBoundaryEvent extends AbstractBoundaryEvent implements TimerEventHandler {

	@Fetch
	@RelatedTo(type = "EVENT_DEFINITION", direction = Direction.OUTGOING)
	private TimerEventDefinition eventDefinition;

	private boolean interrupting;

	/**
	 * Indicates if the boundary event is interrupting.
	 * 
	 * @return boolean
	 */
    @SuppressWarnings("unused")
	public boolean isInterrupting() {
		return interrupting;
	}

	/**
	 * Sets the interrupting property.
	 * 
	 * @param interrupting the interrupting boolean flag
	 */
    @SuppressWarnings("unused")
	public void setInterrupting(boolean interrupting) {
		this.interrupting = interrupting;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TimerEventDefinition getTimerEventDefinition() {
		return eventDefinition;
	}

	/**
	 * Sets the {@link TimerEventDefinition} instance.
	 * 
	 * @param definition the timer event definition instance
	 */
	public void setTimerEventDefinition(TimerEventDefinition definition) {
		this.eventDefinition = definition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public Future<Iterable<EventMessage>> executeBoundaryEvent(Execution execution, ExecutionService service) {
		execution.setState(State.WAITING);
        return FutureUtil.result();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleTimerEvent(Execution execution, ExecutionService service) {
		execution.setState(State.SUCCESS);
		finish(execution, service);
	}

}
