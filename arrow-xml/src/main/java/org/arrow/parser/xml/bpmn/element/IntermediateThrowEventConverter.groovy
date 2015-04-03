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
import org.arrow.model.definition.EventDefinition
import org.arrow.model.definition.escalation.EscalationEventDefinition
import org.arrow.model.definition.link.LinkEventDefinition
import org.arrow.model.definition.message.MessageEventDefinition
import org.arrow.model.definition.signal.SignalEventDefinition
import org.arrow.model.event.intermediate.throwing.AbstractIntermediateThrowEvent
import org.arrow.model.event.intermediate.throwing.IntermediateThrowEvent
import org.arrow.model.event.intermediate.throwing.impl.*
import org.arrow.model.event.startevent.StartEvent
import org.arrow.parser.xml.bpmn.composable.*

/**
 * Intermediate throw event converter implementation.
 *
 * @since 1.0.0
 * @author christian.weber
 */
public class IntermediateThrowEventConverter implements Converter {

    private Set<ComposableConverter> converters = [] as Set

    public IntermediateThrowEventConverter() {
        converters.add(new SignalEventDefinitionConverter())
        converters.add(new MessageEventDefinitionConverter())
        converters.add(new LinkEventDefinitionConverter())
        converters.add(new ConditionalEventDefinitionConverter())
        converters.add(new EscalationEventDefinitionConverter())
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canConvert(Class type) {
        return IntermediateThrowEvent.class.isAssignableFrom(type)
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
    public Object unmarshal(HierarchicalStreamReader reader,
                            UnmarshallingContext context) {

        final String id = reader.getAttribute("id")
        final String name = reader.getAttribute("name")

        Set<EventDefinition> eventDefinitions = parseEventDefinitions(reader)
        AbstractIntermediateThrowEvent event = determineIntermediateThrowEvent(eventDefinitions)

        event.setId(id)
        event.setName(name)

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
    private static AbstractIntermediateThrowEvent determineIntermediateThrowEvent(Set<EventDefinition> definitions) {
        // @formatter:off
        switch (definitions) {
            // None intermediate throw event
            case { it.isEmpty() }: return new NoneIntermediateThrowEvent()
            // Multiple intermediate throw event
            case { it.size() > 1 }: return new MultipleIntermediateThrowEvent(eventDefinitions: definitions)
            // Signal intermediate throw event
            case { it[0] instanceof SignalEventDefinition }: return new SignalIntermediateThrowEvent(signalEventDefinition: definitions[0])
            // Message intermediate throw event
            case { it[0] instanceof MessageEventDefinition }: return new MessageIntermediateThrowEvent(messageEventDefinition: definitions[0])
            // Link intermediate throw event
            case { it[0] instanceof LinkEventDefinition }: return new LinkIntermediateThrowEvent(linkEventDefinition: definitions[0])
            // Link intermediate throw event
            case { it[0] instanceof EscalationEventDefinition }: return new EscalationIntermediateThrowEvent(escalationEventDefinition: definitions[0])
            // default
            default: throw new IllegalArgumentException("can not determine event with definitions $definitions")
        }
        // @formatter:on
    }

}