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

import org.springframework.context.ApplicationEvent;
import org.arrow.runtime.api.event.BusinessCondition.BusinessConditionContext;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.message.AbstractFinishEventMessage;

import java.util.Map;

/**
 * {@link ApplicationEvent} class for 'Conditional' events.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class ConditionEventMessage extends AbstractFinishEventMessage {

    private final String beanName;
    private final BusinessConditionContext context;
    private final Map<String, Object> variables;

    public ConditionEventMessage(Execution execution, String beanName,
                                 BusinessConditionContext context, Map<String, Object> variables) {
        super(execution);
        this.beanName = beanName;
        this.context = context;
        this.variables = variables;
    }

    @Override
    public boolean continueNode() {
        return true;
    }
}
