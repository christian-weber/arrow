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

package org.arrow.runtime.api.event;

import java.util.HashMap;
import java.util.Map;

/**
 * Classes which implements the {@link BusinessCondition} interface are able to
 * continue listening BPMN processes by evaluating business relevant conditions.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public interface BusinessCondition {

    /**
     * Evaluates the business condition. If the method returns 'true' all
     * listening BPMN processes will continue.
     *
     * @param context the business condition context instance
     */
    boolean evaluate(BusinessConditionContext context);

    /**
     * Returns the business condition bean name.
     *
     * @return String
     */
    String getBeanName();

    /**
     * Stores business condition context relevant information in order to
     * forwarding them to the process execution.
     *
     * @author christian.weber
     * @since 1.0.0
     */
    class BusinessConditionContext {

        private final Map<String, Object> variables = new HashMap<>();

        /**
         * Returns the variables map.
         *
         * @return Map
         */
        public Map<String, Object> getVariables() {
            return variables;
        }

        /**
         * Adds the given object with the given key to the variables map.
         *
         * @param key   the variable key
         * @param value the variable value
         */
        @SuppressWarnings("unused")
        public void addVariable(String key, Object value) {
            this.variables.put(key, value);
        }

    }

}
