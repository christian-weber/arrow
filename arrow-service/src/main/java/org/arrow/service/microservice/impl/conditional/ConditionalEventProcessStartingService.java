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
import org.arrow.data.neo4j.store.ProcessInstanceStore;
import org.arrow.runtime.api.StartEventSpecification;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.execution.service.data.StartEventRepository;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.impl.StartEventMessage;
import org.arrow.service.microservice.EventMessageService;
import org.arrow.service.microservice.annotation.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import scala.concurrent.Future;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * {@link org.arrow.service.microservice.EventMessageService} implementation class which is designed to
 * start all signal start events by a signal reference.
 *
 * @since 1.0.0
 * @author christian.weber
 */
@EventService
public class ConditionalEventProcessStartingService implements EventMessageService<ConditionalEventRequest> {

    @Autowired
    private ApplicationContext context;

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> getEventMessages(ConditionalEventRequest request) {

        // get the actor system from the IoC container
        ActorSystem system = context.getBean(ActorSystem.class);

        // execute the callable instance
        EventMessageCallable call = new EventMessageCallable(request);
        return Futures.future(call, system.dispatcher());
    }

    /**
     * {@link java.util.concurrent.Callable} implementation class used to find all
     * {@link org.arrow.model.event.startevent.StartEvent} instances which belongs to the given
     * signal reference. All results will cause a new process start.
     *
     * @since 1.0.0
     * @author christian.weber
     */
    private class EventMessageCallable implements Callable<Iterable<EventMessage>> {

        private final ConditionalEventRequest request;

        public EventMessageCallable(ConditionalEventRequest request) {
            this.request = request;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterable<EventMessage> call() throws Exception {

            String beanName = request.getBeanName();
            if (StringUtils.isEmpty(beanName)) {
                return Collections.emptyList();
            }

            Set<EventMessage> messages = new HashSet<>();

            StartEventRepository startEventRepository = context.getBean(StartEventRepository.class);

            // find all signal start events with the given signal reference
            Iterable<? extends StartEventSpecification> startEvents;
            startEvents = startEventRepository.findAllConditionalStartEvents(beanName);

            ProcessInstanceStore piStore = context.getBean(ProcessInstanceStore.class);
            for (StartEventSpecification startEvent : startEvents) {
                ProcessInstance pi = piStore.store(startEvent, request.getVariables());
                messages.add(new StartEventMessage(startEvent, pi));
            }

            return messages;
        }
    }

}
