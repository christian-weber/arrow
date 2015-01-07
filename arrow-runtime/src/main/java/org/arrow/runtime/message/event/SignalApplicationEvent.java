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
 * {@link ApplicationEvent} instance for signal events.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class SignalApplicationEvent extends ApplicationEvent {

    public SignalApplicationEvent(String signalRef, Execution execution, Map<String, Object> variables) {
        super(new SignalHolder(signalRef, execution, variables));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SignalHolder getSource() {
        return (SignalHolder) super.getSource();
    }

    /**
     * Bean which holds the signal data.
     *
     * @author christian.weber
     * @since 1.0.0
     */
    public static class SignalHolder implements ExecutionHolder {

        private final String signalRef;
        private final Execution execution;
        private final Map<String, Object> variables;

        public SignalHolder(String signalRef, Execution execution, Map<String, Object> variables) {
            this.signalRef = signalRef;
            this.execution = execution;
            this.variables = variables;
        }

        /**
         * Returns the signal reference string.
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
         * Returns the variables.
         *
         * @return Map
         */
        public Map<String, Object> getVariables() {
            return variables;
        }
    }

}
