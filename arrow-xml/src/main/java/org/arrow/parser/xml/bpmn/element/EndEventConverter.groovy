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

package org.arrow.parser.xml.bpmn.element

import org.arrow.model.definition.compensate.CompensateEventDefinition
import org.arrow.model.definition.conditional.ConditionalEventDefinition
import org.arrow.model.definition.timer.TimerEventDefinition
import org.arrow.model.event.boundary.impl.CancelBoundaryEvent
import org.arrow.model.event.boundary.impl.CompensateBoundaryEvent
import org.arrow.model.event.boundary.impl.ConditionalBoundaryEvent
import org.arrow.model.event.boundary.impl.ErrorBoundaryEvent
import org.arrow.model.event.boundary.impl.EscalationBoundaryEvent
import org.arrow.model.event.boundary.impl.MessageBoundaryEvent
import org.arrow.model.event.boundary.impl.MultipleBoundaryEvent
import org.arrow.model.event.boundary.impl.SignalBoundaryEvent
import org.arrow.model.event.boundary.impl.TimerBoundaryEvent;

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
    private static AbstractEndEvent determineAbstractEndEvent(Set<EventDefinition> definitions) {
        // @formatter:off
        switch (definitions) {
            // None end event
            case { it.isEmpty() }: return new NoneEndEvent()
            // Multiple end event
            case { it.size() > 1 }: return new MultipleEndEvent(eventDefinitions: definitions)
            // Signal end event
            case { it[0] instanceof SignalEventDefinition }: return new SignalEndEvent(signalEventDefinition: definitions[0])
            // Message end event
            case { it[0] instanceof MessageEventDefinition }: return new MessageEndEvent(messageEventDefinition: definitions[0])
            // Terminate end event
            case { it[0] instanceof TerminateEventDefinition }: return new TerminateEndEvent(terminateEventDefinition: definitions[0])
            // Error end event
            case { it[0] instanceof ErrorEventDefinition }: return new ErrorEndEvent(errorEventDefinition: definitions[0])
            // Escalation end event
            case { it[0] instanceof EscalationEventDefinition }: return new EscalationEndEvent(escalationEventDefinition: definitions[0])
            // Cancel  end event
            case { it[0] instanceof CancelEventDefinition }: return new CancelEndEvent(cancelEventDefinition: definitions[0])
            // default
            default: throw new IllegalArgumentException("can not determine event with definitions $definitions")
        }
        // @formatter:on
    }

}