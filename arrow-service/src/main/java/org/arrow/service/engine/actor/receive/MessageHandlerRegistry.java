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

package org.arrow.service.engine.actor.receive;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Classes which implements this interface are able to register handlers which should be used to handle message events.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public interface MessageHandlerRegistry {

    /**
     * Registers the event message handler.
     *
     * @param predicate the predicate instance
     * @param consumer  the consumer instance
     * @param <T>       the type
     */
    <T> void register(Predicate<T> predicate, Consumer<T> consumer);

    /**
     * Handles the given event message.
     *
     * @param message the event message
     */
    void handle(Object message);

}
