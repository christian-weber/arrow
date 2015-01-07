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
import org.apache.log4j.Logger;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.service.data.ExecutionRepository;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.impl.ConditionEventMessage;
import org.arrow.service.microservice.EventMessageService;
import org.arrow.service.microservice.annotation.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import scala.concurrent.Future;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Event message service for conditional events which resumes processes.
 *
 * @since 1.0.0
 * @author christian.weber
 */
@EventService
public class ConditionalEventProcessResumingService implements EventMessageService<ConditionalEventRequest> {

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
        ConditionalEventResumingCallable call = new ConditionalEventResumingCallable(request);
        return Futures.future(call, system.dispatcher());
    }

    private class ConditionalEventResumingCallable implements Callable<Iterable<EventMessage>> {

        private final ConditionalEventRequest request;

        public ConditionalEventResumingCallable(ConditionalEventRequest request) {
            this.request = request;
        }

        @Override
        public Iterable<EventMessage> call() throws Exception {
            Set<EventMessage> events = new HashSet<>();

            // prepare the input data
            String beanName = request.getBeanName();
            Map<String, Object> vars = request.getVariables();

            // execution based signal event
            if (request.getExecution() != null) {
                Execution exec = request.getExecution();

                ConditionEventMessage event = new ConditionEventMessage(exec, beanName, request.getContext(), vars);
                events.add(event);

                Logger.getLogger(getClass()).info("EVENTS " + events.size());
                return events;
            }

            // signal reference based signal event
            ExecutionRepository executionRepository = context.getBean(ExecutionRepository.class);
            Set<Execution> executions = executionRepository.findAllByBusinessCondition(beanName);
            for (Execution exec : executions) {
                ConditionEventMessage event = new ConditionEventMessage(exec, beanName, request.getContext(), vars);
                events.add(event);
            }

            return events;
        }
    }

}
