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

package org.arrow.parser.xml.bpmn.element;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.springframework.util.Assert;
import org.arrow.model.definition.EventDefinition;
import org.arrow.model.definition.conditional.ConditionalEventDefinition;
import org.arrow.model.definition.escalation.EscalationEventDefinition;
import org.arrow.model.definition.link.LinkEventDefinition;
import org.arrow.model.definition.message.MessageEventDefinition;
import org.arrow.model.definition.signal.SignalEventDefinition;
import org.arrow.model.event.intermediate.throwing.AbstractIntermediateThrowEvent;
import org.arrow.model.event.intermediate.throwing.IntermediateThrowEvent;
import org.arrow.model.event.intermediate.throwing.impl.EscalationIntermediateThrowEvent;
import org.arrow.model.event.intermediate.throwing.impl.LinkIntermediateThrowEvent;
import org.arrow.model.event.intermediate.throwing.impl.MessageIntermediateThrowEvent;
import org.arrow.model.event.intermediate.throwing.impl.MultipleIntermediateThrowEvent;
import org.arrow.model.event.intermediate.throwing.impl.NoneIntermediateThrowEvent;
import org.arrow.model.event.intermediate.throwing.impl.SignalIntermediateThrowEvent;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.parser.xml.bpmn.composable.ComposableConverter;
import org.arrow.parser.xml.bpmn.composable.ConditionalEventDefinitionConverter;
import org.arrow.parser.xml.bpmn.composable.EscalationEventDefinitionConverter;
import org.arrow.parser.xml.bpmn.composable.LinkEventDefinitionConverter;
import org.arrow.parser.xml.bpmn.composable.MessageEventDefinitionConverter;
import org.arrow.parser.xml.bpmn.composable.SignalEventDefinitionConverter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class IntermediateThrowEventConverter implements Converter {

	private Set<ComposableConverter<?>> converters = new HashSet<ComposableConverter<?>>();

	public IntermediateThrowEventConverter() {
		converters.add(new SignalEventDefinitionConverter());
		converters.add(new MessageEventDefinitionConverter());
		converters.add(new LinkEventDefinitionConverter());
		converters.add(new ConditionalEventDefinitionConverter());
		converters.add(new EscalationEventDefinitionConverter());
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class type) {
		return IntermediateThrowEvent.class.isAssignableFrom(type);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {

		final String id = reader.getAttribute("id");
		final String name = reader.getAttribute("name");

		Set<EventDefinition> eventDefinitions = parseEventDefinitions(reader);
		AbstractIntermediateThrowEvent event = determineIntermediateThrowEvent(eventDefinitions);

		event.setId(id);
		event.setName(name);

		return event;
	}
	
	/**
	 * Parses the {@link EventDefinition} instances.
	 * 
	 * @param reader
	 * @return Set
	 */
	private Set<EventDefinition> parseEventDefinitions(
			HierarchicalStreamReader reader) {
		Set<EventDefinition> definitions = new HashSet<EventDefinition>();

		while (reader.hasMoreChildren()) {
			reader.moveDown();
			for (ComposableConverter<?> converter : converters) {

				if (converter.supports(reader)) {
					definitions.add(converter.convert(reader));
					break;
				}

			}
			reader.moveUp();
		}

		return definitions;
	}

	/**
	 * Determines the {@link StartEvent} instance by evaluating the
	 * {@link EventDefinition} instances.
	 * 
	 * @param definitions
	 * @return AbstractStartEvent
	 */
	private AbstractIntermediateThrowEvent determineIntermediateThrowEvent(
			Set<EventDefinition> definitions) {
		AbstractIntermediateThrowEvent event = null;

		if (definitions.isEmpty()) {
			return new NoneIntermediateThrowEvent();
		}
		
		// Multiple intermediate catch event
		if (definitions.size() > 1) {
			event = new MultipleIntermediateThrowEvent();
			((MultipleIntermediateThrowEvent) event)
					.setEventDefinitions(definitions);
			return event;
		}
		
		Iterator<EventDefinition> iterator = definitions.iterator();
		EventDefinition definition = iterator.next();

		// Signal Intermediate Catch Event
		if (definition instanceof SignalEventDefinition) {
			SignalEventDefinition eventDefinition = (SignalEventDefinition) definition;

			event = new SignalIntermediateThrowEvent();
			((SignalIntermediateThrowEvent) event)
					.setSignalEventDefinition(eventDefinition);
		}
		// Message Intermediate Catch Event
		if (definition instanceof MessageEventDefinition) {
			MessageEventDefinition eventDefinition = (MessageEventDefinition) definition;

			event = new MessageIntermediateThrowEvent();
			((MessageIntermediateThrowEvent) event)
					.setMessageEventDefinition(eventDefinition);
		}
		// Link Intermediate Catch Event
		if (definition instanceof LinkEventDefinition) {
			LinkEventDefinition eventDefinition = (LinkEventDefinition) definition;
			
			event = new LinkIntermediateThrowEvent();
			((LinkIntermediateThrowEvent) event)
			.setLinkEventDefinition(eventDefinition);
		}
		// Escalation End Event
		if (definition instanceof EscalationEventDefinition) {
			EscalationEventDefinition eventDefinition = (EscalationEventDefinition) definition;
			
			event = new EscalationIntermediateThrowEvent();
			((EscalationIntermediateThrowEvent) event)
			.setEscalationEventDefinition(eventDefinition);
		}

		Assert.notNull(event);
		
		return event;
	}


}