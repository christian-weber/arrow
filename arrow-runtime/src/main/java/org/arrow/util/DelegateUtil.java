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

import org.springframework.util.Assert;
import org.arrow.runtime.api.gateway.TransitionEvaluation;
import org.arrow.runtime.api.task.JavaDelegate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class for Java delegate functionality.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public final class DelegateUtil {

    private static final Map<String, Object> cache = new ConcurrentHashMap<>();

    private DelegateUtil() {
        super();
    }

    /**
     * Creates a new {@link JavaDelegate} instance of the class with the given
     * name. Throws an IllegalArgumentException if the class could not be
     * instantiated.
     *
     * @param className the java delegate class name
     * @return JavaDelegate
     */
    public static JavaDelegate getJavaDelegate(String className) {

        Assert.notNull(className, "className must not be null");

        try {
            if (cache.containsKey(className)) {
                return (JavaDelegate) cache.get(className);
            }

            Class<?> clazz = Class.forName(className);
            JavaDelegate delegate = (JavaDelegate) clazz.newInstance();

            cache.put(className, delegate);
            return delegate;
        } catch (Exception e) {
            throw new IllegalArgumentException("Java delegation error", e);
        }
    }

    /**
     * Creates a new {@link TransitionEvaluation} instance of the
     * class with the given name. Throws an IllegalArgumentException if the
     * class could not be instantiated.
     *
     * @param className the transition evaluation class name
     * @return JavaDelegate
     */
    public static TransitionEvaluation getTransitionEvaluation(String className) {

        Assert.notNull(className, "className must not be null");

        try {
            if (cache.containsKey(className)) {
                Object obj = cache.get(className);
                return (TransitionEvaluation) obj;
            }

            Class<?> clazz = Class.forName(className);
            TransitionEvaluation evaluation;
            evaluation = (TransitionEvaluation) clazz.newInstance();

            cache.put(className, evaluation);
            return evaluation;
        } catch (Exception e) {
            throw new IllegalArgumentException("transition evaluation delegation error", e);
        }
    }

}
