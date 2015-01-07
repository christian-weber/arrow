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

package org.arrow.model;

import akka.dispatch.Futures;
import org.arrow.model.transition.Flow;
import org.arrow.model.transition.OutgoingFlowAware;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.listener.BpmnNodeListenerComparator;
import org.arrow.runtime.execution.listener.ExecutionListener;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.EventMessage;
import scala.concurrent.Future;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * Abstract BPMN 2.0 node entity.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public abstract class AbstractBpmnNodeEntity extends AbstractBpmnEntity implements BpmnNodeEntity {

    private transient Set<ExecutionListener> executionListeners;

    {
        this.executionListeners = new TreeSet<>(BpmnNodeListenerComparator.INSTANCE);
    }

    /**
     * Gets the execution listeners.
     *
     * @return the execution listeners
     */
    public Set<ExecutionListener> getExecutionListeners() {
        return executionListeners;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> execute(Execution execution, ExecutionService service) {
        for (ExecutionListener listener : getExecutionListeners()) {
            listener.onExecute(execution, service);
        }
        return executeNode(execution, service);
    }

    protected abstract Future<Iterable<EventMessage>> executeNode(Execution execution, ExecutionService service);

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> finish(Execution execution, ExecutionService service) {

        for (ExecutionListener listener : getExecutionListeners()) {
            listener.onFinish(execution, service);
        }

        return finishNode(execution, service);
    }

    protected Future<Iterable<EventMessage>> finishNode(Execution execution, ExecutionService service) {

        // handle outgoing flows if present
        BpmnNodeEntity entity = (BpmnNodeEntity) execution.getEntity();
        if (entity instanceof OutgoingFlowAware) {
            for (Flow flow : ((OutgoingFlowAware) entity).getOutgoingFlows()) {
                service.enableFlow(execution, flow);
            }
        }

        return Futures.successful(iterableOf());
    }

    protected Iterable<EventMessage> iterableOf(EventMessage... messages) {
        return Arrays.asList(messages);
    }

    protected Iterable<EventMessage> iterableOf(Iterable<EventMessage> messages) {
        return messages;
    }

}
