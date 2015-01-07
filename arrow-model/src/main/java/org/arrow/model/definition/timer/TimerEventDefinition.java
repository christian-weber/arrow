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

package org.arrow.model.definition.timer;

import org.arrow.model.definition.AbstractEventDefinition;
import org.arrow.model.definition.EventDefinition;

/**
 * {@link EventDefinition} implementation which represents a timer event
 * definition.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class TimerEventDefinition extends AbstractEventDefinition {

	/** The time date. */
	private String timeDate;

	/** The time duration. */
	private String timeDuration;

	/** The time cycle. */
	private String timeCycle;

	/**
	 * Gets the time date.
	 * 
	 * @return the time date
	 */
	public String getTimeDate() {
		return timeDate;
	}

	/**
	 * Sets the time date.
	 * 
	 * @param timeDate
	 *            the new time date
	 */
	public void setTimeDate(String timeDate) {
		this.timeDate = timeDate;
	}

	/**
	 * Gets the time duration.
	 * 
	 * @return the time duration
	 */
	public String getTimeDuration() {
		return timeDuration;
	}

	/**
	 * Sets the time duration.
	 * 
	 * @param timeDuration
	 *            the new time duration
	 */
	public void setTimeDuration(String timeDuration) {
		this.timeDuration = timeDuration;
	}

	/**
	 * Gets the time cycle.
	 * 
	 * @return the time cycle
	 */
	public String getTimeCycle() {
		return timeCycle;
	}

	/**
	 * Sets the time cycle.
	 * 
	 * @param timeCycle
	 *            the new time cycle
	 */
	public void setTimeCycle(String timeCycle) {
		this.timeCycle = timeCycle;
	}

}
