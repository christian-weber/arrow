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
import org.arrow.model.definition.link.LinkEventDefinition;
import org.arrow.model.definition.message.MessageEventDefinition;
import org.arrow.model.definition.signal.SignalEventDefinition;
import org.arrow.model.definition.timer.TimerEventDefinition;
import org.arrow.model.event.intermediate.catching.AbstractIntermediateCatchEvent;
import org.arrow.model.event.intermediate.catching.IntermediateCatchEvent;
import org.arrow.model.event.intermediate.catching.impl.ConditionalIntermediateCatchEvent;
import org.arrow.model.event.intermediate.catching.impl.LinkIntermediateCatchEvent;
import org.arrow.model.event.intermediate.catching.impl.MessageIntermediateCatchEvent;
import org.arrow.model.event.intermediate.catching.impl.MultipleIntermediateCatchEvent;
import org.arrow.model.event.intermediate.catching.impl.SignalIntermediateCatchEvent;
import org.arrow.model.event.intermediate.catching.impl.TimerIntermediateCatchEvent;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.parser.xml.bpmn.composable.ComposableConverter;
import org.arrow.parser.xml.bpmn.composable.ConditionalEventDefinitionConverter;
import org.arrow.parser.xml.bpmn.composable.LinkEventDefinitionConverter;
import org.arrow.parser.xml.bpmn.composable.MessageEventDefinitionConverter;
import org.arrow.parser.xml.bpmn.composable.SignalEventDefinitionConverter;
import org.arrow.parser.xml.bpmn.composable.TimerEventDefinitionConverter;
import org.arrow.parser.xml.bpmn.util.ConverterUtils;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * {@link Converter} implementation used to convert BPMN
 * {@link IntermediateCatchEvent} instances.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class IntermediateCatchEventConverter implements Converter {

	private Set<ComposableConverter<?>> converters = new HashSet<ComposableConverter<?>>();

	public IntermediateCatchEventConverter() {
		converters.add(new SignalEventDefinitionConverter());
		converters.add(new MessageEventDefinitionConverter());
		converters.add(new TimerEventDefinitionConverter());
		converters.add(new ConditionalEventDefinitionConverter());
		converters.add(new LinkEventDefinitionConverter());
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class type) {
		return IntermediateCatchEvent.class.isAssignableFrom(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {

		final String id = reader.getAttribute("id");
		final String name = reader.getAttribute("name");
		final String parallelMultiple = reader.getAttribute("parallelMultiple");

		Set<EventDefinition> eventDefinitions = parseEventDefinitions(reader);
		AbstractIntermediateCatchEvent event = determineIntermediateCatchEvent(eventDefinitions);

		event.setId(id);
		event.setName(name);
		event.setParallelMultiple(ConverterUtils.toBoolean(parallelMultiple));

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
	private AbstractIntermediateCatchEvent determineIntermediateCatchEvent(
			Set<EventDefinition> definitions) {
		AbstractIntermediateCatchEvent event = null;

		// Multiple intermediate catch event
		if (definitions.size() > 1) {
			event = new MultipleIntermediateCatchEvent();
			((MultipleIntermediateCatchEvent) event)
					.setEventDefinitions(definitions);
			return event;
		}

		Iterator<EventDefinition> iterator = definitions.iterator();
		EventDefinition definition = iterator.next();

		// Signal Intermediate Catch Event
		if (definition instanceof SignalEventDefinition) {
			SignalEventDefinition eventDefinition = (SignalEventDefinition) definition;

			event = new SignalIntermediateCatchEvent();
			((SignalIntermediateCatchEvent) event)
					.setSignalEventDefinition(eventDefinition);
		}
		// Message Intermediate Catch Event
		if (definition instanceof MessageEventDefinition) {
			MessageEventDefinition eventDefinition = (MessageEventDefinition) definition;

			event = new MessageIntermediateCatchEvent();
			((MessageIntermediateCatchEvent) event)
					.setMessageEventDefinition(eventDefinition);
		}
		// Timer Intermediate Catch Event
		if (definition instanceof TimerEventDefinition) {
			TimerEventDefinition eventDefinition = (TimerEventDefinition) definition;

			event = new TimerIntermediateCatchEvent();
			((TimerIntermediateCatchEvent) event)
					.setTimerEventDefinition(eventDefinition);
		}
		// Conditional Intermediate Catch Event
		if (definition instanceof ConditionalEventDefinition) {
			ConditionalEventDefinition eventDefinition = (ConditionalEventDefinition) definition;

			event = new ConditionalIntermediateCatchEvent();
			((ConditionalIntermediateCatchEvent) event)
					.setConditionalEventDefinition(eventDefinition);
		}
		// Link Intermediate Catch Event
		if (definition instanceof LinkEventDefinition) {
			LinkEventDefinition eventDefinition = (LinkEventDefinition) definition;

			event = new LinkIntermediateCatchEvent();
			((LinkIntermediateCatchEvent) event)
					.setLinkEventDefinition(eventDefinition);
		}

		Assert.notNull(event);

		return event;
	}

}