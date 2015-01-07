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

package org.arrow.runtime.message.event;

import org.springframework.context.ApplicationEvent;
import org.arrow.runtime.execution.Execution;

import java.util.Map;

/**
 * {@link ApplicationEvent} instance for message events.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class MessageApplicationEvent extends ApplicationEvent {

    public MessageApplicationEvent(String message, Execution execution, Map<String, Object> variables) {
        super(new MessageHolder(message, execution, variables));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageHolder getSource() {
        return (MessageHolder) super.getSource();
    }

    /**
     * Bean which holds the message data.
     *
     * @author christian.weber
     * @since 1.0.0
     */
    public static class MessageHolder implements ExecutionHolder {

        private final String message;
        private final Execution execution;
        private final Map<String, Object> variables;

        public MessageHolder(String message, Execution execution, Map<String, Object> variables) {
            this.message = message;
            this.execution = execution;
            this.variables = variables;
        }

        /**
         * Returns the message string.
         *
         * @return String
         */
        public String getMessage() {
            return message;
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
         * Returns the variables.
         *
         * @return Map
         */
        public Map<String, Object> getVariables() {
            return variables;
        }

    }

}
