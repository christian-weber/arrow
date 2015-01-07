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

package org.arrow.runtime.message;

import akka.actor.ActorRef;
import akka.event.japi.ScanningEventBus;
import org.springframework.stereotype.Component;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.ProcessInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link ScanningEventBus} implementation designed to publish
 * {@link EventMessage} messages.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@Component
public class EventMessageEventBus extends ScanningEventBus<EventMessage, ActorRef, String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareClassifiers(String classifier1, String classifier2) {

        if (classifier1 == null && classifier2 == null) {
            return 0;
        }
        if (classifier1 == null) {
            return 1;
        }
        if (classifier2 == null) {
            return -1;
        }

        return classifier1.compareTo(classifier2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareSubscribers(ActorRef obj1, ActorRef obj2) {
        return obj1.compareTo(obj2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publish(EventMessage event, ActorRef subscriber) {
        subscriber.tell(event, subscriber);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(String classifier, EventMessage event) {

        if (classifier == null) {
            return true;
        }
        ProcessInstance pi = event.getProcessInstance();
        return pi.getId().compareTo(classifier) == 0;
    }


}
