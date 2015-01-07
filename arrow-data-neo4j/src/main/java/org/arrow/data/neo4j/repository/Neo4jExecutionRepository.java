/*
 * Copyright 2014 Christian Weber
 *
 * This file is build on Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.arrow.data.neo4j.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.data.ExecutionRepository;

import java.util.Collection;
import java.util.Set;

/**
 * {@link GraphRepository} definition for {@link Execution} instances.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public interface Neo4jExecutionRepository extends GraphRepository<Execution>, ExecutionRepository {

    /**
     * Returns all {@link Execution} instances by the given
     * {@link ProcessInstance} instance.
     *
     * @param processInstance the process instance
     * @return Set
     */
    @Override
    @Query("start pi=node({0}) "
            + "match (pi)<-[:PROCESS_INSTANCE]-(execution) return execution")
    Set<Execution> findByProcessInstance(ProcessInstance processInstance);

    /**
     * Returns all {@link Execution} instances by the given conditional bean
     * name.
     *
     * @param beanName the name of the bean instance
     * @return Set
     */
    @Override
    @Query("match (condition:Condition)<-[:CONDITION]-(definition)<-[:EVENT_DEFINITION]-(event)-[:EXECUTION]->(execution) "
            + "where condition.beanName = {0} and execution.state = 'WAITING' return execution")
    Set<Execution> findAllByBusinessCondition(String beanName);

    /**
     * Returns all {@link Execution} instances by the given signal reference.
     *
     * @param signalRef the signal reference value
     * @return Set
     */
    @Override
    @Query("match (definition:SignalEventDefinition)<-[:EVENT_DEFINITION]-(event)-[:EXECUTION]->(execution) "
            + "where definition.signalRef = {0} and execution.state = 'WAITING' "
            + "return execution")
    Set<Execution> findAllBySignalRef(String signalRef);

    /**
     * Returns the {@link Execution} instance by the given message reference.
     *
     * @param messageRef the message reference value
     * @return Execution
     */
    @Override
    @Query("match (definition:MessageEventDefinition)<-[:EVENT_DEFINITION]-(event)-[:EXECUTION]->(execution) "
            + "where definition.messageRef = {0} and execution.state = 'WAITING' "
            + "return execution "
            + "ORDER BY ID(execution) DESC LIMIT 1")
    Execution findByMessageRef(String messageRef);

    @Override
    @Query("match (definition:ErrorEventDefinition)<-[:EVENT_DEFINITION]-(boundaryEvent)<-[:BOUNDARY_EVENT]-(task)-[:EXECUTION]->(taskExecution)-[:PROCESS_INSTANCE]->(pi1),"
            + "(boundaryEvent)-[:EXECUTION]-(execution)-[:PROCESS_INSTANCE]->(pi2) "
            + "where execution.state = 'WAITING' and taskExecution.id = {0} and pi1.id = pi2.id "
            + "return execution")
    Execution findByErrorEvent(String executionId);

    @Override
    @Query("start entity=node({0}) "
            + "match (entity)-[:EXECUTION]->(execution)-[:PROCESS_INSTANCE]->(pi) "
            + "where pi.id = {1} return execution ORDER BY ID(execution) LIMIT 1")
    Execution findByEntity(BpmnNodeEntitySpecification entity, String piId);

    @Override
    @Query("START entity=node({0}) "
            + "match (entity)-[:EXECUTION]->(execution)-[:PROCESS_INSTANCE]->(pi) "
            + "where ID(pi) = {1} return execution ORDER BY ID(execution) LIMIT 1")
    Execution findByEntityId(Long entityId, Long piId);

    @Override
    @Query("match (entity)-[:EXECUTION]->(execution)-[:PROCESS_INSTANCE]->(pi) "
            + " where ID(entity) = {0} "
            + "return execution LIMIT 1")
    Execution findByEntityNodeId(Long entity);

    @Override
    @Query("start execution1=node({0}) "
            + "match (execution1)<-[:EXECUTION]-(entity)-[:SEQUENCE_FLOW]->(next)-[:EXECUTION]->(execution2) "
            + "where execution2.state = {1} "
            + "with execution1, execution2 "
            + "match (execution1)-[:PROCESS_INSTANCE]-(pi1) "
            + "with execution2, pi1 "
            + "match (execution2)-[:PROCESS_INSTANCE]-(pi2) "
            + "where pi1.id = pi2.id "
            + "return execution2")
    Set<Execution> findFollowingByState(Execution execution, State state);

    /**
     * Returns an {@link Execution} instance by the given escalation code.
     *
     * @param escalationCode the escalation code value
     * @param piId           the process instance id
     * @return Execution
     */
    @Override
    @Query("match (escalation)<-[:ESCALATION_REF]-(definition:EscalationEventDefinition)<-[:EVENT_DEFINITION]-(event)-[:EXECUTION]->(execution) "
            + "where escalation.escalationCode = {0} "
            + "and execution.state = 'WAITING' "
            + "with execution "
            + "match (execution)-[:PROCESS_INSTANCE]->(pi) "
            + "where pi.id = {1} "
            + "return execution")
    Execution findByEscalationCode(String escalationCode, String piId);

    @Override
    @Query("match (execution:Execution) where execution.id IN {ids} return execution")
    Set<Execution> findByIds(@Param("ids") Collection<String> ids);

    @Override
//    @Query("START pi=node:entityId(id={0}) "
//            + "match (pi)<-[:PROCESS_INSTANCE]-(execution)<-[:EXECUTION]-(subProcess:SubProcess{triggeredByEvent:true}) "
//            + " return execution")
    @Query("match (subProcess:SubProcess)-[:EXECUTION]->(execution)-[:PROCESS_INSTANCE]->(pi) "
            + "where pi.id = {0} and subProcess.triggeredByEvent = true return execution")
    Set<Execution> findAllEventSubProcessExecutions(String piId);

    @Override
    @Query("match (event:EndEvent)-[:EXECUTION]->(execution)-[:PROCESS_INSTANCE]->(pi) "
            + " where pi.id = {0}"
            + " and execution.state = 'WAITING' "
            + " return distinct execution")
    Set<Execution> findEndEventExecutionsInWaitState(String piId);

    @Query("START entity=node:entityId(id={1}) "
            + "match (entity)-[:EXECUTION]->(execution)-[:PROCESS_INSTANCE]->(pi) "
            + "where pi.id = {0} return execution LIMIT 1")
    Execution findByProcessInstanceAndEntityId(String piId, String entityId);

    @Override
    @Query("match(task:ReceiveTask)-[:EXECUTION]->(execution)-[:PROCESS_INSTANCE]->(pi) " +
            "where has(task.messageRef) " +
            "and task.messageRef = {0} " +
//            "and task.state = {1} " +
            "return execution " +
            "ORDER BY ID(execution) DESC LIMIT 1")
    Execution findTaskExecutionByMessageAndState(String messageRef, String state);


    @Override
    @Query("match (definition:CompensateEventDefinition)<-[:EVENT_DEFINITION]-(boundaryEvent)-[:EXECUTION]->(execution)-[:PROCESS_INSTANCE]->(pi) "
            + "where pi.id = {0} "
            + "return execution")
    Set<Execution> findCompensateBoundaryEventExecutions(String piId);

    @Override
    @Query("START pi=node({0}) "
            + "match (pi)<-[:PROCESS_INSTANCE]-(execution{isForCompensation:true}) "
            + "return execution")
    Set<Execution> findUnsuccessfulCompensateTaskExecutions(Long piId);

    @Override
    @Query("match (endEvent:CancelEndEvent)-[:EXECUTION]->(execution)-[:PROCESS_INSTANCE]->(pi) "
            + "where pi.id = {0} "
            + "return execution")
    Execution findCancelEndEventExecutionInStateWait(String piId);

    @Override
    @Query("match (endEvent:CancelEndEvent)-[:EXECUTION]->(execution)-[:PROCESS_INSTANCE]->(pi) "
            + "where pi.id = {0} and execution.state = 'SUCCESS' "
            + "return execution LIMIT 1")
    Execution findCancelEndEventExecutionByState(String piId, String state);

    @Override
    @Query("START attachedExecution=node({0}) "
            + "match (attachedExecution)<-[:EXECUTION]-(entity)-[:BOUNDARY_EVENT]->(boundaryEvent:CancelBoundaryEvent)-[:EXECUTION]->(execution)-[:PROCESS_INSTANCE]->(pi) "
            + "where pi.id = {1} "
            + "return execution LIMIT 1")
    Execution findCancelBoundaryEventExecution(Long executionId, String piId);

    @Override
    @Query("START entity=node:entityId(id={1})"
            + "match (entity)-[:EXECUTION]->(execution)-[:PROCESS_INSTANCE]->(pi) "
            + "where pi.id = {0} and execution.state = 'JOINING'"
            + "return execution")
    Set<Execution> findJoiningExecutions(String piId, String entityId);

    @Override
    @Query("START entity=node:entityId(id={1})"
            + "match (entity)-[:EXECUTION]->(execution)-[:PROCESS_INSTANCE]->(pi) "
            + "where pi.id = {0} and execution.state <> 'SUCCESS'"
            + "set execution.state = 'SUCCESS'")
//    @Query("match (entity{id:{1}})-[:EXECUTION]->(execution)-[:PROCESS_INSTANCE]->(pi) "
//            + "where pi.id = {0} and execution.state <> 'STATE' "
//            + "set execution.state = 'SUCCESS'")
    void synchronizeGatewayExecutions(String piId, String gatewayId);

    @Override
    @Query("match (execution:Execution) where ID(execution) IN {ids} return execution")
    Set<Execution> findByNodeIds(@Param("ids") Collection<Long> ids);



}
