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

import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.converters.MarshallingContext
import com.thoughtworks.xstream.converters.UnmarshallingContext
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import org.arrow.model.PlaceholderBpmnEntity
import org.arrow.model.definition.EventDefinition
import org.arrow.model.definition.cancel.CancelEventDefinition
import org.arrow.model.definition.compensate.CompensateEventDefinition
import org.arrow.model.definition.conditional.ConditionalEventDefinition
import org.arrow.model.definition.error.ErrorEventDefinition
import org.arrow.model.definition.escalation.EscalationEventDefinition
import org.arrow.model.definition.message.MessageEventDefinition
import org.arrow.model.definition.signal.SignalEventDefinition
import org.arrow.model.definition.timer.TimerEventDefinition
import org.arrow.model.event.boundary.AbstractBoundaryEvent
import org.arrow.model.event.boundary.BoundaryEvent
import org.arrow.model.event.boundary.impl.*
import org.arrow.model.event.startevent.StartEvent
import org.arrow.parser.xml.bpmn.composable.*
import org.arrow.parser.xml.bpmn.util.ConverterUtils

/**
 * {@link Converter} implementation used to convert BPMN {@link BoundaryEvent}
 * instances.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class BoundaryEventConverter implements Converter {

    private Set<ComposableConverter> converters = new HashSet<ComposableConverter>()

    public BoundaryEventConverter() {
        converters.add(new SignalEventDefinitionConverter())
        converters.add(new MessageEventDefinitionConverter())
        converters.add(new TimerEventDefinitionConverter())
        converters.add(new ConditionalEventDefinitionConverter())
        converters.add(new ErrorEventDefinitionConverter())
        converters.add(new EscalationEventDefinitionConverter())
        converters.add(new CompensateEventDefinitionConverter())
        converters.add(new CancelEventDefinitionConverter())
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public boolean canConvert(Class type) {
        return BoundaryEvent.class.isAssignableFrom(type)
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
        final String attachedToRef = reader.getAttribute("attachedToRef")
        final String parallelMultiple = reader.getAttribute("parallelMultiple")
        final String cancelActivity = reader.getAttribute("cancelActivity")

        Set<EventDefinition> eventDefinitions = parseEventDefinitions(reader)
        AbstractBoundaryEvent event = determineBoundaryEvent(eventDefinitions)

        event.setId(id)
        event.setName(name)

        PlaceholderBpmnEntity placeholder = new PlaceholderBpmnEntity()
        placeholder.setId(attachedToRef)
        placeholder.setName(attachedToRef)
        event.setAttachedToRef(placeholder)
        event.setParallelMultiple(ConverterUtils.toBoolean(parallelMultiple))
        event.setCancelActivity(cancelActivity != null && ConverterUtils.toBoolean(cancelActivity))

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
     * {@link BoundaryEvent} instances.
     *
     * @param definitions
     * @return AbstractStartEvent
     */
    @SuppressWarnings("GroovyAssignabilityCheck")
    private static AbstractBoundaryEvent determineBoundaryEvent(Set<EventDefinition> definitions) {
        // @formatter:off
        switch (definitions) {
            // Multiple boundary event
            case { it.size() > 1 }: return new MultipleBoundaryEvent(eventDefinitions: definitions)
            // Signal boundary event
            case { it[0] instanceof SignalEventDefinition }: return new SignalBoundaryEvent(signalEventDefinition: definitions[0])
            // Message boundary event
            case { it[0] instanceof MessageEventDefinition }: return new MessageBoundaryEvent(messageEventDefinition: definitions[0])
            // Timer boundary event
            case { it[0] instanceof TimerEventDefinition }: return new TimerBoundaryEvent(timerEventDefinition: definitions[0])
            // Conditional boundary event
            case { it[0] instanceof ConditionalEventDefinition }: return new ConditionalBoundaryEvent(conditionalEventDefinition: definitions[0])
            // Error boundary event
            case { it[0] instanceof ErrorEventDefinition }: return new ErrorBoundaryEvent(errorEventDefinition: definitions[0])
            // Escalation boundary event
            case { it[0] instanceof EscalationEventDefinition }: return new EscalationBoundaryEvent(escalationEventDefinition: definitions[0])
            // Compensate boundary event
            case { it[0] instanceof CompensateEventDefinition }: return new CompensateBoundaryEvent(compensateEventDefinition: definitions[0])
            // Cancel  boundary event
            case { it[0] instanceof CancelEventDefinition }: return new CancelBoundaryEvent(cancelEventDefinition: definitions[0])
            // default
            default: throw new IllegalArgumentException("can not determine event with definitions $definitions")
        }
        // @formatter:on
    }

}