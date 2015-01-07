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

package org.arrow.runtime.message;

import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.message.EventMessage;

/**
 * Abstract {@link org.arrow.runtime.message.EventMessage} implementation.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public abstract class AbstractEventMessage implements EventMessage {

    private final ProcessInstance processInstance;

    public AbstractEventMessage(ProcessInstance processInstance) {
        this.processInstance = processInstance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessInstance getProcessInstance() {
        return processInstance;
    }


}
