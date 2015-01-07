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

import org.arrow.model.definition.message.MessageEventDefinition;
import org.arrow.model.event.startevent.impl.MessageStartEvent;

/**
 * Builder implementation for {@link MessageStartEvent} instances.
 *
 * @since 1.0.0
 * @author christian.weber
 */
public final class MessageStartEventBuilder {

    private final MessageStartEvent startEvent;

    private MessageStartEventBuilder() {
        this.startEvent = new MessageStartEvent();
        this.startEvent.setId(startEvent.hashCode() + "");
    }

    /**
     * Returns a new {@link MessageStartEventBuilder} instance.
     *
     * @return SignalStartEventBuilder
     */
    public static MessageStartEventBuilder builder() {
        return new MessageStartEventBuilder();
    }

    /**
     * Sets the id property.
     *
     * @param id the id property value
     * @return SignalStartEventBuilder
     */
    public MessageStartEventBuilder id(String id) {
        this.startEvent.setId(id);
        return this;
    }

    /**
     * Sets the event definition property.
     *
     * @param messageRef the message reference value
     * @return SignalStartEventBuilder
     */
    public MessageStartEventBuilder eventDefinition(String messageRef) {
        MessageEventDefinition definition = new MessageEventDefinition();
        definition.setMessageRef(messageRef);
        definition.setId(definition.hashCode() + "");

        this.startEvent.setMessageEventDefinition(definition);

        return this;
    }

    /**
     * Returns the {@link org.arrow.model.event.startevent.impl.SignalStartEvent} instance.
     *
     * @return SignalStartEvent
     */
    public MessageStartEvent build() {
        return this.startEvent;
    }

}
