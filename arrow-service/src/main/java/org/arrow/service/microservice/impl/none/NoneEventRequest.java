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

package org.arrow.service.microservice.impl.none;

import java.util.Map;

/**
 * Request object for all none event relevant information.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class NoneEventRequest {

    private final String processId;
    private final Map<String, Object> variables;

    public NoneEventRequest(String processId, Map<String, Object> variables) {
        this.processId = processId;
        this.variables = variables;
    }

    /**
     * Returns the signal reference value.
     *
     * @return String
     */
    public String getProcessId() {
        return processId;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

}
