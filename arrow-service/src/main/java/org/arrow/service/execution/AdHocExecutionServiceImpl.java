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

package org.arrow.service.execution;

import akka.actor.ActorSystem;
import org.spockframework.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.arrow.model.task.Task;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.execution.service.ExecutionAdHocService;
import org.arrow.runtime.execution.service.data.ExecutionRepository;
import org.arrow.runtime.execution.service.data.ProcessInstanceRepository;
import org.arrow.runtime.execution.service.data.TaskRepository;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.ExecuteEventMessage;
import org.arrow.runtime.message.FinishEventMessage;
import org.arrow.runtime.message.impl.DefaultExecuteEventMessage;
import org.arrow.runtime.message.impl.DefaultFinishEventMessage;
import org.arrow.service.engine.concurrent.dispatch.onsuccess.PublishEventMessagesOnSuccess;
import org.arrow.util.FutureUtil;
import scala.concurrent.Future;

import java.util.Arrays;

/**
 * {@link ExecutionAdHocService} implementation.
 *
 * @since 1.0.0
 * @author christian.weber
 */
@Service
public class AdHocExecutionServiceImpl implements ExecutionAdHocService {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ExecutionRepository executionRepository;

    @Autowired
    private ActorSystem actorSystem;

    @Autowired
    private ProcessInstanceRepository processInstanceRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(ProcessInstance pi, String adHocId, String taskId) {

        ProcessInstance adHocPi = processInstanceRepository.findAdHocSubProcess(pi.getNodeId(), adHocId);

        Task task = taskRepository.findAdHocTask(adHocPi.getNodeId(), taskId);

        Assert.notNull(task);
        ExecuteEventMessage msg = new DefaultExecuteEventMessage(task, adHocPi);

        Future<Iterable<EventMessage>> future =  FutureUtil.result(msg);
        future.onSuccess(new PublishEventMessagesOnSuccess(applicationContext), actorSystem.dispatcher());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finish(ProcessInstance pi, String adHocId) {

        ProcessInstance adHocPi = processInstanceRepository.findAdHocSubProcess(pi.getNodeId(), adHocId);

        Long processNodeId = adHocPi.getProcess().getNodeId();
        Long parentProcessNodeId = adHocPi.getParentProcessInstance().getNodeId();

        Execution execution = executionRepository.findByEntityId(processNodeId, parentProcessNodeId);

        FinishEventMessage msg = new DefaultFinishEventMessage(execution);
        PublishEventMessagesOnSuccess publisher = new PublishEventMessagesOnSuccess(applicationContext);

        publisher.onSuccess(Arrays.asList(msg));
    }

}
