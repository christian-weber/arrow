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

import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.message.AbstractContinueEventMessage;

/**
 * {@link org.springframework.context.ApplicationEvent} class for message events.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class CompensateEventMessage extends AbstractContinueEventMessage {

    /**
     * Instantiates a new error event.
     *
     * @param execution the execution
     */
    public CompensateEventMessage(Execution execution) {
        super(execution);
    }

}
