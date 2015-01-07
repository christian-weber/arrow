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

import java.util.HashSet;
import java.util.Set;

import org.arrow.model.definition.conditional.ConditionalEventDefinition;
import org.arrow.model.definition.conditional.ConditionalEventDefinition.Condition;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

/**
 * {@link ComposableConverter} implementation class for
 * {@link ConditionalEventDefinition} conversion.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class ConditionalEventDefinitionConverter implements
		ComposableConverter<ConditionalEventDefinition> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConditionalEventDefinition convert(HierarchicalStreamReader reader) {
		
		ConditionalEventDefinition definition = new ConditionalEventDefinition();
		definition.setId(reader.getAttribute("id"));
		
		Set<Condition> conditions = new HashSet<Condition>();
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			
			Condition condition = new Condition();
			condition.setBeanName(reader.getValue());
			conditions.add(condition);
			
			reader.moveUp();
		}
		
		definition.setConditions(conditions);
		
		if (definition.getId() == null) {
			definition.setId("conditionalevent_" + definition.hashCode());
		}
		
		return definition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(HierarchicalStreamReader reader) {
		return "conditionalEventDefinition".equals(reader.getNodeName());
	}

}
