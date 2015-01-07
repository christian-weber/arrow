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

package org.arrow.runtime.execution;

import org.springframework.util.Assert;
import org.arrow.model.definition.EventDefinition;
import org.arrow.model.definition.multiple.MultipleEventAware;
import org.arrow.model.definition.multiple.ParallelMultipleCapable;
import org.arrow.runtime.logger.LoggerFacade;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link Execution} implementation for multiple event executions.
 * 
 * @author christian.weber
 * @since 1.0.0
 * 
 * @see MultipleEventAware
 */
public class MultipleEventExecution extends Execution {

    private static LoggerFacade LOGGER = new LoggerFacade(MultipleEventExecution.class);

	/** The received event ids. */
	private Set<String> receivedEventIds;

	/**
	 * Gets the received event ids.
	 * 
	 * @return the received event ids
	 */
    @SuppressWarnings("unused")
	public Set<String> getReceivedEventIds() {
		return receivedEventIds;
	}

	/**
	 * Adds the given received event id.
	 * 
	 * @param eventId the event id value
	 */
	public void addReceivedEventId(String eventId) {

        Assert.notNull(eventId);
        if (receivedEventIds == null) {
            receivedEventIds = Collections.synchronizedSet(new HashSet<>());
        }

        receivedEventIds.add(eventId);
		evaluateExecutionState();
	}

	/**
	 * Evaluates the execution state by verifying the received event IDs.
	 */
	private void evaluateExecutionState() {

		// multiple events
		if (isMultipleNonParallel()) {
			setState(State.SUCCESS);
		}

		// multiple events
		else if (receivedEventIds.containsAll(getExpectedEvendIds())) {
			setState(State.SUCCESS);
		}

	}

	/**
	 * Returns a set of all expected event definition IDs.
	 * 
	 * @return Set
	 */
	private Set<String> getExpectedEvendIds() {
		MultipleEventAware mea = (MultipleEventAware) getEntity();
		Set<EventDefinition> definitions = mea.getEventDefinitions();

		if (definitions == null) {
			return Collections.emptySet();
		}
		return definitions.stream().map(EventDefinition::getId).collect(Collectors.toSet());
	}
	
	private boolean isMultipleNonParallel() {

        if (getEntity() instanceof ParallelMultipleCapable) {
            boolean isNonParallel = !((ParallelMultipleCapable) getEntity()).isParallelMultiple();
			LOGGER.debug("is multiple non parallel: %s", isNonParallel);

			return isNonParallel;
        }
		LOGGER.debug("is multiple non parallel: false");
        return false;
    }

}
