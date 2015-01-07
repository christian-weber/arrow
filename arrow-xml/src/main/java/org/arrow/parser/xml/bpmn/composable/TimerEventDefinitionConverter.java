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

package org.arrow.parser.xml.bpmn.composable;

import org.arrow.model.definition.timer.TimerEventDefinition;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

/**
 * {@link ComposableConverter} implementation class for
 * {@link TimerEventDefinition} conversion.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class TimerEventDefinitionConverter implements
		ComposableConverter<TimerEventDefinition> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TimerEventDefinition convert(HierarchicalStreamReader reader) {
		
		TimerEventDefinition definition = new TimerEventDefinition();
		definition.setId(reader.getAttribute("id"));
		
		while (reader.hasMoreChildren()) {
			reader.moveDown();

			if ("timeCycle".equals(reader.getNodeName())) {
				handleTimeCycle(definition, reader.getValue());
			}
			if ("timeDuration".equals(reader.getNodeName())) {
				handleTimeDuration(definition, reader.getValue());
			}
			if ("timeDate".equals(reader.getNodeName())) {
				handleTimeDate(definition, reader.getValue());
			}
			
			reader.moveUp();
		}
		
		if (definition.getId() == null) {
			definition.setId("timerevent_" + definition.hashCode());
		}
		
		return definition;
	}
	
	private void handleTimeCycle(TimerEventDefinition definition, String value) {
		definition.setTimeCycle(value);
	}

	private void handleTimeDuration(TimerEventDefinition definition, String value) {
		definition.setTimeDuration(value);
	}
	
	private void handleTimeDate(TimerEventDefinition definition, String value) {
		definition.setTimeDate(value);
	}

	@Override
	public boolean supports(HierarchicalStreamReader reader) {
		return "timerEventDefinition".equals(reader.getNodeName());
	}

}
