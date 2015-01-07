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

import java.util.Set;

import akka.dispatch.Futures;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.arrow.model.BpmnNodeEntity;
import org.arrow.model.definition.multiple.MultipleEventAware;
import org.arrow.model.event.boundary.BoundaryEvent;
import org.arrow.model.event.boundary.BoundaryEventAware;
import org.arrow.model.event.boundary.impl.ErrorBoundaryEvent;
import org.arrow.util.FutureUtil;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.EventMessage;
import org.arrow.service.engine.execution.interceptor.ExecutionInterceptor;
import org.arrow.service.engine.util.PublishErrorEventMethodInterceptor;

import scala.concurrent.Future;

/**
 * {@link ExecutionInterceptor} implementation used to initialize
 * {@link MultipleEventAware} BPMN entities.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@Component
public class BoundaryEventAwareInitializer implements ExecutionInterceptor {

    @Autowired
    private ApplicationContext context;

    /**
     * {@inheritDoc}
     */
    @Override
    public BpmnNodeEntity beforeExecution(Execution execution, BpmnNodeEntity entity) {

        BoundaryEventAware bea = (BoundaryEventAware) entity;

        ProxyFactory factory = new ProxyFactory(bea);
        if (hasErrorEventDefinition(bea)) {
            factory.addAdvice(0, new PublishErrorEventMethodInterceptor());
        }

        return (BpmnNodeEntity) factory.getProxy();
    }

    /**
     * Indicates if the given {@link BoundaryEventAware} instance has an error event definition.
     *
     * @param bea the boundary event aware instance
     * @return boolean
     */
    private boolean hasErrorEventDefinition(BoundaryEventAware bea) {
        Assert.notNull(bea);
        Set<BoundaryEvent> events = bea.getBoundaryEvents();

        if (events == null) {
            return false;
        }

        for (BoundaryEvent event : events) {
            if (event instanceof ErrorBoundaryEvent) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Object entity) {
        if (entity instanceof BoundaryEventAware) {
            BoundaryEventAware bea = (BoundaryEventAware) entity;
            return !CollectionUtils.isEmpty(bea.getBoundaryEvents());
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> afterExecution(Execution execution, BpmnNodeEntity entity) {


        ExecutionService service = context.getBean(ExecutionService.class);

        BoundaryEventAware bea = (BoundaryEventAware) entity;
        for (BoundaryEvent boundaryEvent : bea.getBoundaryEvents()) {
            if (boundaryEvent.isCancelActivity()) {
                Execution boundaryExecution = service.data().execution().findByEntity(boundaryEvent, execution.getProcessInstance().getId());
                if (boundaryExecution != null && boundaryExecution.getState().compareTo(State.WAITING) != 0) {
                    execution.setState(State.SUSPEND);
                }
            }
        }

        return Futures.successful(FutureUtil.iterableOf());
    }

}