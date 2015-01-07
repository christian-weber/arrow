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

package org.arrow.model.definition.conditional.introduction;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.arrow.model.definition.conditional.ConditionalEventDefinition;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.service.ExecutionService;

/**
 * {@link DelegatingIntroductionInterceptor} implementation used for
 * {@link ConditionalEventPublisher} mixin introduction.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public class ConditionalEventPublisherIntroduction extends
        DelegatingIntroductionInterceptor implements ConditionalEventPublisher {

    private final ConditionalEventDefinition eventDefinition;

    public ConditionalEventPublisherIntroduction(ConditionalEventDefinition eventDefinition) {
        this.eventDefinition = eventDefinition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishConditionalEvent(Execution execution, ExecutionService service) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doProceed(MethodInvocation mi) throws Throwable {

        Method method = mi.getMethod();
        Class<?> dc = method.getDeclaringClass();
        if (dc.isAssignableFrom(Execution.class) && method.getName().equals("execute")) {
            Execution execution = (Execution) mi.getArguments()[0];
            ExecutionService service = (ExecutionService) mi.getArguments()[1];
            publishConditionalEvent(execution, service);
        }

        return mi.proceed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConditionalEventDefinition getConditionalEventDefinition() {
        return eventDefinition;
    }

}
