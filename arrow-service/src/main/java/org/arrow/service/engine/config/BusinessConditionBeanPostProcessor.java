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

package org.arrow.service.engine.config;

import akka.actor.ActorSystem;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.arrow.runtime.api.event.BusinessCondition;
import org.arrow.runtime.api.event.BusinessCondition.BusinessConditionContext;
import org.arrow.runtime.api.event.EventTrigger;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.EventMessageEventBus;
import org.arrow.service.engine.concurrent.dispatch.onfailure.PrintStacktraceOnFailure;
import org.arrow.service.engine.concurrent.dispatch.onsuccess.PublishEventMessagesOnSuccess;
import org.arrow.service.microservice.EventMessageService;
import org.arrow.service.microservice.impl.conditional.ConditionalEventRequest;
import scala.concurrent.Future;

/**
 * {@link BeanPostProcessor} implementation used to generate proxies of
 * {@link BusinessCondition} instances in order to give these instances the
 * ability to continue BPMN processes.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@Component
public class BusinessConditionBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private EventMessageEventBus eventBus;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    @Qualifier("conditional")
    private EventMessageService<ConditionalEventRequest> conditionalEventService;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (!(bean instanceof BusinessCondition)) {
            return bean;
        }

        if (AnnotationUtils.findAnnotation(bean.getClass(), EventTrigger.class) == null) {
            return bean;
        }

        ProxyFactory factory = new ProxyFactory(bean);
        factory.addAdvice(new BusinessConditionMethodInterceptor(beanName));

        return factory.getProxy();
    }

    /**
     * {@link MethodInterceptor} implementation used to publish a
     * {@link org.arrow.runtime.message.impl.ConditionEventMessage} event.
     *
     * @author christian.weber
     * @since 1.0.0
     */
    public class BusinessConditionMethodInterceptor implements
            MethodInterceptor {

        private final String beanName;

        public BusinessConditionMethodInterceptor(String beanName) {
            this.beanName = beanName;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {

            BusinessConditionContext context;
            context = (BusinessConditionContext) invocation.getArguments()[0];

            Boolean invoke = (Boolean) invocation.proceed();

            if (invoke) {

                // prepare the request
                ConditionalEventRequest req = new ConditionalEventRequest(context, beanName, null);

                // call the micro service
                Future<Iterable<EventMessage>> messages;
                messages = conditionalEventService.getEventMessages(req);

                ActorSystem system = applicationContext.getBean(ActorSystem.class);
                // register success/failure hooks
                messages.onSuccess(new PublishEventMessagesOnSuccess(applicationContext), system.dispatcher());
                messages.onFailure(new PrintStacktraceOnFailure(), system.dispatcher());

            }

            return invoke;
        }

    }

}
