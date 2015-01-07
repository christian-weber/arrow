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

import org.arrow.model.event.startevent.impl.NoneStartEvent;

/**
 * Builder implementation for {@link NoneStartEvent} instances.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public final class NoneStartEventBuilder {

    private final NoneStartEvent startEvent;

    private NoneStartEventBuilder() {
        this.startEvent = new NoneStartEvent();
        this.startEvent.setId(startEvent.hashCode() + "");
    }

    /**
     * Returns a new {@link org.arrow.model.event.startevent.builder.NoneStartEventBuilder} instance.
     *
     * @return SignalStartEventBuilder
     */
    public static NoneStartEventBuilder builder() {
        return new NoneStartEventBuilder();
    }

    /**
     * Sets the id property.
     *
     * @param id the id property value
     * @return SignalStartEventBuilder
     */
    public NoneStartEventBuilder id(String id) {
        this.startEvent.setId(id);
        return this;
    }

    /**
     * Returns the {@link org.arrow.model.event.startevent.impl.SignalStartEvent} instance.
     *
     * @return SignalStartEvent
     */
    public NoneStartEvent build() {
        return this.startEvent;
    }

}
