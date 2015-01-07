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

package org.arrow.model.process;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.impl.DefaultContinueEventMessage;
import org.arrow.runtime.message.impl.DefaultFinishEventMessage;
import org.arrow.runtime.message.impl.StartSubProcessEventMessage;
import org.arrow.util.FutureUtil;
import scala.concurrent.Future;

/**
 * BPMN 2.0 sub process implementation.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("Transaction")
public class Transaction extends SubProcess {

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> executeNode(Execution execution, ExecutionService service) {
        execution.setState(State.WAITING);

        EventMessage msg = new StartSubProcessEventMessage(execution, getSubProcessStartEvent());
        return FutureUtil.result(msg);
    }

    /**
     * Returns the none start event of the given sub process.
     *
     * @return StartEvent
     */
    private StartEvent getSubProcessStartEvent() {
        return getStartEvents().iterator().next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> finishNode(Execution execution, ExecutionService service) {

        super.finishNode(execution, service);
        execution.setState(State.SUCCESS);

        // evaluate if all compensate events has been triggered
        // ****************************************************
        final ProcessInstance subPi = service.data().processInstance().findSubProcessInstance(execution);

        Execution cancelExecution;
        cancelExecution = service.data().execution().findCancelEndEventExecutionByState(subPi.getId(), State.SUCCESS.name());

        if (cancelExecution != null) {
            final Long executionId = execution.getNodeId();
            final String piId = execution.getProcessInstance().getId();
            Execution boundaryExecution = service.data().execution().findCancelBoundaryEventExecution(executionId, piId);
            if (boundaryExecution != null) {
                return FutureUtil.result(new DefaultFinishEventMessage(boundaryExecution));
            }
        }
        return FutureUtil.result(new DefaultContinueEventMessage(execution));
    }

}
