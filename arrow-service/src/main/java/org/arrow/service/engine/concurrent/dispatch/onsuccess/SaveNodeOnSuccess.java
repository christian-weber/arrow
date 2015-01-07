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
import akka.actor.UntypedActorContext;
import akka.dispatch.Futures;
import akka.dispatch.OnSuccess;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.arrow.model.BpmnNodeEntity;
import org.arrow.runtime.message.EntityEventMessage;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.infrastructure.FutureAdapter;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.service.engine.concurrent.SaveNodeCallable;
import scala.App;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;

/**
 * {@link OnSuccess} implementation used to save a {@link BpmnNodeEntity}
 * instance.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class SaveNodeOnSuccess extends OnSuccess<Iterable<EventMessage>> {

    private static final Logger LOGGER = Logger.getLogger(SaveNodeOnSuccess.class);

    private final ApplicationContext applicationContext;
    private final Neo4jTemplate template;
    private final ExecutionContext ec;
    private final ActorRef sender;
    private final ActorRef self;
    private final EntityEventMessage entity;
    private final ExecutionService executionService;

    public SaveNodeOnSuccess(ApplicationContext context, ExecutionContext ec, UntypedActorContext ctx, EntityEventMessage entity) {
        this.applicationContext = context;
        this.entity = entity;
        this.template = context.getBean(Neo4jTemplate.class);
        this.ec = ec;
        this.sender = ctx.sender();
        this.self = ctx.self();
        this.executionService = context.getBean(ExecutionService.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSuccess(Iterable<EventMessage> node) throws Throwable {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("save node " + entity);
        }

        SaveNodeCallable call = new SaveNodeCallable(entity, applicationContext);
        Future<EntityEventMessage> future = Futures.future(call, ec);
        sender.tell(new FutureAdapter(future, entity), self);
    }

}