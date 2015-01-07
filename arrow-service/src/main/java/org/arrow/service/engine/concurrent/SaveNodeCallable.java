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

package org.arrow.service.engine.concurrent;

import org.spockframework.util.Assert;
import org.springframework.context.ApplicationContext;
import org.arrow.data.neo4j.store.impl.ExecutionStoreImpl;
import org.arrow.model.BpmnNodeEntity;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.logger.LoggerFacade;
import org.arrow.runtime.message.EntityEventMessage;

import java.util.concurrent.Callable;

/**
 * {@link Callable} implementation used to store {@link BpmnNodeEntity}
 * instances.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class SaveNodeCallable implements Callable<EntityEventMessage> {

    private final static LoggerFacade LOGGER = new LoggerFacade(SaveNodeCallable.class);

    private final EntityEventMessage node;
    private final ExecutionStoreImpl executionStore;

    public SaveNodeCallable(EntityEventMessage node, ApplicationContext context) {
        Assert.notNull(node);
        Assert.notNull(context);

        this.node = node;
        this.executionStore = context.getBean(ExecutionStoreImpl.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityEventMessage call() throws Exception {
        final Execution execution = node.getExecution();
        LOGGER.debug("save " + execution.getEntity().getId() + " " + execution.getState());

        executionStore.saveExecution(execution);
        return node;
    }

}
