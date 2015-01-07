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

import org.arrow.model.definition.message.MessageEventDefinition;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

/**
 * {@link ComposableConverter} implementation class for
 * {@link MessageEventDefinition} conversion.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class MessageEventDefinitionConverter implements
		ComposableConverter<MessageEventDefinition> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MessageEventDefinition convert(HierarchicalStreamReader reader) {
		MessageEventDefinition definition = new MessageEventDefinition();
		definition.setId(reader.getAttribute("id"));
		definition.setMessageRef(reader.getAttribute("messageRef"));

		if (definition.getId() == null) {
			definition.setId("messageevent_" + definition.hashCode());
		}

		return definition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(HierarchicalStreamReader reader) {
		return "messageEventDefinition".equals(reader.getNodeName());
	}

}
