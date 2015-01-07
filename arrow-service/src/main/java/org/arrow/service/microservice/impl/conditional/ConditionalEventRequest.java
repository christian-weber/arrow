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

package org.arrow.service.microservice.impl.conditional;

import org.arrow.runtime.api.event.BusinessCondition.BusinessConditionContext;
import org.arrow.runtime.execution.Execution;

import java.util.HashMap;
import java.util.Map;

/**
 * Request object for all signal event relevant information.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class ConditionalEventRequest {

    private final BusinessConditionContext context;
    private final String beanName;
    private final Execution execution;
    private final Map<String, Object> variables;

    public ConditionalEventRequest(BusinessConditionContext context, String beanName, Execution exec) {
        this.context = context;
        this.beanName = beanName;
        this.execution = exec;
        this.variables = new HashMap<>();
    }

    /**
     * Returns the signal reference value.
     *
     * @return String
     */
    public String getBeanName() {
        return beanName;
    }

    /**
     * Returns the execution instance.
     *
     * @return Execution
     */
    public Execution getExecution() {
        return execution;
    }

    /**
     * Returns the business condition context.
     *
     * @return BusinessConditionContext
     */
    public BusinessConditionContext getContext() {
        return context;
    }

    /**
     * Returns the variables map.
     *
     * @return Map
     */
    public Map<String, Object> getVariables() {
        return variables;
    }
}
