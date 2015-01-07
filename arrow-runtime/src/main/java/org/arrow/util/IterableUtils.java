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
import java.util.Collections;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Utility class for Iterable handling.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public final class IterableUtils {

    private IterableUtils() {
        super();
    }

    /**
     * Returns an empty iterable instance if the given iterable argument is null. Otherwise returns the argument itself.
     *
     * @param iterable the iterable instance
     * @param <T>      the type
     * @return T
     */
    public static <T> Iterable<T> emptyIfNull(Iterable<T> iterable) {
        if (iterable == null || !iterable.iterator().hasNext()) {
            return Collections.emptyList();
        }
        return iterable;
    }

}
