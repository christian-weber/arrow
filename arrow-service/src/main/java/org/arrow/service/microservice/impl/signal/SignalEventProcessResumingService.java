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
import org.apache.log4j.Logger;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.service.data.ExecutionRepository;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.impl.SignalEventMessage;
import org.arrow.service.microservice.EventMessageService;
import org.arrow.service.microservice.annotation.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import scala.concurrent.Future;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Event message service for signal events which resumes processes.
 *
 * @since 1.0.0
 * @author christian.weber
 */
@EventService
public class SignalEventProcessResumingService implements EventMessageService<SignalEventRequest> {

    @Autowired
    private ActorSystem actorSystem;
    @Autowired
    private ExecutionRepository executionRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> getEventMessages(SignalEventRequest request) {
        SignalEventResumingCallable call = new SignalEventResumingCallable(request);
        return Futures.future(call, actorSystem.dispatcher());
    }

    /**
     * Callable implementation for signal event resuming events.
     *
     * @since 1.0.0
     * @author christian.weber
     */
    private class SignalEventResumingCallable implements Callable<Iterable<EventMessage>> {

        private final SignalEventRequest request;

        public SignalEventResumingCallable(SignalEventRequest request) {
            this.request = request;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterable<EventMessage> call() throws Exception {
            Set<EventMessage> events = new HashSet<>();

            // prepare the input data
            String signalRef = request.getSignalRef();
            Map<String, Object> vars = request.getVariables();

            // execution based signal event
            if (request.getExecution() != null) {
                Execution exec = request.getExecution();

                SignalEventMessage event = new SignalEventMessage(signalRef, exec, vars);
                events.add(event);

                Logger.getLogger(getClass()).info("EVENTS " + events.size());
                return events;
            }

//            Thread.sleep(1000);

            // signal reference based signal event
            Set<Execution> executions = executionRepository.findAllBySignalRef(signalRef);
            for (Execution exec : executions) {
                SignalEventMessage event = new SignalEventMessage(signalRef, exec, vars);
                events.add(event);
            }

            return events;
        }
    }

}
