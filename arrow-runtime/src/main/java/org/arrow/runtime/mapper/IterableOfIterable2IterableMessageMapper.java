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

package org.arrow.runtime.mapper;

import akka.dispatch.Mapper;
import org.apache.log4j.Logger;
import org.arrow.runtime.message.EventMessage;

import java.util.*;

/**
 * {@link Mapper} implementation used to reduce a iterable of iterable of event message instance
 * to a instance of iterable of event messages.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public final class IterableOfIterable2IterableMessageMapper extends Mapper<Iterable<Iterable<EventMessage>>, Iterable<EventMessage>> {

    public static final IterableOfIterable2IterableMessageMapper INSTANCE = new IterableOfIterable2IterableMessageMapper();

    public IterableOfIterable2IterableMessageMapper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<EventMessage> apply(Iterable<Iterable<EventMessage>> parameter) {
        Logger.getLogger(getClass()).info("MAP");

        List<EventMessage> messages = new ArrayList<>();

        for (Iterable<EventMessage> iteration : parameter) {
            if (iteration == null) {
                continue;
            }
            messages.addAll((Collection<EventMessage>) iteration);
        }

        return messages;
    }

}