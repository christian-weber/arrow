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

package org.arrow.service.engine.execution.interceptor.impl;

import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.arrow.model.BpmnNodeEntity;
import org.arrow.model.task.Task;
import org.arrow.model.task.multi.LoopCardinality;
import org.arrow.model.task.multi.MultiInstanceLoopCharacteristics;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.mapper.IterableOfIterable2IterableMessageMapper;
import org.arrow.service.engine.execution.interceptor.AbstractExecutionInterceptor;
import org.arrow.service.engine.execution.interceptor.ExecutionInterceptor;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ExecutionInterceptor} implementation used to support multi instance
 * loop characteristics functionality.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@Component
public class MultiInstanceLoopCharacteristicsExecutionInterceptor extends AbstractExecutionInterceptor {

    @Autowired
    private ApplicationContext context;

    /**
     * {@inheritDoc}
     */
    @Override
    public BpmnNodeEntity beforeExecution(Execution execution, BpmnNodeEntity entity) {

        Task task = (Task) entity;

        MultiInstanceLoopCharacteristics characteristics;
        characteristics = task.getMultiInstanceLoopCharacteristics();

        ProxyFactory factory = new ProxyFactory(task);

        // Loop Cardinality
        if (characteristics.getLoopCardinality() != null) {
            Advice advice = new LoopCardinalityAdvice(characteristics);
            factory.addAdvice(advice);
        }

        return (BpmnNodeEntity) factory.getProxy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Object entity) {
        if (entity instanceof Task) {
            Task task = (Task) entity;
            return task.getMultiInstanceLoopCharacteristics() != null;
        }

        return false;
    }

    /**
     * {@link MethodInterceptor} implementation designed to support multi
     * instance loop characteristics mechanisms.
     *
     * @author christian.weber
     * @since 1.0.0
     */
    public class LoopCardinalityAdvice implements MethodInterceptor {

        private final MultiInstanceLoopCharacteristics loopCharacteristics;

        public LoopCardinalityAdvice(
                MultiInstanceLoopCharacteristics loopCharacteristics) {
            this.loopCharacteristics = loopCharacteristics;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("unchecked")
        public Object invoke(MethodInvocation invocation) throws Throwable {

            if (!invocation.getMethod().getName().equals("execute")) {
                return invocation.proceed();
            }

            LoopCardinality lc = loopCharacteristics.getLoopCardinality();

            List<Future<Iterable<EventMessage>>> futures = new ArrayList<>();

            for (int i = 0; i < lc.getCardinality(); i++) {

                // handle sequential loop
                if (loopCharacteristics.isSequential()) {
                    Future<Iterable<EventMessage>> future = (Future<Iterable<EventMessage>>) invocation.proceed();
                    Await.result(future, Duration.Inf());
                    futures.add(future);
                }
                // handle parallel loop
                else {
                    futures.add((Future<Iterable<EventMessage>>) invocation.proceed());
                }
            }

            ActorSystem system = context.getBean(ActorSystem.class);

            Future<Iterable<Iterable<EventMessage>>> sequence;
            sequence = Futures.sequence(futures, system.dispatcher());

            return sequence.map(IterableOfIterable2IterableMessageMapper.INSTANCE, system.dispatcher());
        }

    }

}
