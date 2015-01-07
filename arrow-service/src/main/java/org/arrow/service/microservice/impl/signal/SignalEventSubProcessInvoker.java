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

package org.arrow.service.microservice.impl.signal;

import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import org.arrow.data.neo4j.store.ProcessInstanceStore;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.model.event.startevent.impl.SignalStartEvent;
import org.arrow.model.process.SubProcess;
import org.arrow.runtime.api.process.ProcessSpecification;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.execution.service.data.ProcessInstanceRepository;
import org.arrow.runtime.execution.service.data.StartEventRepository;
import org.arrow.runtime.execution.service.data.SubProcessRepository;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.impl.EventSubProcessEventMessage;
import org.arrow.runtime.message.impl.StartSubProcessEventMessage;
import org.arrow.runtime.support.EngineSynchronizationManager;
import org.arrow.runtime.support.EngineSynchronizationManagerCallableDecorator;
import org.arrow.service.microservice.EventMessageService;
import org.arrow.service.microservice.annotation.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import scala.concurrent.Future;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * {@link org.arrow.service.microservice.EventMessageService} implementation class which is designed to
 * start all event sub processes by a signal reference.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@EventService
public class SignalEventSubProcessInvoker implements EventMessageService<SignalEventRequest> {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private StartEventRepository startEventRepository;
    @Autowired
    private SubProcessRepository subProcessRepository;
    @Autowired
    private ProcessInstanceStore piStore;
    @Autowired
    private ProcessInstanceRepository piRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> getEventMessages(SignalEventRequest request) {
        // get the actor system from the IoC container
        ActorSystem system = context.getBean(ActorSystem.class);
        // execute the callable instance
        return Futures.future(getCallable(request), system.dispatcher());
    }

    /**
     * Returns a callable instance.
     *
     * @param request the signal event request
     * @return Callable
     */
    private Callable<Iterable<EventMessage>> getCallable(SignalEventRequest request) {
        CallableImpl call = new CallableImpl(request);
        return new EngineSynchronizationManagerCallableDecorator<>(call);
    }

    /**
     * The callable implementation used to find all event sub processes
     * which will be invoked by the signal reference.
     *
     * @since 1.0.0
     * @author christian.weber
     */
    private class CallableImpl implements Callable<Iterable<EventMessage>> {

        private final SignalEventRequest request;

        public CallableImpl(SignalEventRequest request) {
            this.request = request;
        }

        @Override
        public Iterable<EventMessage> call() throws Exception {

            final String piId = EngineSynchronizationManager.getProcessInstanceId();
            final String signalRef = request.getSignalRef();

            if (StringUtils.isEmpty(signalRef) || StringUtils.isEmpty(piId)) {
                return Collections.emptyList();
            }

            ProcessInstance parentPi = piRepository.findById(piId);

            List<EventMessage> messages = new ArrayList<>();

            // find all event SubProcess instances with a matching signal start event
            Iterable<? extends ProcessSpecification> processes;
            processes = subProcessRepository.findAllBySignalEvent(piId, signalRef);

            for (ProcessSpecification process : processes) {
                SubProcess subProcess = (SubProcess) process;

                StartEvent event = getStartEvent(subProcess, signalRef);
                messages.add(new StartSubProcessEventMessage(subProcess, parentPi, event));

                messages.add(new EventSubProcessEventMessage(subProcess, parentPi));
            }

            return messages;
        }
    }

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