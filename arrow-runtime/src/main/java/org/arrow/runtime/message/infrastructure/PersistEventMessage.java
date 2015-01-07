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

package org.arrow.runtime.message.infrastructure;

import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.ProcessInstance;

/**
 * Created by christian.weber on 05.11.2014.
 */
public class PersistEventMessage implements InfrastructureEventMessage {

    private final Execution execution;
    private final EventMessage message;

    public PersistEventMessage(Execution execution, EventMessage message) {
        this.message = message;
        this.execution = execution;
    }

    public PersistEventMessage(Execution execution) {
        this.message = null;
        this.execution = execution;
    }

    public Object getMessage() {
        return message;
    }

    @Override
    public ProcessInstance getProcessInstance() {
        return execution.getProcessInstance();
    }

    @Override
    public String toString() {
        return "PersistEventMessage " + execution.getEntity().getId();
    }
}
