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

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import org.arrow.data.neo4j.store.ProcessInstanceStore;
import org.arrow.model.definition.timer.TimerEventDefinition;
import org.arrow.model.event.startevent.impl.TimerStartEvent;
import org.arrow.runtime.RuntimeService;
import org.arrow.runtime.api.StartEventSpecification;
import org.arrow.runtime.api.TimerStartEventSpecification;
import org.arrow.runtime.api.event.BusinessCondition;
import org.arrow.runtime.api.event.BusinessCondition.BusinessConditionContext;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.execution.service.data.ExecutionRepository;
import org.arrow.runtime.execution.service.data.ProcessRepository;
import org.arrow.runtime.execution.service.data.StartEventRepository;
import org.arrow.runtime.mapper.EventMessage2ProcessInstanceMapper;
import org.arrow.runtime.mapper.EventMessage2ProcessInstancesMapper;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.EventMessageEventBus;
import org.arrow.runtime.message.impl.ErrorEventMessage;
import org.arrow.runtime.message.impl.MessageEventMessage;
import org.arrow.runtime.message.impl.StartEventMessage;
import org.arrow.service.engine.concurrent.dispatch.onfailure.PrintStacktraceOnFailure;
import org.arrow.service.engine.concurrent.dispatch.onsuccess.PublishEventMessagesOnSuccess;
import org.arrow.service.microservice.EventMessageService;
import org.arrow.service.microservice.impl.conditional.ConditionalEventRequest;
import org.arrow.service.microservice.impl.message.MessageEventCompoundService;
import org.arrow.service.microservice.impl.message.MessageEventRequest;
import org.arrow.service.microservice.impl.none.NoneEventCompoundService;
import org.arrow.service.microservice.impl.none.NoneEventRequest;
import org.arrow.service.microservice.impl.signal.SignalEventCompoundService;
import org.arrow.service.microservice.impl.signal.SignalEventRequest;
import org.arrow.util.TriggerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.stereotype.Service;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.Future;

import java.util.*;

/**
 * Akka based {@link RuntimeService} implementation class.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@Service
public class AkkaRuntimeService implements RuntimeService {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private Neo4jTemplate template;
    @Autowired
    private ProcessRepository processRepository;
    @Autowired
    private StartEventRepository startEventRepository;
    @Autowired
    private TaskScheduler scheduler;

    @Autowired
    private EventMessageEventBus eventMessageEventBus;
    @Autowired
    private ExecutionRepository executionRepository;

    @Autowired
    @Qualifier("conditional")
    private EventMessageService<ConditionalEventRequest> conditionalEventService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<ProcessInstance> startProcessById(String id) {
        return startProcessById(id, new HashMap<>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<ProcessInstance> startProcessById(String id, Map<String, Object> variables) {

        NoneEventCompoundService noneEventCompoundService = context.getBean(NoneEventCompoundService.class);

        NoneEventRequest request = new NoneEventRequest(id, variables);

        Future<Iterable<EventMessage>> messages;
        messages = noneEventCompoundService.getEventMessages(request);

        // register success/failure hooks
        messages.onSuccess(new PublishEventMessagesOnSuccess(context), getExecutionContextExecutor());
        messages.onFailure(new PrintStacktraceOnFailure(), getExecutionContextExecutor());

        EventMessage2ProcessInstanceMapper mapper = EventMessage2ProcessInstanceMapper.INSTANCE;
        return messages.map(mapper, getExecutionContextExecutor());
    }

    /**
     * Executes the BPMN process asynchronously.
     *
     * @param startEvent the start event
     * @param pi     the process instance
     */
    public void publishStartEventMessage(StartEventSpecification startEvent, ProcessInstance pi, ActorRef sender) {
        List<EventMessage> messages = Arrays.asList((EventMessage) new StartEventMessage(startEvent, pi));
        getEventPublisher(sender).onSuccess(messages);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> signal(Execution execution) {
        return signal(null, execution, Collections.<String, Object>emptyMap());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> signal(Execution exec, Map<String, Object> variables) {
        return signal(null, exec, variables);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> signal(String signalRef) {
        return signal(signalRef, null, Collections.<String, Object>emptyMap());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> signal(String signalRef, Map<String, Object> variables) {
        return signal(signalRef, null, variables);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> signal(String signalRef, Execution execution) {
        Map<String, Object> variables = Collections.emptyMap();
        return signal(signalRef, execution, variables);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> signal(String signalRef, Execution exec, Map<String, Object> vars) {

        SignalEventCompoundService signalEventCompoundService = context.getBean(SignalEventCompoundService.class);

        // prepare the request
        SignalEventRequest req = new SignalEventRequest(signalRef, exec, vars);

        // call the micro service
        Future<Iterable<EventMessage>> messages;
        messages = signalEventCompoundService.getEventMessages(req);

        // register success/failure hooks
        messages.onSuccess(new PublishEventMessagesOnSuccess(context), getExecutionContextExecutor());
        messages.onFailure(new PrintStacktraceOnFailure(), getExecutionContextExecutor());

        return messages;
    }

    /**
     * Returns the {@link ExecutionContextExecutor} instance.
     *
     * @return ExecutionContextExecutor
     */
    private ExecutionContextExecutor getExecutionContextExecutor() {
        ActorSystem system = context.getBean(ActorSystem.class);
        return system.dispatcher();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<ProcessInstance>> startProcessBySignal(String signalRef) {
        return startProcessBySignal(signalRef, Collections.<String, Object>emptyMap());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<ProcessInstance>> startProcessBySignal(String signalRef, Map<String, Object> variables) {

        SignalEventCompoundService signalEventCompoundService = context.getBean(SignalEventCompoundService.class);

        SignalEventRequest request = new SignalEventRequest(signalRef, null, variables);
        Future<Iterable<EventMessage>> messages = signalEventCompoundService.getEventMessages(request);

        // register success/failure hooks
        messages.onSuccess(new PublishEventMessagesOnSuccess(context), getExecutionContextExecutor());
        messages.onFailure(new PrintStacktraceOnFailure(), getExecutionContextExecutor());

        EventMessage2ProcessInstancesMapper mapper = EventMessage2ProcessInstancesMapper.INSTANCE;
        return messages.map(mapper, getExecutionContextExecutor());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<EventMessage> message(String message, Execution execution) {
        return message(message, execution, Collections.<String, Object>emptyMap());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<EventMessage> message(String message, Execution execution, Map<String, Object> variables) {
        EventMessage msg = new MessageEventMessage(message, execution, variables);
        getEventPublisher().onSuccess(Arrays.asList(msg));
        return Futures.successful(msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> message(String message) {
        ActorSystem system = context.getBean(ActorSystem.class);
        MessageEventCompoundService messageEventCompoundService = context.getBean(MessageEventCompoundService.class);

        Future<Iterable<EventMessage>> future = messageEventCompoundService.getEventMessages(new MessageEventRequest(message, new HashMap<>()));
        future.onSuccess(getEventPublisher(), system.dispatcher());

        return future;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<ProcessInstance> startProcessByMessage(String messageRef) {
        return startProcessByMessage(messageRef, Collections.<String, Object>emptyMap());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<ProcessInstance> startProcessByMessage(String messageRef, Map<String, Object> variables) {

        MessageEventCompoundService messageEventCompoundService = context.getBean(MessageEventCompoundService.class);

        // prepare the request
        MessageEventRequest request = new MessageEventRequest(messageRef, variables, true);

        // call the micro service
        Future<Iterable<EventMessage>> messages = messageEventCompoundService.getEventMessages(request);

        // register success/failure hooks
        messages.onSuccess(new PublishEventMessagesOnSuccess(context), getExecutionContextExecutor());
        messages.onFailure(new PrintStacktraceOnFailure(), getExecutionContextExecutor());

        // map the result
        EventMessage2ProcessInstanceMapper mapper = EventMessage2ProcessInstanceMapper.INSTANCE;
        return messages.map(mapper, getExecutionContextExecutor());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void schedule(TimerStartEventSpecification event) {
        TimerEventDefinition definition = (TimerEventDefinition) event.getTimerEventDefinition();

        Trigger trigger = TriggerUtils.getTrigger(definition);
        scheduler.schedule(new TimerStartEventRunnable((TimerStartEvent) event, this), trigger);
    }

    private class TimerStartEventRunnable implements Runnable {

        private final TimerStartEvent event;
        private final RuntimeService runtimeService;

        public TimerStartEventRunnable(TimerStartEvent event,
                                       RuntimeService runtimeService) {
            this.event = event;
            this.runtimeService = runtimeService;
        }

        @Override
        public void run() {
            runtimeService.startProcessByStartEvent(event);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<EventMessage> startProcessByStartEvent(StartEventSpecification event) {
        return startProcessByStartEvent(event, Collections.<String, Object>emptyMap());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<EventMessage> startProcessByStartEvent(StartEventSpecification event, Map<String, Object> variables) {

        ProcessInstanceStore piStore = context.getBean(ProcessInstanceStore.class);

        // store the process instance
        ProcessInstance pi = piStore.store(event, variables);

        // publish the event
        EventMessage msg = new StartEventMessage(event, pi);
        getEventPublisher().onSuccess(Arrays.asList(msg));
        return Futures.successful(msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<EventMessage> publishErrorEvent(Execution execution, Exception ex) {
        EventMessage msg = new ErrorEventMessage(execution, ex);
        getEventPublisher().onSuccess(Arrays.asList(msg));
        return Futures.successful(msg);
    }

    /**
     * Returns a {@link PublishEventMessagesOnSuccess} instance.
     *
     * @return PublishEventMessagesOnSuccess
     */
    private PublishEventMessagesOnSuccess getEventPublisher() {
        return new PublishEventMessagesOnSuccess(context);
    }

    /**
     * Returns a {@link PublishEventMessagesOnSuccess} instance.
     *
     * @return PublishEventMessagesOnSuccess
     */
    private PublishEventMessagesOnSuccess getEventPublisher(ActorRef sender) {
        return new PublishEventMessagesOnSuccess(context, sender);
    }

    @Override
    public Future<Iterable<EventMessage>> publishConditionEvent(BusinessCondition condition, BusinessConditionContext context) {

        boolean invoke = condition.evaluate(context);

        if (invoke) {

            // prepare the request
            ConditionalEventRequest req = new ConditionalEventRequest(context, condition.getBeanName(), null);

            // call the micro service
            Future<Iterable<EventMessage>> messages;
            messages = conditionalEventService.getEventMessages(req);

            ActorSystem system = this.context.getBean(ActorSystem.class);
            // register success/failure hooks
            messages.onSuccess(new PublishEventMessagesOnSuccess(this.context), system.dispatcher());
            messages.onFailure(new PrintStacktraceOnFailure(), system.dispatcher());

            return messages;
        }

        return Futures.successful(iterableOf());
    }

    @Override
    public void publishEventMessage(EventMessage eventMessage) {
        getEventPublisher().onSuccess(Arrays.asList(eventMessage));
    }

    private Iterable<EventMessage> iterableOf(EventMessage...eventMessages) {
        return Arrays.asList(eventMessages);
    }

}
