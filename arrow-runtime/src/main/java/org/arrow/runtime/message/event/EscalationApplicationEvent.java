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

/**
 * {@link ApplicationEvent} instance for escalation events.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class EscalationApplicationEvent extends ApplicationEvent {

    public EscalationApplicationEvent(String escalationCode, Execution execution) {
        super(new EscalationHolder(escalationCode, execution));
    }

    /**
     * @{@inheritDoc}
     */
    @Override
    public EscalationHolder getSource() {
        return (EscalationHolder) super.getSource();
    }

    /**
     * Bean which holds the escalation data.
     *
     * @author christian.weber
     * @since 1.0.0
     */
    public static class EscalationHolder implements ExecutionHolder {

        private final String escalationCode;
        private final Execution execution;

        public EscalationHolder(String escalationCode, Execution execution) {
            this.escalationCode = escalationCode;
            this.execution = execution;
        }

        /**
         * Returns the escalation code.
         *
         * @return String
         */
        @SuppressWarnings("unused")
        public String getEscalationCode() {
            return escalationCode;
        }

        /**
         * Returns the execution instance.
         *
         * @return Execution
         */
        public Execution getExecution() {
            return execution;
        }
    }

}
