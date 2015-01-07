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

package org.arrow.service.microservice.impl.message;

import java.util.Map;

/**
 * Request object for all message event relevant information.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class MessageEventRequest {

    private final String messageRef;
    private final Map<String, Object> variables;
    private final boolean startProcess;

    public MessageEventRequest(String messageRef, Map<String, Object> variables) {
        this.messageRef = messageRef;
        this.variables = variables;
        this.startProcess = false;
    }

    public MessageEventRequest(String messageRef, Map<String, Object> variables, boolean startProcess) {
        this.messageRef = messageRef;
        this.variables = variables;
        this.startProcess = startProcess;
    }

    /**
     * Returns the signal reference value.
     *
     * @return String
     */
    public String getMessageRef() {
        return messageRef;
    }

    /**
     * Returns the variables.
     *
     * @return Map
     */
    public Map<String, Object> getVariables() {
        return variables;
    }

    /**
     * Indicates if the message is starting a process.
     *
     * @return boolean
     */
    public boolean isStartProcess() {
        return startProcess;
    }
}
