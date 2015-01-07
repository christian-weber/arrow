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

package org.arrow.runtime.execution.service;

import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.data.*;
import org.arrow.runtime.meta.ProcessMetaDataRepository;

import java.util.Set;

/**
 * Execution service facade for data services.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public interface ExecutionDataService {

    /**
     * Executes a depth first traversing to identify the synchronisation state.
     *
     * @param entity the bpmn entity specification
     * @return Synchronisation
     */
    SynchronisationResult depthFirstSynchronization(BpmnNodeEntitySpecification entity);

    /**
     * Executes a breadth first traversing to identify the synchronisation state.
     *
     * @param entity the bpmn entity specification
     * @return Synchronisation
     */
    SynchronisationResult breadthFirstSynchronization(BpmnNodeEntitySpecification entity);

    /**
     * Returns all following executions of the given start node with the given state.
     *
     * @param startNode the start node instance
     * @param state     the state instance
     * @return Set
     */
    Set<Execution> followingExecutions(BpmnNodeEntitySpecification startNode, State state);

    /**
     * Returns the execution repository instance.
     *
     * @return ExecutionRepository
     */
    ExecutionRepository execution();

    /**
     * Returns the start event repository instance.
     *
     * @return StartEventRepository
     */
    StartEventRepository startEvent();

    /**
     * Returns the process instance repository instance.
     *
     * @return ProcessInstanceRepository
     */
    ProcessInstanceRepository processInstance();

    /**
     * Returns the task repository instance.
     *
     * @return TaskRepository
     */
    TaskRepository task();

    /**
     * Returns the gateway repository instance.
     *
     * @return GatewayRepository
     */
    GatewayRepository gateway();

    /**
     * Returns the process repository instance.
     *
     * @return ProcessRepository
     */
    ProcessRepository process();

    /**
     * Returns the process meta data repository instance.
     *
     * @return ProcessMetaDataRepository
     */
    ProcessMetaDataRepository processMetaData();

    /**
     * Synchronisation result information.
     *
     * @author christian.weber
     * @since 1.0.0
     */
    public static class SynchronisationResult {

        private final boolean synchronised;
        private final int expectedFlows;

        public SynchronisationResult(boolean synchronised, int expectedFlows) {
            this.synchronised = synchronised;
            this.expectedFlows = expectedFlows;
        }

        /**
         * Indicates if the bpmn element is synchronised.
         *
         * @return boolean
         */
        public boolean isSynchronised() {
            return synchronised;
        }

        /**
         * Returns the expected amount of incoming flows.
         *
         * @return int
         */
        public int getExpectedFlows() {
            return expectedFlows;
        }

    }

}
