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

package org.arrow.model.event.startevent.builder;

import org.arrow.model.definition.conditional.ConditionalEventDefinition;
import org.arrow.model.definition.conditional.ConditionalEventDefinition.Condition;
import org.arrow.model.event.startevent.impl.ConditionalStartEvent;

import java.util.HashSet;

/**
 * Builder implementation for {@link ConditionalStartEvent} instances.
 *
 * @since 1.0.0
 * @author christian.weber
 */
public final class ConditionalStartEventBuilder {

    private final ConditionalStartEvent startEvent;

    private ConditionalStartEventBuilder() {
        this.startEvent = new ConditionalStartEvent();
        this.startEvent.setId(startEvent.hashCode() + "");
    }

    /**
     * Returns a new {@link ConditionalStartEventBuilder} instance.
     *
     * @return SignalStartEventBuilder
     */
    public static ConditionalStartEventBuilder builder() {
        return new ConditionalStartEventBuilder();
    }

    /**
     * Sets the id property.
     *
     * @param id the id property value
     * @return SignalStartEventBuilder
     */
    public ConditionalStartEventBuilder id(String id) {
        this.startEvent.setId(id);
        return this;
    }

    /**
     * Sets the event definition property.
     *
     * @param beanName the bean name value
     * @return SignalStartEventBuilder
     */
    public ConditionalStartEventBuilder eventDefinition(String beanName) {
        ConditionalEventDefinition definition = new ConditionalEventDefinition();
        definition.setId(definition.hashCode() + "");

        if (definition.getConditions() == null) {
            definition.setConditions(new HashSet<Condition>());
        }

        Condition condition = new Condition();
        condition.setBeanName(beanName);

        definition.getConditions().add(condition);

        this.startEvent.setConditionalEventDefinition(definition);

        return this;
    }

    /**
     * Returns the {@link org.arrow.model.event.startevent.impl.SignalStartEvent} instance.
     *
     * @return SignalStartEvent
     */
    public ConditionalStartEvent build() {
        return this.startEvent;
    }

}
