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

package org.arrow.test;

import org.arrow.model.BpmnNodeEntity;
import org.arrow.runtime.RuntimeService;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.data.ExecutionRepository;
import org.arrow.runtime.execution.service.data.ProcessInstanceRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.Set;

/**
 * Workflow test utility class.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public final class WorkflowTestUtils {

	private WorkflowTestUtils() {
		super();
	}

	/**
	 * Asserts whether all BPMN entities of the given {@link ProcessInstance}
	 * has the state 'SUCCESS'.
	 * 
	 * @param execRepo
	 * @param pi
	 */
	public static void assertSuccessState(ExecutionRepository execRepo,
			ProcessInstance pi) {

		Set<Execution> executions = execRepo.findByProcessInstance(pi);
		Assert.notNull(executions);
		
		for (Execution execution : executions) {
			State state = execution.getState();
			Assert.isTrue(State.SUCCESS.compareTo(state) == 0);
		}
	}
	
	/**
	 * Asserts whether all BPMN entities of the given {@link ProcessInstance}
	 * has the state 'SUCCESS'.
	 * 
	 * @param execRepo
	 * @param pi
	 */
	public static void assertSuccessState(ExecutionRepository execRepo,
			ProcessInstance pi, String[] ids) {
		
		Set<Execution> executions = execRepo.findByProcessInstance(pi);
		Assert.notNull(executions);
		
		for (Execution execution : executions) {
			BpmnNodeEntity entity = (BpmnNodeEntity) execution.getEntity();
			State state = execution.getState();
			
			if (contains(ids, entity.getId())) {
				Assert.isTrue(State.SUCCESS.compareTo(state) == 0);
			}
		}
	}
	
	private static boolean contains(String[] array, String value) {
		for (String entry : array) {
			if (entry.equals(value)) {
				return true;
			}
		}
		return false;
	}

    public static void validWorkflow(String id, ApplicationContext ctx) throws Exception {

        final RuntimeService runtimeService = ctx.getBean(RuntimeService.class);
        final ExecutionRepository executionRepository = ctx.getBean(ExecutionRepository.class);

        // when a process is started by a process id
        Future<ProcessInstance> future;
        future = runtimeService.startProcessById(id);

        ProcessInstance pi = Await.result(future, Duration.Inf());

        // then the process should complete successfully
        pi.waitOnCompletion();
        org.junit.Assert.assertTrue(pi.isFinished());

        assertSuccessState(executionRepository, pi);
    }

    public static void validMessageWorkflow(String message, ApplicationContext ctx) throws Exception {

        final RuntimeService runtimeService = ctx.getBean(RuntimeService.class);
        final ExecutionRepository executionRepository = ctx.getBean(ExecutionRepository.class);

        // when a process is started by a process id
        Future<ProcessInstance> future;
        future = runtimeService.startProcessByMessage(message);

        ProcessInstance pi = Await.result(future, Duration.Inf());

        // then the process should complete successfully
        pi.waitOnCompletion();
        org.junit.Assert.assertTrue(pi.isFinished());

        assertSuccessState(executionRepository, pi);
    }

    public static void validSignalWorkflow(String signal, ApplicationContext ctx) throws Exception {

        final RuntimeService runtimeService = ctx.getBean(RuntimeService.class);
        final ExecutionRepository executionRepository = ctx.getBean(ExecutionRepository.class);

// when a process is started by a process id
        Future<Iterable<ProcessInstance>> future;
        future = runtimeService.startProcessBySignal(signal);

        Iterable<ProcessInstance> instances = Await.result(future, Duration.Inf());

        junit.framework.Assert.assertNotNull(instances);
        junit.framework.Assert.assertTrue(instances.iterator().hasNext());

        // then the process should complete successfully
        for (ProcessInstance pi : instances) {
            pi.waitOnCompletion();
            junit.framework.Assert.assertTrue(pi.isFinished());

            WorkflowTestUtils.assertSuccessState(executionRepository, pi);
        }
    }

    public static void validLatestWorkflow(String id, ApplicationContext ctx) throws Exception {
        final ProcessInstanceRepository processInstanceRepository = ctx.getBean(ProcessInstanceRepository.class);
        final ExecutionRepository executionRepository = ctx.getBean(ExecutionRepository.class);

        ProcessInstance pi;
        pi = processInstanceRepository.findLatestProcessInstance(id);
        junit.framework.Assert.assertNotNull(pi);

        Thread.sleep(250);
        WorkflowTestUtils.assertSuccessState(executionRepository, pi);
    }

}
