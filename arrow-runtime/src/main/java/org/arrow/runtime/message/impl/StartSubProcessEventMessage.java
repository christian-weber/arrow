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

package org.arrow.runtime.message.impl;

import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.api.StartEventSpecification;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.message.AbstractExecuteEventMessage;
import org.arrow.runtime.message.SubProcessEventMessage;

/**
 * Actor message used to start a new sub process. This class is used for
 * sub process node processing.
 *
 * @since 1.0.0
 * @author christian.weber
 */
public class StartSubProcessEventMessage extends AbstractExecuteEventMessage implements SubProcessEventMessage {

    private final StartEventSpecification startEvent;

    public StartSubProcessEventMessage(BpmnNodeEntitySpecification entity, ProcessInstance pi, StartEventSpecification event) {
        super(entity, pi);
        this.startEvent = event;
    }

    public StartSubProcessEventMessage(Execution execution, StartEventSpecification event) {
        super(execution);
        this.startEvent = event;
    }

    /**
     * Returns the start event instance.
     *
     * @return StartEvent
     */
    public StartEventSpecification getStartEvent() {
        return startEvent;
    }

}
