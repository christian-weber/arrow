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

package org.arrow.service.microservice.impl.none;

import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import akka.dispatch.Recover;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.arrow.runtime.mapper.IterableOfIterable2IterableMessageMapper;
import org.arrow.runtime.message.EventMessage;
import org.arrow.service.engine.concurrent.dispatch.recover.Recovers;
import org.arrow.service.microservice.EventMessageService;
import org.arrow.service.microservice.annotation.CompoundService;
import scala.concurrent.Future;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Event Message Service which handles message events.
 *
 * @since 1.0.0
 * @author christian.weber
 */
@CompoundService
public class NoneEventCompoundService implements EventMessageService<NoneEventRequest> {

    @Autowired
    private ActorSystem system;
    @Autowired
    private NoneEventProcessStartingService service1;

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> getEventMessages(NoneEventRequest request) {

        // composing the service results
        Future<Iterable<EventMessage>> msg1 = fallback(service1.getEventMessages(request));

        Future<Iterable<Iterable<EventMessage>>> result;
        result = Futures.sequence(Arrays.asList(msg1), system.dispatcher());

        return result.map(getMapper(), system.dispatcher());
    }

    /**
     * Returns the mapper instance.
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
        return future.recover(Recovers.logAndReturnEmptyIterable(), system.dispatcher());
    }

}
