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

package org.arrow.service.engine.concurrent.dispatch.onsuccess;

import akka.actor.ActorRef;
import akka.dispatch.OnSuccess;
import org.arrow.runtime.message.EventMessage;

/**
* OnSuccess implementation used to tell the given actor each message.
 *
 * @since 1.0.0
 * @author christian.weber
*/
public class TellActorForEachMessage extends OnSuccess<Iterable<EventMessage>> {

    private final ActorRef self;
    private final ActorRef actorRef;

    public TellActorForEachMessage(ActorRef self, ActorRef actorRef) {
        this.self = self;
        this.actorRef = actorRef;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSuccess(Iterable<EventMessage> eventMessages) {
        eventMessages.forEach(msg -> actorRef.tell(msg, self));
    }

}
