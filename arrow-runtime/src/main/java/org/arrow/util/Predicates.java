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

package org.arrow.util;

import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;

import java.util.function.Predicate;


public final class Predicates {

    private Predicates() {
        super();
    }

    public static Predicate<Execution> waitingState() {
        return execution -> {
            State state = execution.getState();
            return state != null && state.isWait();
        };
    }

    public static <T>Predicate<T> when(Predicate<T>predicate) {
        return predicate;
    }

    public static <T>Predicate<T> when(Class<T> cls) {
        return new ClassBindedPredicate<>(cls);
    }

    public static <T>Predicate<T> when(Class<T> cls, Predicate<T> predicate) {
        return new ClassBindedPredicate<>(cls).and(predicate);
    }

    public static class ClassBindedPredicate<T> implements Predicate<T> {

        private final Class<T> cls;

        public ClassBindedPredicate(Class<T> cls) {
            this.cls = cls;
        }

        @Override
        public boolean test(T t) {
            return t.getClass().isAssignableFrom(cls);
        }

        public Class<T> getBindedClass() {
            return cls;
        }

        @Override
        public String toString() {
            return "ClassBindedPredicate{" + "cls=" + cls + '}';
        }
    }

}
