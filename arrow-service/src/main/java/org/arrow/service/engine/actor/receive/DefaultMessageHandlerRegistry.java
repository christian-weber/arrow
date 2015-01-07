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

import org.arrow.util.Predicates.ClassBindedPredicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;


public class DefaultMessageHandlerRegistry implements MessageHandlerRegistry {

    private List<RegistryEntry> entries = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T>void register(Predicate<T> predicate, Consumer<T> consumer) {
        if (predicate instanceof ClassBindedPredicate) {
            RegistryEntry entry = new RegistryEntry(msg -> test(((ClassBindedPredicate)predicate).getBindedClass(), msg.getClass()), consumer);
            entries.add(entry);
        } else {
            RegistryEntry entry = new RegistryEntry(predicate, consumer);
            entries.add(entry);
        }
    }

    private boolean test(Class<?>cls1, Class<?>cls2) {
        return cls1.isAssignableFrom(cls2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void handle(Object message) {
        Optional<RegistryEntry> optional = entries.stream().filter(new FindRegistryEntry(message)).findFirst();
        optional.ifPresent(entry -> entry.getConsumer().accept(message));
    }

    private class FindRegistryEntry implements Predicate<RegistryEntry> {

        private final Object message;

        public FindRegistryEntry(Object message) {
            this.message = message;
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean test(RegistryEntry entry) {
            return entry.getPredicate().test(message);
        }
    }

    private class RegistryEntry {

        private final Predicate predicate;
        private final Consumer consumer;

        public RegistryEntry(Predicate predicate, Consumer consumer) {
            this.predicate = predicate;
            this.consumer = consumer;
        }

        public Predicate getPredicate() {
            return predicate;
        }

        public Consumer getConsumer() {
            return consumer;
        }
    }

}
