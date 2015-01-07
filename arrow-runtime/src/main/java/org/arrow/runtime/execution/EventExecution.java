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

/**
 * Specific Execution implementation for event based BPMN entity executions.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class EventExecution extends Execution {

	/** The event received. */
	private boolean eventReceived;

	/**
	 * Checks if is event received.
	 * 
	 * @return true, if is event received
	 */
	public boolean isEventReceived() {
		return eventReceived;
	}

	/**
	 * Sets the event received.
	 * 
	 * @param eventReceived
	 *            the new event received
	 */
	public void setEventReceived(boolean eventReceived) {
		this.eventReceived = eventReceived;
	}

}
