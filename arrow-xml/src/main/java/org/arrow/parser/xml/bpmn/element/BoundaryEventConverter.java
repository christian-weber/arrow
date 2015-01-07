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
import org.arrow.model.PlaceholderBpmnEntity;
import org.arrow.model.definition.EventDefinition;
import org.arrow.model.definition.cancel.CancelEventDefinition;
import org.arrow.model.definition.compensate.CompensateEventDefinition;
import org.arrow.model.definition.conditional.ConditionalEventDefinition;
import org.arrow.model.definition.error.ErrorEventDefinition;
import org.arrow.model.definition.escalation.EscalationEventDefinition;
import org.arrow.model.definition.message.MessageEventDefinition;
import org.arrow.model.definition.signal.SignalEventDefinition;
import org.arrow.model.definition.timer.TimerEventDefinition;
import org.arrow.model.event.boundary.AbstractBoundaryEvent;
import org.arrow.model.event.boundary.BoundaryEvent;
import org.arrow.model.event.boundary.impl.*;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.parser.xml.bpmn.composable.*;
import org.arrow.parser.xml.bpmn.util.ConverterUtils;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * {@link Converter} implementation used to convert BPMN {@link BoundaryEvent}
 * instances.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class BoundaryEventConverter implements Converter {

	private Set<ComposableConverter<?>> converters = new HashSet<ComposableConverter<?>>();

	public BoundaryEventConverter() {
		converters.add(new SignalEventDefinitionConverter());
		converters.add(new MessageEventDefinitionConverter());
		converters.add(new TimerEventDefinitionConverter());
		converters.add(new ConditionalEventDefinitionConverter());
		converters.add(new ErrorEventDefinitionConverter());
		converters.add(new EscalationEventDefinitionConverter());
        converters.add(new CompensateEventDefinitionConverter());
        converters.add(new CancelEventDefinitionConverter());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class type) {
		return BoundaryEvent.class.isAssignableFrom(type);
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
		final String attachedToRef = reader.getAttribute("attachedToRef");
		final String parallelMultiple = reader.getAttribute("parallelMultiple");
		final String cancelActivity = reader.getAttribute("cancelActivity");

		Set<EventDefinition> eventDefinitions = parseEventDefinitions(reader);
		AbstractBoundaryEvent event = determineBoundaryEvent(eventDefinitions);

		event.setId(id);
		event.setName(name);

		PlaceholderBpmnEntity placeholder = new PlaceholderBpmnEntity();
		placeholder.setId(attachedToRef);
		placeholder.setName(attachedToRef);
		event.setAttachedToRef(placeholder);
		event.setParallelMultiple(ConverterUtils.toBoolean(parallelMultiple));
		event.setCancelActivity(cancelActivity != null && ConverterUtils.toBoolean(cancelActivity));

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
	 * {@link BoundaryEvent} instances.
	 * 
	 * @param definitions
	 * @return AbstractStartEvent
	 */
	private AbstractBoundaryEvent determineBoundaryEvent(
			Set<EventDefinition> definitions) {
		AbstractBoundaryEvent event = null;

		// Multiple start event
		if (definitions.size() > 1) {
			event = new MultipleBoundaryEvent();
			((MultipleBoundaryEvent) event).setEventDefinitions(definitions);
			return event;
		}

		Iterator<EventDefinition> iterator = definitions.iterator();
		EventDefinition definition = iterator.next();

		// Signal Boundary Event
		if (definition instanceof SignalEventDefinition) {
			SignalEventDefinition eventDefinition = (SignalEventDefinition) definition;

			event = new SignalBoundaryEvent();
			((SignalBoundaryEvent) event)
					.setSignalEventDefinition(eventDefinition);
		}
		// Message Boundary Event
		if (definition instanceof MessageEventDefinition) {
			MessageEventDefinition eventDefinition = (MessageEventDefinition) definition;

			event = new MessageBoundaryEvent();
			((MessageBoundaryEvent) event)
					.setMessageEventDefinition(eventDefinition);
		}
		// Timer Boundary Event
		if (definition instanceof TimerEventDefinition) {
			TimerEventDefinition eventDefinition = (TimerEventDefinition) definition;

			event = new TimerBoundaryEvent();
			((TimerBoundaryEvent) event)
					.setTimerEventDefinition(eventDefinition);
		}
		// Conditional Boundary Event
		if (definition instanceof ConditionalEventDefinition) {
			ConditionalEventDefinition eventDefinition = (ConditionalEventDefinition) definition;

			event = new ConditionalBoundaryEvent();
			((ConditionalBoundaryEvent) event)
					.setConditionalEventDefinition(eventDefinition);
		}
		// Error Boundary Event
		if (definition instanceof ErrorEventDefinition) {
			ErrorEventDefinition eventDefinition = (ErrorEventDefinition) definition;

			event = new ErrorBoundaryEvent();
			((ErrorBoundaryEvent) event)
					.setErrorEventDefinition(eventDefinition);
		}
		// Escalation Boundary Event
		if (definition instanceof EscalationEventDefinition) {
			EscalationEventDefinition eventDefinition = (EscalationEventDefinition) definition;
			
			event = new EscalationBoundaryEvent();
			((EscalationBoundaryEvent) event)
			.setEscalationEventDefinition(eventDefinition);
		}
        // Compensate Boundary Event
        if (definition instanceof CompensateEventDefinition) {
            CompensateEventDefinition eventDefinition = (CompensateEventDefinition) definition;

            event = new CompensateBoundaryEvent();
            ((CompensateBoundaryEvent) event)
                    .setCompensateEventDefinition(eventDefinition);
        }
        // Cancel Boundary Event
        if (definition instanceof CancelEventDefinition) {
            CancelEventDefinition eventDefinition = (CancelEventDefinition) definition;

            event = new CancelBoundaryEvent();
            ((CancelBoundaryEvent) event).setCancelEventDefinition(eventDefinition);
        }

		Assert.notNull(event);
		return event;
	}

}