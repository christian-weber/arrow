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
import org.arrow.runtime.api.event.BusinessCondition.BusinessConditionContext;
import org.arrow.runtime.execution.Execution;

/**
 * {@link ApplicationEvent} instance for conditional events.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class ConditionApplicationEvent extends ApplicationEvent {

    public ConditionApplicationEvent(String beanName, Execution execution, BusinessConditionContext context) {
        super(new ConditionHolder(beanName, execution, context));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConditionHolder getSource() {
        return (ConditionHolder) super.getSource();
    }

    /**
     * Bean which holds the condition data.
     *
     * @author christian.weber
     * @since 1.0.0
     */
    public static class ConditionHolder implements ExecutionHolder {

        private final Execution execution;
        private final String beanName;
        private final BusinessConditionContext context;

        public ConditionHolder(String beanName, Execution execution, BusinessConditionContext context) {
            this.execution = execution;
            this.beanName = beanName;
            this.context = context;
        }

        /**
         * Returns the execution instance.
         * @return Execution
         */
        public Execution getExecution() {
            return execution;
        }

        /**
         * Returns the bean name.
         * @return String
         */
        public String getBeanName() {
            return beanName;
        }

        /**
         * Returns the business condition context.
         *
         * @return BusinessConditionContext
         */
        public BusinessConditionContext getContext() {
            return context;
        }
    }

}
