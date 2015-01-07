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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Utility class for collection stream handling.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public final class StreamUtils {

    private StreamUtils() {
        super();
    }

    /**
     * Indicates if the collection contains any entry which matches the given predicate.
     *
     * @param collection the collection instance
     * @param predicate  the predicate instance
     * @param <T>        the type
     * @return boolean
     */
    public static <T> boolean anyMatch(Collection<T> collection, Predicate<T> predicate) {
        return collection.stream().anyMatch(predicate);
    }

    /**
     * Returns the given collection instance. Can be used to write more readable code.
     *
     * @param collection the collection instance
     * @param <T>        the type
     * @return T
     */
    public static <T> Collection<T> of(Collection<T> collection) {
        return collection;
    }

    /**
     * Returns the given predicate instance. Can be used to write more readable code.
     *
     * @param predicate the predicate instance
     * @param <T>       the type
     * @return Predicate
     */
    public static <T> Predicate<T> in(Predicate<T> predicate) {
        return predicate;
    }

    /**
     * Indicates if the collection contains an entry which is an instance of the given type.
     *
     * @param collection the collection instance
     * @param type       the type
     * @return boolean
     */
    public static boolean containsInstanceOf(Collection<?> collection, Class<?> type) {
        return collection.stream().anyMatch(element -> type.isAssignableFrom(element.getClass()));
    }

}
