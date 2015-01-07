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

import org.arrow.model.definition.signal.SignalEventDefinition;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

/**
 * {@link ComposableConverter} implementation class for
 * {@link SignalEventDefinition} conversion.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class SignalEventDefinitionConverter implements
		ComposableConverter<SignalEventDefinition> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SignalEventDefinition convert(HierarchicalStreamReader reader) {
		SignalEventDefinition definition = new SignalEventDefinition();
		definition.setId(reader.getAttribute("id"));
		definition.setSignalRef(reader.getAttribute("signalRef"));
		
		if (definition.getId() == null) {
			definition.setId("signalevent_" + definition.hashCode());
		}

		return definition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(HierarchicalStreamReader reader) {
		return "signalEventDefinition".equals(reader.getNodeName());
	}

}
