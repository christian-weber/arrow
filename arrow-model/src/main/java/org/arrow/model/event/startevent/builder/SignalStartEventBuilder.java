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

import org.arrow.model.definition.signal.SignalEventDefinition;
import org.arrow.model.event.startevent.impl.SignalStartEvent;

import java.util.Random;

/**
 * Created by christian.weber on 27.07.2014.
 */
public final class SignalStartEventBuilder {

    private final SignalStartEvent startEvent;

    private SignalStartEventBuilder() {
        this.startEvent = new SignalStartEvent();
        this.startEvent.setId(startEvent.hashCode() + "");
    }

    /**
     * Returns a new {@link SignalStartEventBuilder} instance.
     *
     * @return SignalStartEventBuilder
     */
    public static SignalStartEventBuilder builder() {
        return new SignalStartEventBuilder();
    }

    /**
     * Sets the id property.
     *
     * @param id the id property value
     * @return SignalStartEventBuilder
     */
    public SignalStartEventBuilder id(String id) {
        this.startEvent.setId(id);
        return this;
    }

    /**
     * Sets the event definition property.
     *
     * @param signalRef the signal reference value
     * @return SignalStartEventBuilder
     */
    public SignalStartEventBuilder eventDefinition(String signalRef) {
        SignalEventDefinition definition = new SignalEventDefinition();
        definition.setSignalRef(signalRef);
        definition.setId(String.valueOf(System.currentTimeMillis()));

        this.startEvent.setSignalEventDefinition(definition);

        return this;
    }

    /**
     * Returns the {@link SignalStartEvent} instance.
     *
     * @return SignalStartEvent
     */
    public SignalStartEvent build() {
        return this.startEvent;
    }

}
