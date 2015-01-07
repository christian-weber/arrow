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
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Component;
import org.arrow.model.BpmnNodeEntity;
import org.arrow.model.event.intermediate.catching.IntermediateCatchEvent;
import org.arrow.model.gateway.impl.EventBasedGateway;
import org.arrow.model.transition.Flow;
import org.arrow.model.transition.IncomingFlowAware;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.mapper.AppendMessageMapper;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.message.impl.DefaultFinishEventMessage;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.infrastructure.PersistEventMessage;
import org.arrow.service.engine.execution.interceptor.AbstractExecutionInterceptor;
import scala.concurrent.Future;

import java.util.Iterator;
import java.util.Set;

/**
 * {@link org.arrow.service.engine.execution.interceptor.ExecutionInterceptor} implementation used to initialize
 * {@link org.arrow.model.definition.multiple.MultipleEventAware} BPMN entities.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@Component
public class EventBasedGatewayEventInitializer extends AbstractExecutionInterceptor {

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
        factory.addAdvice(new NotifyEventBasedGatewayAdvice());

        return (BpmnNodeEntity) factory.getProxy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Object entity) {

        if (entity instanceof IntermediateCatchEvent) {
            IncomingFlowAware ifa = (IncomingFlowAware) entity;
            Iterator<? extends Flow> iterator = ifa.getIncomingFlows().iterator();

            if (!iterator.hasNext()) {
                return false;
            }
            Flow flow = iterator.next();
            return flow.getSourceRef() instanceof EventBasedGateway;
        }

        return false;
    }

    private class NotifyEventBasedGatewayAdvice implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {

            if (methodInvocation.getMethod().getName().startsWith("finish")) {
                Future<Iterable<EventMessage>> result = (Future<Iterable<EventMessage>>) methodInvocation.proceed();
                Execution execution = (Execution) methodInvocation.getArguments()[0];
                execution.disableFlows();

                ExecutionService service = (ExecutionService) methodInvocation.getArguments()[1];

                IncomingFlowAware ifa = (IncomingFlowAware) execution.getEntity();
                Set<? extends Flow> flows = ifa.getIncomingFlows();
                Flow flow = flows.iterator().next();

                // get the gateway instance
                ProcessInstance pi = execution.getProcessInstance();
                EventBasedGateway gateway = (EventBasedGateway) flow.getSourceRef();

                // find the gateway execution
                Execution gatewayExecution = service.data().execution().findByEntityNodeId(gateway.getNodeId());

                execution.setState(State.SUCCESS);
                executionService.saveEntity(execution);
                EventMessage msg = new DefaultFinishEventMessage(gatewayExecution);
                msg = new PersistEventMessage(execution, msg);

//                new PublishEventMessagesOnSuccess(context).onSuccess(Arrays.asList(msg));

                ActorSystem actorSystem = service.getBean(ActorSystem.class);

                return result.map(new AppendMessageMapper(msg), actorSystem.dispatcher());
            }

            return methodInvocation.proceed();
        }

    }

}