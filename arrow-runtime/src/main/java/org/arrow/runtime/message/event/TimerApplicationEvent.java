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
 * {@link ApplicationEvent} instance for timer events.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class TimerApplicationEvent extends ApplicationEvent {

    public TimerApplicationEvent(Execution execution) {
        super(new TimerHolder(execution));
    }

    @Override
    public TimerHolder getSource() {
        return (TimerHolder) super.getSource();
    }

    /**
     * Bean which holds the timer data.
     *
     * @author christian.weber
     * @since 1.0.0
     */
    public static class TimerHolder implements ExecutionHolder {

        private final Execution execution;

        public TimerHolder(Execution execution) {
            this.execution = execution;
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
