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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.arrow.runtime.mapper.IterableOfIterable2IterableMessageMapper;
import org.arrow.runtime.message.EventMessage;
import org.arrow.service.engine.concurrent.dispatch.recover.Recovers;
import org.arrow.service.microservice.EventMessageService;
import org.arrow.service.microservice.annotation.CompoundService;
import scala.concurrent.Future;

import java.util.Arrays;

/**
 * Event message compound service implementation for conditional events.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@CompoundService
@Qualifier("conditional")
public class ConditionalEventCompoundService implements EventMessageService<ConditionalEventRequest> {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private ConditionalEventProcessResumingService service1;
    @Autowired
    private ConditionalEventProcessStartingService service2;
    @Autowired
    private ConditionalEventSubProcessInvoker service3;

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> getEventMessages(ConditionalEventRequest signalRequest) {

        // composing the service results
        Future<Iterable<EventMessage>> msg1 = fallback(service1.getEventMessages(signalRequest));
        Future<Iterable<EventMessage>> msg2 = fallback(service2.getEventMessages(signalRequest));
        Future<Iterable<EventMessage>> msg3 = fallback(service3.getEventMessages(signalRequest));

        Future<Iterable<Iterable<EventMessage>>> result;
        result = Futures.sequence(Arrays.asList(msg1, msg2, msg3), getActorSystem().dispatcher());

        return result.map(getMapper(), getActorSystem().dispatcher());
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
     * Returns the mapper instance.
     *
     * @return IterableOfIterable2IterableMessageMapper
     */
    private IterableOfIterable2IterableMessageMapper getMapper() {
        return IterableOfIterable2IterableMessageMapper.INSTANCE;
    }

    /**
     * Defines a recover fallback for the given future instance.
     *
     * @param future the future object
     * @return Future
     */
    private Future<Iterable<EventMessage>> fallback(Future<Iterable<EventMessage>> future) {
        return future.recover(Recovers.logAndReturnEmptyIterable(), getActorSystem().dispatcher());
    }

}
