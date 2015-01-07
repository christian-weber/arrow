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
import akka.dispatch.Mapper;
import akka.dispatch.Recover;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Component;
import org.arrow.model.BpmnNodeEntity;
import org.arrow.model.event.boundary.impl.CompensateBoundaryEvent;
import org.arrow.model.task.Task;
import org.arrow.model.transition.Flow;
import org.arrow.model.transition.IncomingFlowAware;
import org.arrow.model.transition.impl.SequenceFlow;
import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.logger.LoggerFacade;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.impl.CompensateEventMessage;
import org.arrow.runtime.message.impl.DefaultFinishEventMessage;
import org.arrow.runtime.message.infrastructure.PersistEventMessage;
import org.arrow.service.engine.concurrent.dispatch.onsuccess.SaveNodeOnSuccess;
import org.arrow.service.engine.execution.interceptor.AbstractExecutionInterceptor;
import scala.concurrent.Future;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * {@link org.arrow.service.engine.execution.interceptor.ExecutionInterceptor} implementation used to initialize
 * {@link org.arrow.model.definition.multiple.MultipleEventAware} BPMN entities.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@Component
public class CompensateTaskInitializer extends AbstractExecutionInterceptor {

    private static transient final LoggerFacade LOGGER = new LoggerFacade(CompensateTaskInitializer.class);

    @Autowired
    private ApplicationContext context;
	@Autowired
	private ExecutionService executionService;
	@Autowired
	private Neo4jTemplate neo4jTemplate;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BpmnNodeEntity beforeExecution(Execution execution, BpmnNodeEntity entity) {

		ProxyFactory factory = new ProxyFactory(entity);
        factory.addAdvice(new CompensateTaskAdvice());

        return (BpmnNodeEntity) factory.getProxy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(Object entity) {

		if (entity instanceof Task) {
            IncomingFlowAware ifa = (IncomingFlowAware) entity;
            Iterator<? extends Flow> iterator = ifa.getIncomingFlows().iterator();

            if (!iterator.hasNext()) {
                return false;
            }

            Flow flow = iterator.next();
            return flow.getSourceRef() instanceof CompensateBoundaryEvent;
        }

        return false;
	}

    private class CompensateTaskAdvice implements MethodInterceptor {

        @Override
        @SuppressWarnings("unchecked")
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {

            if (methodInvocation.getMethod().getName().startsWith("execute")) {
                Future<Iterable<EventMessage>> result = (Future<Iterable<EventMessage>>) methodInvocation.proceed();

                // get the execution instance
                final Execution execution = (Execution) methodInvocation.getArguments()[0];
                // get the execution service instance
                final ExecutionService service = (ExecutionService) methodInvocation.getArguments()[1];

                // get the actor system instance
                ActorSystem actorSystem = service.getBean(ActorSystem.class);

                // add the finish event message to the result future
                // *************************************************
                Future<Iterable<EventMessage>> mapped = result.map(new Mapper<Iterable<EventMessage>, Iterable<EventMessage>>() {

                    @Override
                    public Iterable<EventMessage> apply(Iterable<EventMessage> parameter) {
                        try {
                            Collection<EventMessage> castedParameter = (Collection<EventMessage>) parameter;
                            List<EventMessage> list = new ArrayList<>(castedParameter);

                            EventMessage msg = new CompensateEventMessage(execution);
                            list.add(new PersistEventMessage(execution, msg));

                            return list;
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }

                }, actorSystem.dispatcher());

                mapped.recover(new FallbackRecover(), actorSystem.dispatcher());

                return mapped;
            }

            return methodInvocation.proceed();
        }

    }

    private class FallbackRecover extends Recover<EventMessage> {

        @Override
        public EventMessage recover(Throwable throwable) throws Throwable {
            LOGGER.error(throwable);
            throwable.printStackTrace();
            return null;
        }

    }

}