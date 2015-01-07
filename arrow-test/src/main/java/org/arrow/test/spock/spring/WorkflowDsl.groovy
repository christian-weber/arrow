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

package org.arrow.test.spock.spring

import org.springframework.context.ApplicationContext
import org.springframework.util.Assert
import org.arrow.data.neo4j.repository.Neo4jExecutionGroupRepository
import org.arrow.data.neo4j.repository.Neo4jExecutionRepository
import org.arrow.data.neo4j.repository.Neo4jProcessInstanceRepository
import org.arrow.runtime.RuntimeService
import org.arrow.runtime.api.event.BusinessCondition
import org.arrow.runtime.api.event.BusinessCondition.BusinessConditionContext
import org.arrow.runtime.message.EventMessage
import org.arrow.runtime.execution.Execution
import org.arrow.runtime.execution.ProcessInstance
import org.arrow.runtime.execution.State
import org.arrow.runtime.message.impl.DefaultFinishEventMessage
import org.arrow.runtime.execution.service.ExecutionService
import org.arrow.test.SpringWorkflowTestExecutionListener
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration

/**
 * This class defines the DSL which can be used in a test to start and manipulate
 * process executions. The DSL supports fully access to the process engine so that
 * workflow operations could be handled by the use of expressions which are offered
 * by the DSL.
 *
 * @since 1.0.0
 * @author christian.weber
 */
@SuppressWarnings("GroovyAssignabilityCheck")
public class WorkflowDsl {

    /**
     * Starts a process instance by the given id.
     *
     * @param id the process id
     * @return ProcessInstance
     */
    public static ProcessInstance startById(String id) {
        def future = runtimeService().startProcessById(id);
        return Await.result(future, Duration.Inf());
    }

    /**
     * Starts a process instance by the given id.
     *
     * @param id the process id
     * @param vars the process variables
     * @return ProcessInstance
     */
    public static ProcessInstance startById(String id, Map<String, Object> vars) {
        def future = runtimeService().startProcessById(id, vars);
        return Await.result(future, Duration.Inf());
    }

    /**
     * Starts a process instance by the given signal reference.
     *
     * @param signal the signal reference
     * @return ProcessInstance
     */
    public static Iterable<ProcessInstance> startBySignal(String signal) throws Exception {
        def future = runtimeService().startProcessBySignal(signal);
        Iterable<ProcessInstance> instances = Await.result(future, Duration.Inf());

        Assert.notNull(instances);
        Assert.isTrue(instances.iterator().hasNext());

        return instances;
    }

    /**
     * Starts a process instance by the given message reference.
     *
     * @param message the message reference
     * @return ProcessInstance
     */
    public static ProcessInstance startByMessage(String message) {
        Future<ProcessInstance> future;
        future = runtimeService().startProcessByMessage(message);

        return Await.result(future, Duration.Inf());
    }

    /**
     * Starts a process instance by the a business condition with the given name.
     * The business condition instance is gained by an application context lookup.
     *
     * @param beanName the beanName of the business condition to evaluate
     */
    public static void startByCondition(String beanName) throws Exception {
        BusinessCondition condition = getApplicationContext().getBean(beanName, BusinessCondition.class);
        condition.evaluate(new BusinessConditionContext());

        Thread.sleep 500
    }

    /**
     * Starts a process instance by the given business condition instance.
     *
     * @param condition the business condition instance
     */
    public static void startByCondition(BusinessCondition condition) {
        def messages = runtimeService().publishConditionEvent(condition, new BusinessConditionContext());
        Await.result(messages, Duration.Inf());
    }

    /**
     * Publishes a signal event with the given signal reference.
     * @param signal the signal reference
     */
    public static void signal(String signal) {
        def future = runtimeService().signal(signal);
        Await.result(future, Duration.Inf());
    }

    /**
     * Publishes a message event with the given message reference.
     * @param message the message reference
     */
    public static void message(String message) {
        def future = runtimeService().message(message)
        Await.result(future, Duration.Inf());
    }

    /**
     * Publishes a condition event with the given business condition instance.
     * @param condition the business condition instance
     */
    public static void condition(BusinessCondition condition) {
        Future<Iterable<EventMessage>> future = runtimeService().publishConditionEvent(condition, new BusinessConditionContext())
        Await.result(future, Duration.Inf());
    }


    /**
     * Publishes a message event with the given message reference.
     * @param message the message reference
     */
    public static void finish(pi, entityId) {
        Execution execution = executionRepository().findByProcessInstanceAndEntityId(pi.id, entityId)
        def msg = new DefaultFinishEventMessage(execution)

        runtimeService().publishEventMessage(msg)
    }

    /**
     * Blocks the current thread until the given process instance is complete.
     * @param pi the process instance
     *
     * @return ProcessInstance
     */
    public ProcessInstance wait(ProcessInstance pi) {
        pi.waitOnCompletion();
        return pi;
    }

    /**
     * Blocks the current thread until the given process instances are complete.
     * @param pis the process instances
     *
     * @return Iterable < ProcessInstance >
     */
    public Iterable<ProcessInstance> wait(Iterable<ProcessInstance> pis) {
        pis.each { wait it }
        return pis;
    }

    /**
     * Returns the latest process instance with the given id.
     *
     * @param id
     * @return
     */
    public static ProcessInstance latestProcessInstance(String id) {
        def pi = processInstanceRepository().findLatestProcessInstance(id);
        return notNull(pi)
    }

    /**
     * Asserts if all execution states of the given process instance are in state SUCCESS.
     *
     * @param pi
     * @param ids
     */
//    public static void assertSuccess(ProcessInstance pi) throws Exception {
//        Set<Execution> executions = executionRepository().findByProcessInstance(pi);
//        assertState(State.SUCCESS, executions);
//    }

    /**
     * Asserts if all execution states of the given list of process instances are in state SUCCESS.
     *
     * @param pi
     * @param ids
     */
    public static void assertSuccess(Iterable<ProcessInstance> pis) throws Exception {
        pis.each { assertSuccess it }
    }

    /**
     * Asserts if all execution states with the given ids are in state SUCCESS.
     *
     * @param pi
     * @param ids
     */
    public static void assertSuccess(ProcessInstance pi, String... ids) throws Exception {
        def executions = executionRepository().findByProcessInstance(pi);
        assertState(State.SUCCESS, executions, ids);
    }

    /**
     * Asserts if all execution states with the given ids are not in state SUCCESS.
     *
     * @param pi
     * @param ids
     */
    public static void assertSkipped(ProcessInstance pi, String... ids) {
        def executions = executionRepository().findByProcessInstance(pi);
        assertStateNot(State.SUCCESS, executions, ids);
    }

    /**
     * Asserts if all execution states with the given ids are not in state SUCCESS.
     *
     * @param pi
     * @param ids
     */
    public static void assertSuspend(ProcessInstance pi, String... ids) {
        def executions = executionRepository().findByProcessInstance(pi);
        assertStateNot(State.SUSPEND, executions, ids);
    }

    public static void executionGroups(ProcessInstance pi, Closure closure) {
        def executionGroups = executionGroupRepository().findByProcessInstance(pi)
        closure(executionGroups)
    }

    public static void variables(ProcessInstance pi, Closure closure) {
        def executionGroups = executionGroupRepository().findByProcessInstance(pi)
        closure(executionGroups)
    }

    /**
     * Asserts if the state of all given executions with the given entity ids are in state SUCCESS.
     * @param reference
     * @param executions
     * @param ids
     */
    private static assertState(State reference, Set<Execution> executions, String... ids) {
        def closure1 = { ids.length == 0 ? true : ids?.contains(it?.entity?.id) }
        def closure2 = { assert reference.compareTo(it.state) == 0, "state of $it.entity is not $reference but $it.state" }
        assertNotEmpty(executions?.findAll(closure1))?.each(closure2)
    }

    /**
     * Asserts if the state of all given executions with the given entity ids are not in state SUCCESS.
     * @param reference
     * @param executions
     * @param ids
     */
    private static assertStateNot(State reference, Set<Execution> executions, String... ids) {
        def closure1 = { ids.length == 0 ? true : ids?.contains(it?.entity?.id) }
        def closure2 = { Assert.isTrue reference.compareTo(it.state) != 0, "state of $it.entity is $reference" }
        executions?.findAll(closure1)?.each(closure2)
    }

    /**
     * Returns the application context.
     * @return ApplicationContext
     */
    private static ApplicationContext getApplicationContext() {
        return SpringWorkflowTestExecutionListener.CONTEXT_HOLDER.get();
    }

    /**
     * Returns the ExecutionRepository instance from the application context.
     * @return ExecutionRepository
     */
    private static Neo4jExecutionRepository executionRepository() {
        return getApplicationContext().getBean(Neo4jExecutionRepository.class);
    }

    /**
     * Returns the ExecutionGroupRepository instance from the application context.
     * @return ExecutionGroupRepository
     */
    private static Neo4jExecutionGroupRepository executionGroupRepository() {
        return getApplicationContext().getBean(Neo4jExecutionGroupRepository.class);
    }

    /**
     * Returns the ProcessInstanceRepository instance from the application context.
     * @return ProcessInstanceRepository
     */
    private static Neo4jProcessInstanceRepository processInstanceRepository() {
        return getApplicationContext().getBean(Neo4jProcessInstanceRepository.class);
    }

    /**
     * Returns the RuntimeService instance from the application context.
     * @return RuntimeService
     */
    private static RuntimeService runtimeService() {
        return getApplicationContext().getBean(RuntimeService.class);
    }

    /**
     * Returns the RuntimeService instance from the application context.
     * @return RuntimeService
     */
    private static ExecutionService executionService() {
        return getApplicationContext().getBean(ExecutionService.class);
    }

    /**
     * Asserts if the given instance is not null.
     * @param instance the instance to assert
     *
     * @return the instance
     */
    def static notNull(instance) {
        Assert.notNull(instance)
        return instance
    }

    private static Collection assertNotEmpty(Collection collection) {
        Assert.isTrue(collection.size() > 0, "the collection must not be empty")
        return collection
    }

}
