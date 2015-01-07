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

import java.lang.ref.WeakReference;

/**
 * {@link ApplicationEvent} instance for error events.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class ErrorApplicationEvent extends ApplicationEvent {

    public ErrorApplicationEvent(Throwable exception, Execution execution) {
        super(new ErrorHolder(exception, execution));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorHolder getSource() {
        return (ErrorHolder) super.getSource();
    }

    /**
     * Bean which holds the error data.
     *
     * @author christian.weber
     * @since 1.0.0
     */
    public static class ErrorHolder implements ExecutionHolder {

        private final Throwable exception;
        private final WeakReference<Execution> execution;

        public ErrorHolder(Throwable exception, Execution execution) {
            this.exception = exception;
            this.execution = new WeakReference<>(execution);
        }

        /**
         * Returns the exception instance.
         *
         * @return Exception
         */
        public Throwable getException() {
            return exception;
        }

        /**
         * Returns the execution instance.
         *
         * @return execution
         */
        public Execution getExecution() {
            return execution.get();
        }
    }

}
