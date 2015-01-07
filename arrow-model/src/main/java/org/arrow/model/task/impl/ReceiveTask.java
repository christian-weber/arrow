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

package org.arrow.model.task.impl;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.arrow.model.definition.message.MessageEventDefinition;
import org.arrow.model.definition.message.introduction.MessageEventHandler;
import org.arrow.model.task.AbstractTask;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.EventMessage;
import org.arrow.util.FutureUtil;
import scala.concurrent.Future;

/**
 * BPMN 2 Service Task implementation used to execute Java methods.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
public class ReceiveTask extends AbstractTask implements MessageEventHandler {

    @Fetch
    @RelatedTo(type = "EVENT_DEFINITION", direction = Direction.OUTGOING)
    private MessageEventDefinition eventDefinition;

    private boolean instantiate;

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> executeTask(Execution execution, ExecutionService service) {

        if (instantiate) {
            execution.setState(State.SUCCESS);
            return finish(execution, service);
        }

        execution.setState(State.WAITING);
        return FutureUtil.result();
    }

    @Override
    public Future<Iterable<EventMessage>> handleMessageEvent(Execution execution, ExecutionService service) {
        execution.setState(State.SUCCESS);
        return finish(execution, service);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageEventDefinition getMessageEventDefinition() {
        return eventDefinition;
    }

    /**
     * Sets the message event definition.
     *
     * @param eventDefinition the new message event definition
     */
    public void setMessageEventDefinition(MessageEventDefinition eventDefinition) {
        this.eventDefinition = eventDefinition;
    }

    /**
     * Indicates if the receive task is instantiating.
     *
     * @return boolean
     */
    public boolean isInstantiate() {
        return instantiate;
    }

    /**
     * Sets the instantiating flag
     *
     * @param instantiate the instantiating flag
     */
    public void setInstantiate(boolean instantiate) {
        this.instantiate = instantiate;
    }
}
