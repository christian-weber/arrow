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
import org.arrow.model.definition.cancel.CancelEventDefinition;
import org.arrow.model.definition.error.ErrorEventDefinition;
import org.arrow.model.definition.escalation.EscalationEventDefinition;
import org.arrow.model.definition.message.MessageEventDefinition;
import org.arrow.model.definition.signal.SignalEventDefinition;
import org.arrow.model.definition.terminate.TerminateEventDefinition;
import org.arrow.model.event.endevent.AbstractEndEvent;
import org.arrow.model.event.endevent.EndEvent;
import org.arrow.model.event.endevent.impl.*;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.parser.xml.bpmn.composable.*;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * {@link Converter} implementation used to convert BPMN {@link EndEvent}
 * instances.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class EndEventConverter implements Converter {

	private Set<ComposableConverter<?>> converters = new HashSet<ComposableConverter<?>>();

	public EndEventConverter() {
		converters.add(new SignalEventDefinitionConverter());
		converters.add(new MessageEventDefinitionConverter());
		converters.add(new ErrorEventDefinitionConverter());
		converters.add(new TerminateEventDefinitionConverter());
		converters.add(new EscalationEventDefinitionConverter());
        converters.add(new CancelEventDefinitionConverter());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class type) {
		return EndEvent.class.isAssignableFrom(type);
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

		Set<EventDefinition> eventDefinitions = parseEventDefinitions(reader);
		AbstractEndEvent event = determineAbstractEndEvent(eventDefinitions);

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
	private AbstractEndEvent determineAbstractEndEvent(
			Set<EventDefinition> definitions) {
		AbstractEndEvent event = null;

		// None start event
		if (definitions.isEmpty()) {
			return new NoneEndEvent();
		}

		// Multiple intermediate catch event
		if (definitions.size() > 1) {
			event = new MultipleEndEvent();
			((MultipleEndEvent) event).setEventDefinitions(definitions);
			return event;
		}

		Iterator<EventDefinition> iterator = definitions.iterator();
		EventDefinition definition = iterator.next();

		// Signal End Event
		if (definition instanceof SignalEventDefinition) {
			SignalEventDefinition eventDefinition = (SignalEventDefinition) definition;

			event = new SignalEndEvent();
			((SignalEndEvent) event).setSignalEventDefinition(eventDefinition);
		}
		// Message End Event
		if (definition instanceof MessageEventDefinition) {
			MessageEventDefinition eventDefinition = (MessageEventDefinition) definition;

			event = new MessageEndEvent();
			((MessageEndEvent) event)
					.setMessageEventDefinition(eventDefinition);
		}
		// Error End Event
		if (definition instanceof ErrorEventDefinition) {
			ErrorEventDefinition eventDefinition = (ErrorEventDefinition) definition;

			event = new ErrorEndEvent();
			((ErrorEndEvent) event).setErrorEventDefinition(eventDefinition);
		}
		// Terminate End Event
		if (definition instanceof TerminateEventDefinition) {
			TerminateEventDefinition eventDefinition = (TerminateEventDefinition) definition;

			event = new TerminateEndEvent();
			((TerminateEndEvent) event)
					.setTerminateEventDefinition(eventDefinition);
		}
		// Escalation End Event
		if (definition instanceof EscalationEventDefinition) {
			EscalationEventDefinition eventDefinition = (EscalationEventDefinition) definition;
			
			event = new EscalationEndEvent();
			((EscalationEndEvent) event)
			.setEscalationEventDefinition(eventDefinition);
		}
        // Cancel End Event
        if (definition instanceof CancelEventDefinition) {
            CancelEventDefinition eventDefinition = (CancelEventDefinition) definition;

            event = new CancelEndEvent();
            ((CancelEndEvent) event).setCancelEventDefinition(eventDefinition);
        }

		return event;
	}

}