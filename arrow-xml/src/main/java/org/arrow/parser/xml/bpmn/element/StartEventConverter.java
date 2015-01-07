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

import org.arrow.model.definition.EventDefinition;
import org.arrow.model.definition.conditional.ConditionalEventDefinition;
import org.arrow.model.definition.message.MessageEventDefinition;
import org.arrow.model.definition.signal.SignalEventDefinition;
import org.arrow.model.definition.timer.TimerEventDefinition;
import org.arrow.model.event.startevent.AbstractStartEvent;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.model.event.startevent.impl.ConditionalStartEvent;
import org.arrow.model.event.startevent.impl.MessageStartEvent;
import org.arrow.model.event.startevent.impl.MultipleStartEvent;
import org.arrow.model.event.startevent.impl.NoneStartEvent;
import org.arrow.model.event.startevent.impl.SignalStartEvent;
import org.arrow.model.event.startevent.impl.TimerStartEvent;
import org.arrow.parser.xml.bpmn.composable.ComposableConverter;
import org.arrow.parser.xml.bpmn.composable.ConditionalEventDefinitionConverter;
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
 * {@link Converter} implementation used to convert BPMN {@link StartEvent}
 * instances.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class StartEventConverter implements Converter {

	private Set<ComposableConverter<?>> converters = new HashSet<ComposableConverter<?>>();

	public StartEventConverter() {
		converters.add(new SignalEventDefinitionConverter());
		converters.add(new MessageEventDefinitionConverter());
		converters.add(new TimerEventDefinitionConverter());
		converters.add(new ConditionalEventDefinitionConverter());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class type) {
		return StartEvent.class.isAssignableFrom(type);
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
		final String isInterrupting = reader.getAttribute("isInterrupting");

		Set<EventDefinition> eventDefinitions = parseEventDefinitions(reader);
		AbstractStartEvent event = determineStartEvent(eventDefinitions);

		event.setId(id);
		event.setName(name);
		event.setParallelMultiple(ConverterUtils.toBoolean(parallelMultiple));
		event.setInterrupting(ConverterUtils.toBoolean(isInterrupting));

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
	private AbstractStartEvent determineStartEvent(
			Set<EventDefinition> definitions) {
		AbstractStartEvent event = null;

		// None start event
		if (definitions.isEmpty()) {
			return new NoneStartEvent();
		}
		// Multiple start event
		if (definitions.size() > 1) {
			event = new MultipleStartEvent();
			((MultipleStartEvent) event).setEventDefinitions(definitions);
			return event;
		}
		Iterator<EventDefinition> iterator = definitions.iterator();
		EventDefinition definition = iterator.next();

		// Signal Start Event
		if (definition instanceof SignalEventDefinition) {
			SignalEventDefinition eventDefinition = (SignalEventDefinition) definition;

			event = new SignalStartEvent();
			((SignalStartEvent) event)
					.setSignalEventDefinition(eventDefinition);
		}
		// Message Start Event
		if (definition instanceof MessageEventDefinition) {
			MessageEventDefinition eventDefinition = (MessageEventDefinition) definition;

			event = new MessageStartEvent();
			((MessageStartEvent) event)
					.setMessageEventDefinition(eventDefinition);
		}
		// Timer Start Event
		if (definition instanceof TimerEventDefinition) {
			TimerEventDefinition eventDefinition = (TimerEventDefinition) definition;

			event = new TimerStartEvent();
			((TimerStartEvent) event).setTimerEventDefinition(eventDefinition);
		}
		// Conditional Start Event
		if (definition instanceof ConditionalEventDefinition) {
			ConditionalEventDefinition eventDefinition = (ConditionalEventDefinition) definition;

			event = new ConditionalStartEvent();
			((ConditionalStartEvent) event)
					.setConditionalEventDefinition(eventDefinition);
		}

		return event;
	}

}