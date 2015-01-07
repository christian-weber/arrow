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

package org.arrow.runtime.execution.service.data;

import org.springframework.data.repository.query.Param;
import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.execution.State;

import java.util.Collection;
import java.util.Set;

/**
 * Execution repository definition.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public interface ExecutionRepository {

    /**
     * Returns all executions by the given process instance.
     *
     * @param processInstance the process instance
     * @return Set
     */
    Set<Execution> findByProcessInstance(ProcessInstance processInstance);

    /**
     * Returns all executions by the given business condition bean name.
     *
     * @param beanName the business condition bean name
     * @return Set
     */
    Set<Execution> findAllByBusinessCondition(String beanName);

    /**
     * Returns all executions by the given signal reference.
     *
     * @param signalRef the signal reference
     * @return Set
     */
    Set<Execution> findAllBySignalRef(String signalRef);

    /**
     * Returns all executions by the given message reference.
     *
     * @param messageRef the message reference
     * @return Execution
     */
    Execution findByMessageRef(String messageRef);

    /**
     * Returns the execution with the given execution id.
     *
     * @param executionId the execution id
     * @return Execution
     */
    Execution findByErrorEvent(String executionId);

    /**
     * Returns the execution with the given bpmn node entity specification and process instance id.
     *
     * @param entity the bpmn node entity specification instance
     * @param piId   the process instance id
     * @return Execution
     */
    Execution findByEntity(BpmnNodeEntitySpecification entity, String piId);

    /**
     * Returns the execution with the given entity node id and process instance id.
     *
     * @param entityId the entity node id
     * @param piId     the process instance id
     * @return Execution
     */
    Execution findByEntityId(Long entityId, Long piId);

    /**
     * Returns the execution with the given entity node id-
     *
     * @param entity the entity node id
     * @return Execution
     */
    Execution findByEntityNodeId(Long entity);

    /**
     * Returns all executions which are following the given execution and which have the given state.
     *
     * @param execution the execution instance
     * @param state     the state instance
     * @return Set
     */
    Set<Execution> findFollowingByState(Execution execution, State state);

    /**
     * Returns the execution with the given escalation code.
     *
     * @param escalationCode the escalation code
     * @param piId           the process instance id
     * @return Execution
     */
    Execution findByEscalationCode(String escalationCode, String piId);

    /**
     * Returns all executions with the given ids.
     *
     * @param ids the ids
     * @return Set
     */
    Set<Execution> findByIds(@Param("ids") Collection<String> ids);

    /**
     * Returns all executions with the given node ids.
     *
     * @param ids the node ids
     * @return Set
     */
    Set<Execution> findByNodeIds(Collection<Long> ids);

    /**
     * Returns all event sub process executions of the given process instance.
     *
     * @param piId the process instance id
     * @return Set
     */
    Set<Execution> findAllEventSubProcessExecutions(String piId);

    /**
     * Returns all end event executions in wait state of the given process instance.
     *
     * @param piId the process instance id
     * @return Set
     */
    Set<Execution> findEndEventExecutionsInWaitState(String piId);

    /**
     * Returns the task execution with the given message reference and state.
     *
     * @param messageRef the message reference
     * @param state      the state instance
     * @return Execution
     */
    Execution findTaskExecutionByMessageAndState(String messageRef, String state);

    /**
     * Returns all compensate boundary event executions of the given process instance.
     *
     * @param piId the process instance id
     * @return Set
     */
    Set<Execution> findCompensateBoundaryEventExecutions(String piId);

    /**
     * Returns all unsuccessful compensate task executions of the given process instance.
     *
     * @param piId the process instance node id
     * @return Set
     */
    Set<Execution> findUnsuccessfulCompensateTaskExecutions(Long piId);

    /**
     * Returns the cancel end event execution in wait state of the given process instance.
     *
     * @param piId the process instance id
     * @return Execution
     */
    Execution findCancelEndEventExecutionInStateWait(String piId);

    /**
     * Returns the cancel end event execution of the given process instance with the given state.
     *
     * @param piId  the process instance id
     * @param state the state instance
     * @return Execution
     */
    Execution findCancelEndEventExecutionByState(String piId, String state);

    /**
     * Returns the cancel boundary event execution with the given execution id of the given process instance.
     *
     * @param executionId the execution node id
     * @param piId        the process instance id
     * @return Execution
     */
    Execution findCancelBoundaryEventExecution(Long executionId, String piId);

    /**
     * Returns all joining executions by the given entity id of the given process instance.
     *
     * @param piId     the process instance id
     * @param entityId the entity id
     * @return Set
     */
    Set<Execution> findJoiningExecutions(String piId, String entityId);

    /**
     * Synchronizes the gateway executions of the given process instance.
     *
     * @param piId the process instance id
     * @param gatewayId the gateway id
     */
    void synchronizeGatewayExecutions(String piId, String gatewayId);

}
