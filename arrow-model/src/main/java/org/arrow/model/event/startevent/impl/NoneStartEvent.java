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

package org.arrow.model.event.startevent.impl;

import akka.dispatch.Futures;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.arrow.model.event.startevent.AbstractStartEvent;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import scala.concurrent.Future;

/**
 * BPMN 2.0 none start event implementation.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
public class NoneStartEvent extends AbstractStartEvent {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Iterable<EventMessage>> executeStartEvent(Execution execution, ExecutionService service) {
		// mark the event as finished
		execution.setState(State.SUCCESS);
		finish(execution, service);
        return Futures.successful(iterableOf());
    }

}
