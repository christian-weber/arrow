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

import org.arrow.model.definition.escalation.EscalationEventDefinition;
import org.arrow.model.process.event.Escalation;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

/**
 * {@link ComposableConverter} implementation class for
 * {@link EscalationEventDefinition} conversion.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class EscalationEventDefinitionConverter implements
		ComposableConverter<EscalationEventDefinition> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EscalationEventDefinition convert(HierarchicalStreamReader reader) {
		EscalationEventDefinition definition = new EscalationEventDefinition();
		definition.setId(reader.getAttribute("id"));
		
		final String escalationRef = reader.getAttribute("escalationRef");
		EscalationPlaceholder placeholder = new EscalationPlaceholder();
		placeholder.setId(escalationRef);
		
		definition.setEscalationRef(placeholder);

		if (definition.getId() == null) {
			definition.setId("escalationevent_" + definition.hashCode());
		}

		return definition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(HierarchicalStreamReader reader) {
		return "escalationEventDefinition".equals(reader.getNodeName());
	}

	public static class EscalationPlaceholder extends Escalation {
		
	}
	
}
