/*
 * Copyright 2014 Christian Weber
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

package org.arrow.parser.xml.bpmn.element

import org.arrow.model.definition.escalation.EscalationEventDefinition
import org.arrow.model.definition.link.LinkEventDefinition
import org.arrow.model.event.intermediate.throwing.impl.EscalationIntermediateThrowEvent
import org.arrow.model.event.intermediate.throwing.impl.LinkIntermediateThrowEvent
import org.arrow.model.event.intermediate.throwing.impl.MessageIntermediateThrowEvent
import org.arrow.model.event.intermediate.throwing.impl.MultipleIntermediateThrowEvent
import org.arrow.model.event.intermediate.throwing.impl.NoneIntermediateThrowEvent
import org.arrow.model.event.intermediate.throwing.impl.SignalIntermediateThrowEvent

import java.util.HashSet
import java.util.Iterator
import java.util.Set

import org.arrow.model.definition.EventDefinition
import org.arrow.model.definition.conditional.ConditionalEventDefinition
import org.arrow.model.definition.message.MessageEventDefinition
import org.arrow.model.definition.signal.SignalEventDefinition
import org.arrow.model.definition.timer.TimerEventDefinition
import org.arrow.model.event.startevent.AbstractStartEvent
import org.arrow.model.event.startevent.StartEvent
import org.arrow.model.event.startevent.impl.ConditionalStartEvent
import org.arrow.model.event.startevent.impl.MessageStartEvent
import org.arrow.model.event.startevent.impl.MultipleStartEvent
import org.arrow.model.event.startevent.impl.NoneStartEvent
import org.arrow.model.event.startevent.impl.SignalStartEvent
import org.arrow.model.event.startevent.impl.TimerStartEvent
import org.arrow.parser.xml.bpmn.composable.ComposableConverter
import org.arrow.parser.xml.bpmn.composable.ConditionalEventDefinitionConverter
import org.arrow.parser.xml.bpmn.composable.MessageEventDefinitionConverter
import org.arrow.parser.xml.bpmn.composable.SignalEventDefinitionConverter
import org.arrow.parser.xml.bpmn.composable.TimerEventDefinitionConverter
import org.arrow.parser.xml.bpmn.util.ConverterUtils

import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.converters.MarshallingContext
import com.thoughtworks.xstream.converters.UnmarshallingContext
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter

/**
 * {@link Converter} implementation used to convert BPMN {@link StartEvent}
 * instances.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class StartEventConverter implements Converter {

	private Set<ComposableConverter> converters = new HashSet<ComposableConverter>()

	public StartEventConverter() {
		converters.add(new SignalEventDefinitionConverter())
		converters.add(new MessageEventDefinitionConverter())
		converters.add(new TimerEventDefinitionConverter())
		converters.add(new ConditionalEventDefinitionConverter())
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class type) {
		return StartEvent.class.isAssignableFrom(type)
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		throw new UnsupportedOperationException()
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

		final String id = reader.getAttribute("id")
		final String name = reader.getAttribute("name")
		final String parallelMultiple = reader.getAttribute("parallelMultiple")
		final String isInterrupting = reader.getAttribute("isInterrupting")

		Set<EventDefinition> eventDefinitions = parseEventDefinitions(reader)
		AbstractStartEvent event = determineStartEvent(eventDefinitions)

		event.setId(id)
		event.setName(name)
		event.setParallelMultiple(ConverterUtils.toBoolean(parallelMultiple))
		event.setInterrupting(ConverterUtils.toBoolean(isInterrupting))

		return event
	}

	/**
	 * Parses the {@link EventDefinition} instances.
	 * 
	 * @param reader
	 * @return Set
	 */
	private Set<EventDefinition> parseEventDefinitions(HierarchicalStreamReader reader) {
		Set<EventDefinition> definitions = new HashSet<EventDefinition>()

		while (reader.hasMoreChildren()) {
			reader.moveDown()
			for (ComposableConverter converter : converters) {
				if (converter.supports(reader)) {
					definitions.add(converter.convert(reader))
					break
				}
			}
			reader.moveUp()
		}

		return definitions
	}

	/**
	 * Determines the {@link StartEvent} instance by evaluating the
	 * {@link EventDefinition} instances.
	 * 
	 * @param definitions
	 * @return AbstractStartEvent
	 */
	@SuppressWarnings("GroovyAssignabilityCheck")
	private static AbstractStartEvent determineStartEvent(Set<EventDefinition> definitions) {
		// @formatter:off
        switch (definitions) {
            // None start event
            case { it.isEmpty() }: return new NoneStartEvent()
            // Multiple start event
            case { it.size() > 1 }: return new MultipleStartEvent(eventDefinitions: definitions)
            // Signal start event
            case { it[0] instanceof SignalEventDefinition }: return new SignalStartEvent(signalEventDefinition: definitions[0])
            // Message start event
            case { it[0] instanceof MessageEventDefinition }: return new MessageStartEvent(messageEventDefinition: definitions[0])
            // Timer start event
            case { it[0] instanceof TimerEventDefinition }: return new TimerStartEvent(timerEventDefinition: definitions[0])
            // Conditional start event
            case { it[0] instanceof ConditionalEventDefinition }: return new ConditionalStartEvent(conditionalEventDefinition: definitions[0])
            // default
            default: throw new IllegalArgumentException("can not determine event with definitions $definitions")
		}
		// @formatter:on
	}

}