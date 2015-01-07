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

import akka.dispatch.OnSuccess;
import org.springframework.context.ApplicationContext;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.EntityEventMessage;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.impl.StartSubProcessEventMessage;

/**
 * {@link akka.dispatch.OnSuccess} implementation used to save a {@link org.arrow.model.BpmnNodeEntity}
 * instance.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class InterruptNodeOnSuccess extends OnSuccess<Iterable<EventMessage>> {

    private final ExecutionService executionService;
    private final EntityEventMessage message;

    private boolean interrupted = false;

    public InterruptNodeOnSuccess(ApplicationContext context, EntityEventMessage msg) {
        this.executionService = context.getBean(ExecutionService.class);
        this.message = msg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSuccess(Iterable<EventMessage> messages) throws Throwable {

        for (EventMessage message : messages) {
            if (!interrupted && isInterrupting(message)) {
                interrupt();
            }
        }

    }

    private void interrupt() {
        interrupted = true;

        // interrupt the entity execution
        Execution execution = message.getExecution();
        execution.setState(State.SUSPEND);

        // suspend the process instance
        ProcessInstance pi = execution.getProcessInstance();
        pi.setFinished(true);
        pi.setState(State.SUSPEND);

        executionService.saveEntity(execution.getProcessInstance());
    }

    private boolean isInterrupting(EventMessage msg) {

        if (!(msg instanceof StartSubProcessEventMessage)) {
            return false;
        }
        StartSubProcessEventMessage message = (StartSubProcessEventMessage) msg;
        return message.getStartEvent() != null && message.getStartEvent().isInterrupting();
    }

}