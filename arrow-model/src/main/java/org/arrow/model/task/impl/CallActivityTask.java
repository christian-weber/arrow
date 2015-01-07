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

import akka.dispatch.Futures;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.arrow.model.BpmnNodeEntity;
import org.arrow.model.process.SubProcessEntity;
import org.arrow.model.task.AbstractTask;
import org.arrow.runtime.api.StartEventSpecification;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.message.impl.StartSubProcessEventMessage;
import org.arrow.runtime.execution.service.ExecutionService;
import scala.concurrent.Future;

import java.util.Date;

/**
 * BPMN 2 called activity task implementation.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("CalledActivity")
public class CallActivityTask extends AbstractTask implements SubProcessEntity {

    /**
     * The called element.
     */
    private String calledElement;

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> executeTask(Execution execution, ExecutionService service) {
        execution.setState(State.WAITING);

        BpmnNodeEntity entity = (BpmnNodeEntity) execution.getEntity();
        ProcessInstance pi = execution.getProcessInstance();

        SubProcessEntity sub = (SubProcessEntity) entity;
        StartEventSpecification startEvent = getSubProcessStartEvent(service);

        EventMessage continueWith = new StartSubProcessEventMessage(sub, pi, startEvent);
        return Futures.successful(iterableOf(continueWith));
    }

    /**
     * Returns either the start event of the called element if the given sub process
     * is a call activity task or the none start event if the given sub process is a
     * embedded sub process.
     *
     * @param service the execution service instance
     * @return StartEvent
     */
    private StartEventSpecification getSubProcessStartEvent(ExecutionService service) {
        return service.data().startEvent().findNoneStartEventByProcessId(getCalledElement());
    }

    /**
     * Gets the called element.
     *
     * @return the called element
     */
    public String getCalledElement() {
        return calledElement;
    }

    /**
     * Sets the called element.
     *
     * @param calledElement the new called element
     */
    public void setCalledElement(String calledElement) {
        this.calledElement = calledElement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getTimestamp() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTimestamp(Date timestamp) {
        // do nothing
    }

    @Override
    public boolean isHasEventSubProcess() {
        return false;
    }
}
