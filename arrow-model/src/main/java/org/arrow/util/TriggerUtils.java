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

package org.arrow.util;

import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.scheduling.support.CronTrigger;
import org.arrow.model.definition.timer.TimerEventDefinition;

/**
 * Utility class for schedule trigger functionality.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public final class TriggerUtils {

	private TriggerUtils() {
		super();
	}

	/**
	 * Returns a {@link Trigger} instance by evaluating the given
	 * {@link TimerEventDefinition}.
	 * 
	 * @param definition the timer event definition instance
	 * @return Trigger
	 */
	public static Trigger getTrigger(TimerEventDefinition definition) {

		// Time Date
		final String timeDate = definition.getTimeDate();
		if (timeDate != null) {
			return new Iso8601Trigger(timeDate, true);
		}

		// Time Duration
		final String timeDuration = definition.getTimeDuration();
		if (timeDuration != null) {
			return new Iso8601Trigger(timeDuration, true);
		}

		// Time Cycle
		final String timeCycle = definition.getTimeCycle();
		if (timeCycle != null) {
			if (TriggerUtils.isIso8601(timeCycle)) {
				return new Iso8601Trigger(timeCycle, false);
			}
			if (TriggerUtils.isCron(timeCycle)) {
				return new CronTrigger(timeCycle);
			}
		}

		throw new IllegalArgumentException("could not determine trigger");
	}

	/**
	 * Indicates if the given scheduler string is in cron format.
	 * 
	 * @param str the cron string
	 * @return boolean
	 */
	public static boolean isCron(String str) {
		try {
			new CronSequenceGenerator(str, TimeZone.getDefault());
			return true;
		} catch (IllegalArgumentException ex) {
			return false;
		}
	}

	/**
	 * Indicates if the given scheduler string is in ISO8601 format.
	 * 
	 * @param str the ISO 8601 string
	 * @return boolean
	 */
	public static boolean isIso8601(String str) {
		try {
			DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
			fmt.parseDateTime(str);
			return true;
		} catch (IllegalArgumentException ex) {
			return false;
		}
	}

	public static class Iso8601Trigger implements Trigger {

		private final String timeDate;
		private final boolean once;

		public Iso8601Trigger(String timeDate, boolean once) {
			this.timeDate = timeDate;
			this.once = once;
		}

		@Override
		public Date nextExecutionTime(TriggerContext triggerContext) {
			Date date = triggerContext.lastCompletionTime();
			if (date != null) {

				if (once) {
					return null;
				}

				Date scheduled = triggerContext.lastScheduledExecutionTime();
				if (scheduled != null && date.before(scheduled)) {
					// Previous task apparently executed too early...
					// Let's simply use the last calculated execution time then,
					// in order to prevent accidental re-fires in the same
					// second.
					date = scheduled;
				}
			} else {
				date = new Date();
			}

			// DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
			// DateTime dateTime = fmt.parseDateTime(timeDate);

			Period period = ISOPeriodFormat.standard().parsePeriod(timeDate);
			DateTime dateTime = new DateTime();
			dateTime = dateTime.plus(period);

			return dateTime.toDate();
		}

	}

}
