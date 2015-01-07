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

package org.arrow.service.microservice.impl.conditional;

import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import org.arrow.runtime.api.process.ProcessSpecification;
import org.arrow.runtime.execution.service.data.SubProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.model.event.startevent.impl.SignalStartEvent;
import org.arrow.model.process.SubProcess;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.execution.service.data.ProcessInstanceRepository;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.impl.StartSubProcessEventMessage;
import org.arrow.runtime.support.EngineSynchronizationManager;
import org.arrow.runtime.support.EngineSynchronizationManagerCallableDecorator;
import org.arrow.service.microservice.EventMessageService;
import org.arrow.service.microservice.annotation.EventService;
import scala.concurrent.Future;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * {@link org.arrow.service.microservice.EventMessageService} implementation class which is designed to
 * start all event sub processes by a signal reference.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@EventService
public class ConditionalEventSubProcessInvoker implements EventMessageService<ConditionalEventRequest> {

    @Autowired
    private ApplicationContext context;

    /**
     * Returns the process instance repository.
     *
     * @return ProcessInstanceRepository
     */
    private ProcessInstanceRepository getProcessInstanceRepository() {
        return context.getBean(ProcessInstanceRepository.class);
    }

    /**
     * Returns the sub process repository instance.
     *
     * @return Neo4jSubProcessRepository
     */
    private SubProcessRepository getSubProcessRepository() {
        return context.getBean(SubProcessRepository.class);
    }

    /**
     * Returns the actor system instance.
     *
     * @return ActorSystem
     */
    private ActorSystem getActorSystem() {
        return context.getBean(ActorSystem.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> getEventMessages(ConditionalEventRequest request) {
        return Futures.future(getCallable(request), getActorSystem().dispatcher());
    }

    /**
     * Returns a callable instance.
     *
     * @param request the signal event request
     * @return Callable
     */
    private Callable<Iterable<EventMessage>> getCallable(ConditionalEventRequest request) {
        CallableImpl call = new CallableImpl(request);
        return new EngineSynchronizationManagerCallableDecorator<>(call);
    }

    /**
     * The callable implementation used to find all event sub processes
     * which will be invoked by the signal reference.
     *
     * @author christian.weber
     * @since 1.0.0
     */
    private class CallableImpl implements Callable<Iterable<EventMessage>> {

        private final ConditionalEventRequest request;

        public CallableImpl(ConditionalEventRequest request) {
            this.request = request;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterable<EventMessage> call() throws Exception {

            final String piId = EngineSynchronizationManager.getProcessInstanceId();
            final String beanName = request.getBeanName();

            if (StringUtils.isEmpty(beanName) || StringUtils.isEmpty(piId)) {
                return Collections.emptyList();
            }

            ProcessInstance parentPi = getProcessInstanceRepository().findById(piId);
            Set<EventMessage> messages = new HashSet<>();

            // find all event SubProcess instances with a matching signal start event
            Iterable<? extends ProcessSpecification> processes;
            processes = getSubProcessRepository().findAllByConditionalEvent(piId, beanName);

            for (ProcessSpecification process : processes) {
                SubProcess subProcess = (SubProcess) process;

                StartEvent event = getStartEvent(subProcess, beanName);
                messages.add(new StartSubProcessEventMessage(subProcess, parentPi, event));
            }

            return messages;
        }
    }

    /**
     * Returns the signal start event with the given signal reference, if any.
     *
     * @param sub       the sub process instance
     * @param signalRef the signal reference
     * @return StartEvent
     */
    private StartEvent getStartEvent(SubProcess sub, String signalRef) {

        for (StartEvent event : sub.getStartEvents()) {

            if (event instanceof SignalStartEvent) {
                SignalStartEvent sse = (SignalStartEvent) event;
                String ref = sse.getSignalEventDefinition().getSignalRef();

                if (ref.equals(signalRef)) {
                    return event;
                }
            }

        }
        throw new IllegalArgumentException("signal start event not found: " + signalRef);
    }

}