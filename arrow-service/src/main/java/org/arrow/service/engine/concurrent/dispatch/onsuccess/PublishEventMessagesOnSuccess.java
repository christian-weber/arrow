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
import org.apache.log4j.Logger;
import org.spockframework.util.Assert;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StopWatch;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.EventMessageEventBus;
import org.arrow.runtime.message.impl.StartEventMessage;

/**
 * {@link OnSuccess} implementation used to publish a {@link EventMessage} instance.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class PublishEventMessagesOnSuccess extends OnSuccess<Iterable<EventMessage>> {

    private final ApplicationContext context;
    private final EventMessageEventBus eventBus;
    private final ActorRef sender;

    public PublishEventMessagesOnSuccess(ApplicationContext context) {
        this(context, null);
    }

    public PublishEventMessagesOnSuccess(ApplicationContext context, ActorRef sender) {
        this.context = context;
        this.eventBus = context.getBean(EventMessageEventBus.class);
        this.sender = sender;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSuccess(Iterable<EventMessage> events) {

        Assert.notNull(events);

        for (EventMessage event : events) {

            Logger.getLogger(getClass()).info("publish event " + event);

            // create a new master actor ref if the message is of type Start
            if (event instanceof StartEventMessage) {

                ActorRef master = context.getBean("master", ActorRef.class);
                master.tell(event, sender);

                // register the actor to the event bus
                // the process instance id is used as a classifier
                eventBus.subscribe(master, event.getProcessInstance().getId());
            } else {
                eventBus.publish(event);
            }

        }

    }

}