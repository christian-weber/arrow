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

package org.arrow.model.event.startevent;

import org.springframework.data.neo4j.annotation.NodeEntity;
import org.arrow.model.definition.multiple.ParallelMultipleCapable;
import org.arrow.model.event.Event;
import org.arrow.runtime.api.StartEventSpecification;

/**
 * Specific {@link Event} definition used by process starting events.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
public interface StartEvent extends Event, StartEventSpecification, ParallelMultipleCapable {

	/**
	 * Indicates if the {@link StartEvent} is in parallel multiple mode. Only
	 * relevant if the event is of type {@code MultipleEventAware}.
	 * 
	 * @return boolean
	 */
	boolean isParallelMultiple();

	/**
	 * Indicates if the {@link StartEvent} is interrupting. Only relevant if the
	 * event is of type {@code BoundaryEvent}.
	 * 
	 * @return boolean
	 */
	boolean isInterrupting();

}
