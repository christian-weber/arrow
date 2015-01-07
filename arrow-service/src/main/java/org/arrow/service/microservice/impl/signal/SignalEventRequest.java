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

package org.arrow.service.microservice.impl.signal;

/**
 * Created by christian.weber on 23.07.2014.
 */

import org.arrow.runtime.execution.Execution;

import java.util.Map;

/**
 * Request object for all signal event relevant information.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class SignalEventRequest {

    private final String signalRef;
    private final Execution execution;
    private final Map<String, Object> variables;
    private final boolean startProcess;

    public SignalEventRequest(String ref, Execution exec, Map<String, Object> vars) {
        this.signalRef = ref;
        this.execution = exec;
        this.variables = vars;
        this.startProcess = false;
    }

    public SignalEventRequest(String signalRef, Execution execution, Map<String, Object> variables, boolean startProcess) {
        this.signalRef = signalRef;
        this.execution = execution;
        this.variables = variables;
        this.startProcess = startProcess;
    }

    /**
     * Returns the signal reference value.
     *
     * @return String
     */
    public String getSignalRef() {
        return signalRef;
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
     * Returns the variables map.
     *
     * @return Map
     */
    public Map<String, Object> getVariables() {
        return variables;
    }

    public boolean isStartProcess() {
        return startProcess;
    }
}
