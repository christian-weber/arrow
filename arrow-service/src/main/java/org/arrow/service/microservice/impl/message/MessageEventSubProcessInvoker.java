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

package org.arrow.service.microservice.impl.message;

import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import org.arrow.data.neo4j.store.ProcessInstanceStore;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.model.event.startevent.impl.MessageStartEvent;
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
public class MessageEventSubProcessInvoker implements EventMessageService<MessageEventRequest> {

    @Autowired
    private ActorSystem actorSystem;
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
    public Future<Iterable<EventMessage>> getEventMessages(MessageEventRequest request) {
        return Futures.future(getCallable(request), actorSystem.dispatcher());
    }

    /**
     * Returns a callable instance.
     *
     * @param request the signal event request
     * @return Callable
     */
    private Callable<Iterable<EventMessage>> getCallable(MessageEventRequest request) {
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

        private final MessageEventRequest request;

        public CallableImpl(MessageEventRequest request) {
            this.request = request;
        }

        @Override
        public Iterable<EventMessage> call() throws Exception {

            final String piId = EngineSynchronizationManager.getProcessInstanceId();
            final String messageRef = request.getMessageRef();

            if (StringUtils.isEmpty(messageRef) || StringUtils.isEmpty(piId)) {
                return Collections.emptyList();
            }

            ProcessInstance parentPi = piRepository.findById(piId);

            List<EventMessage> messages = new ArrayList<>();

            // find all event SubProcess instances with a matching signal start event
            Iterable<? extends ProcessSpecification> processes;
            processes = subProcessRepository.findAllByMessageEvent(piId, messageRef);

            for (ProcessSpecification process : processes) {
                SubProcess subProcess = (SubProcess) process;

                StartEvent event = getStartEvent(subProcess, messageRef);
                messages.add(new StartSubProcessEventMessage(subProcess, parentPi, event));

                messages.add(new EventSubProcessEventMessage(subProcess, parentPi));
            }

            return messages;
        }
    }

    /**
     * Returns the message start event with the given message reference, if any.
     *
     * @param sub       the sub process instance
     * @param messageRef the message reference
     * @return StartEvent
     */
    private StartEvent getStartEvent(SubProcess sub, String messageRef) {

        for (StartEvent event : sub.getStartEvents()) {

            if (event instanceof MessageStartEvent) {
                MessageStartEvent sse = (MessageStartEvent) event;
                String ref = sse.getMessageEventDefinition().getMessageRef();

                if (ref.equals(messageRef)) {
                    return event;
                }
            }

        }
        throw new IllegalArgumentException("message start event not found: " + messageRef);
    }

}