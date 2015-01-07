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

package org.arrow.runtime.message.infrastructure;

import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ExecutionGroup;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.execution.service.ExecutionDataService.SynchronisationResult;
import org.arrow.runtime.message.EntityEventMessage;

/**
 * Event message used to signal synchronize events.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class SynchronizeEventMessage implements InfrastructureEventMessage, EntityEventMessage {

    private final Execution execution;
    private final SynchronisationResult synchronisationResult;

    public SynchronizeEventMessage(Execution execution, SynchronisationResult synchronisationResult) {
        this.execution = execution;
        this.synchronisationResult = synchronisationResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BpmnNodeEntitySpecification getEntity() {
        return execution.getEntity();
    }

    /**
     * Returns the execution instance.
     *
     * @return Execution
     */
    public Execution getExecution() {
        return execution;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExecutionGroup getExecutionGroup() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Execution getInterruptableExecution() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the synchronisation result.
     *
     * @return SynchronisationResult
     */
    public SynchronisationResult getSynchronisationResult() {
        return synchronisationResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessInstance getProcessInstance() {
        return execution.getProcessInstance();
    }

}
