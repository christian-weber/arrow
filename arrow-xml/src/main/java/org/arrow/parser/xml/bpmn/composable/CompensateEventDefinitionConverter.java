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

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import org.arrow.model.definition.cancel.CancelEventDefinition;
import org.arrow.model.definition.compensate.CompensateEventDefinition;

/**
 * {@link ComposableConverter} implementation class for
 * {@link org.arrow.model.definition.escalation.EscalationEventDefinition} conversion.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class CompensateEventDefinitionConverter implements
		ComposableConverter<CompensateEventDefinition> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompensateEventDefinition convert(HierarchicalStreamReader reader) {
        CompensateEventDefinition definition = new CompensateEventDefinition();
		definition.setId(reader.getAttribute("id"));
		
		if (definition.getId() == null) {
			definition.setId("compensateevent_" + definition.hashCode());
		}

		return definition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(HierarchicalStreamReader reader) {
		return "compensateEventDefinition".equals(reader.getNodeName());
	}

}
