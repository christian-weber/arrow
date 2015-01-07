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
import org.springframework.beans.factory.annotation.Autowired;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.message.impl.MessageEventMessage;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.support.EngineSynchronizationManagerCallableDecorator;
import org.arrow.service.microservice.EventMessageService;
import org.arrow.service.microservice.annotation.EventService;
import scala.concurrent.Future;

import java.util.Arrays;
import java.util.concurrent.Callable;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * {@link org.arrow.service.microservice.EventMessageService} implementation class which is designed to
 * invoke bpmn events in order to continue them.
 *
 * @since 1.0.0
 * @author christian.weber
 */
@EventService
public class MessageEventProcessResumingService implements EventMessageService<MessageEventRequest> {

    @Autowired
    private ActorSystem system;
    @Autowired
    private ExecutionService executionService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> getEventMessages(MessageEventRequest request) {
        return Futures.future(getCallable(request), system.dispatcher());
    }

    /**
     * Returns a callable instance.
     *
     * @param request the signal event request
     * @return Callable
     */
    private Callable<Iterable<EventMessage>> getCallable(MessageEventRequest request) {
        EventMessageCallable call = new EventMessageCallable(request);
        return new EngineSynchronizationManagerCallableDecorator<>(call);
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

        private final MessageEventRequest request;

        public EventMessageCallable(MessageEventRequest request) {
            this.request = request;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterable<EventMessage> call() throws Exception {

            if (isEmpty(request.getMessageRef())) {
                return Arrays.asList();
            }

            final String msgRef = request.getMessageRef();
            final State state = State.WAITING;

            // find the task execution with the given message ref
            // and state WAITING
            Execution execution;
            execution = executionService.data().execution().findByMessageRef(msgRef);

            // return the event message
            if (execution == null) {
                execution = executionService.data().execution().findTaskExecutionByMessageAndState(msgRef, state.name());
            }
            if (execution == null) {
                return Arrays.asList();
            }
            EventMessage result = new MessageEventMessage(request.getMessageRef(), execution, request.getVariables());
            return Arrays.asList(result);
        }
    }

}
