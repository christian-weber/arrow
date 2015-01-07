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
import org.springframework.beans.factory.annotation.Autowired;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.mapper.IterableOfIterable2IterableMessageMapper;
import org.arrow.service.engine.concurrent.dispatch.recover.Recovers;
import org.arrow.service.microservice.EventMessageService;
import org.arrow.service.microservice.annotation.CompoundService;
import scala.concurrent.Future;

import java.util.Arrays;

/**
 * Signal Message Service which handles message events.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@CompoundService
public class SignalEventCompoundService implements EventMessageService<SignalEventRequest> {

    @Autowired
    private ActorSystem actorSystem;
    @Autowired
    private SignalEventProcessResumingService service1;
    @Autowired
    private SignalEventProcessStartingService service2;
    @Autowired
    private SignalEventSubProcessInvoker service3;

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> getEventMessages(SignalEventRequest signalRequest) {

        if (signalRequest.isStartProcess()) {
            // composing the service results
            Future<Iterable<EventMessage>> msg1 = fallback(service2.getEventMessages(signalRequest));

            Future<Iterable<Iterable<EventMessage>>> result;
            result = Futures.sequence(Arrays.asList(msg1), actorSystem.dispatcher());

            return result.map(getMapper(), actorSystem.dispatcher());
        }

        // composing the service results
        Future<Iterable<EventMessage>> msg1 = fallback(service1.getEventMessages(signalRequest));
        Future<Iterable<EventMessage>> msg2 = fallback(service2.getEventMessages(signalRequest));
        Future<Iterable<EventMessage>> msg3 = fallback(service3.getEventMessages(signalRequest));

        Future<Iterable<Iterable<EventMessage>>> result;
        result = Futures.sequence(Arrays.asList(msg1, msg2, msg3), actorSystem.dispatcher());

        return result.map(getMapper(), actorSystem.dispatcher());
    }

    /**
     * Returns the mapper instance.
     *
     * @return IterableOfIterable2IterableMessageMapper
     */
    public IterableOfIterable2IterableMessageMapper getMapper() {
        return IterableOfIterable2IterableMessageMapper.INSTANCE;
    }

    /**
     * Defines a recover fallback for the given future instance.
     *
     * @param future the future object
     * @return Future
     */
    private Future<Iterable<EventMessage>> fallback(Future<Iterable<EventMessage>> future) {
        return future.recover(Recovers.logAndReturnEmptyIterable(), actorSystem.dispatcher());
    }

}
