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

package org.arrow.service;

import akka.actor.Cancellable;
import akka.dispatch.Futures;
import org.arrow.runtime.RuntimeService;
import org.arrow.runtime.api.gateway.TransitionEvaluation;
import org.arrow.runtime.api.query.ExecutionQuery;
import org.arrow.runtime.api.task.JavaDelegate;
import org.arrow.runtime.definition.RelationDef;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.*;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.impl.EscalationEventMessage;
import org.arrow.runtime.message.impl.TimerEventMessage;
import org.arrow.service.engine.concurrent.ScheduledFutureCancellableAdapter;
import org.arrow.service.engine.concurrent.dispatch.onsuccess.PublishEventMessagesOnSuccess;
import org.arrow.service.engine.service.ProcessEngine;
import org.arrow.service.microservice.impl.message.MessageEventCompoundService;
import org.arrow.service.microservice.impl.message.MessageEventRequest;
import org.arrow.service.microservice.impl.signal.SignalEventCompoundService;
import org.arrow.service.microservice.impl.signal.SignalEventRequest;
import org.arrow.util.DelegateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import scala.concurrent.Future;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

/**
 * {@link ExecutionService} implementation class.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@Service
public class DefaultExecutionService implements ExecutionService {

//    @Autowired
//    private RuntimeService runtimeService;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private ProcessEngine engine;
    @Autowired
    private Neo4jTemplate neo4jTemplate;
    @Autowired
    private TaskScheduler scheduler;
//    @Autowired
//    private SignalEventCompoundService signalEventCompoundService;
//    @Autowired
//    private MessageEventCompoundService messageEventCompoundService;
    @Autowired
    private ExecutionDataService executionDataService;
    @Autowired
    private ExecutionUserService executionUserService;
    @Autowired
    private ExecutionAdHocService executionAdHocService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> signal(String signalRef) {
        return signal(signalRef, Collections.<String, Object>emptyMap());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> signal(String signalRef, Map<String, Object> variables) {
        SignalEventCompoundService signalEventCompoundService = context.getBean(SignalEventCompoundService.class);

        SignalEventRequest req = new SignalEventRequest(signalRef, null, variables);
        return signalEventCompoundService.getEventMessages(req);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JavaDelegate getJavaDelegateByName(String beanName) {
        Assert.notNull(beanName);
        return context.getBean(beanName, JavaDelegate.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evaluateExpression(String expression) {
        ExpressionParser parser = new SpelExpressionParser();

        StandardEvaluationContext ec = new StandardEvaluationContext();
        ec.setBeanResolver(new BeanFactoryResolver(context));
        Expression expr = parser.parseExpression(expression);

        expr.getValue(ec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<EventMessage> publishMessageEvent(String message, Execution execution) {
        RuntimeService runtimeService = context.getBean(RuntimeService.class);
        return runtimeService.message(message, execution);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<EventMessage> publishMessageEvent(String message, Execution execution, Map<String, Object> variables) {
        RuntimeService runtimeService = context.getBean(RuntimeService.class);
        return runtimeService.message(message, execution, variables);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExecutionQuery executionQuery() {
        return engine.executionQuery();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public <T> T swapEntity(Object entity, Class<T> targetType) {
        return neo4jTemplate.projectTo(entity, targetType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void saveEntity(Object entity) {
        neo4jTemplate.save(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPersistentState(Object entity) {
        return neo4jTemplate.getPersistentState(entity) != null;
    }

    @Override
    @Transactional
    public <T> T fetchEntity(T entity) {
        return neo4jTemplate.fetch(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<EventMessage> publishErrorEvent(Execution execution, Exception ex) {
        RuntimeService runtimeService = context.getBean(RuntimeService.class);
        return runtimeService.publishErrorEvent(execution, ex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> publishMessageEvent(String message) {
        MessageEventCompoundService messageEventCompoundService = context.getBean(MessageEventCompoundService.class);

        MessageEventRequest request = new MessageEventRequest(message, new HashMap<>());
        return messageEventCompoundService.getEventMessages(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getBean(Class<T> cls) {
        return context.getBean(cls);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getBean(String beanName, Class<T> cls) {
        return context.getBean(beanName, cls);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> publishEscalationEvent(String escalationCode, Execution execution) {

        ProcessInstance ppi = execution.getProcessInstance().getParentProcessInstance();
        Execution escalationExecution = data().execution().findByEscalationCode(escalationCode, ppi.getId());

        EscalationEventMessage message = new EscalationEventMessage(escalationCode, escalationExecution);
        message.setCancelTarget(execution);

        return Futures.successful(iterableOf(message));
    }

    private Iterable<EventMessage> iterableOf(EventMessage... messages) {
        return Arrays.asList(messages);
    }

    @Override
    public Future<EventMessage> publishCancelEvent(Execution execution) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JavaDelegate getJavaDelegateByClassName(String className) {
        return DelegateUtil.getJavaDelegate(className);
    }

    @Override
    public TransitionEvaluation getComplexGatewayTransitionEvaluationByClassName(String className) {
        return DelegateUtil.getTransitionEvaluation(className);
    }

    @Override
    public Execution getExecutionByEscalationCode(final String escalationCode, final String piId) {

        return getRetryTemplate().execute(context1 -> {
            Execution execution = data().execution().findByEscalationCode(escalationCode, piId);
            Assert.notNull(execution, "escalation execution must not be null: " + escalationCode);
            return execution;
        });

    }

    private RetryTemplate getRetryTemplate() {
        SimpleRetryPolicy policy = new SimpleRetryPolicy();
        policy.setMaxAttempts(3);

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(100);

        RetryTemplate template = new RetryTemplate();
        template.setRetryPolicy(policy);
        template.setBackOffPolicy(backOffPolicy);

        return template;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<EventMessage> publishTimerEvent(final Execution execution) {
        EventMessage msg = new TimerEventMessage(execution);
        PublishEventMessagesOnSuccess publisher = new PublishEventMessagesOnSuccess(context);

        publisher.onSuccess(Arrays.asList(msg));
        return Futures.successful(msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cancellable schedule(Trigger trigger, Runnable runnable) {
        ScheduledFuture<?> future = scheduler.schedule(runnable, trigger);
        return new ScheduledFutureCancellableAdapter(future);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExecutionDataService data() {
        return executionDataService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExecutionUserService user() {
        return executionUserService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExecutionRuleService rule() {
        return context.getBean(DefaultExecutionRuleService.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExecutionScriptService script() {
        return context.getBean(ExecutionScriptService.class);
    }

    @Override
    public ExecutionAdHocService adhoc() {
        return executionAdHocService;
    }

    @Override
    public void enableFlow(Execution execution, RelationDef flow) {
        execution.getEnabledFlowIdsContainer().add(flow.getId());
    }

    @Override
    @Deprecated
    public Set<Execution> findFollowingByState(Execution execution, State state) {
        return data().execution().findFollowingByState(execution, state);
    }

    @Override
    @Deprecated
    public Set<Execution> findExecutionsById(Collection<String> ids) {
        return data().execution().findByIds(ids);
    }

}
