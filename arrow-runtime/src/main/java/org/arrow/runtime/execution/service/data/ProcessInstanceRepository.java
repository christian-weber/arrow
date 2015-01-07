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

import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ProcessInstance;

/**
 * The process instance repository definition.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public interface ProcessInstanceRepository {

    /**
     * Returns the latest process instance of the process with the given key.
     *
     * @param key the process key
     * @return ProcessInstance
     */
    ProcessInstance findLatestProcessInstance(String key);

    /**
     * Returns the process instance with the given process instance id.
     *
     * @param piId the process instance id
     * @return ProcessInstance
     */
    ProcessInstance findById(String piId);

    /**
     * Returns the sub process instance by the given sub process execution.
     *
     * @param subProcessExecution the sub process execution instance
     * @return ProcessInstance
     */
    ProcessInstance findSubProcessInstance(Execution subProcessExecution);

    /**
     * Returns the process instance by the given process instance node id.
     *
     * @param id the process instance node id
     * @return ProcessInstance
     */
    ProcessInstance findOne(Long id);

    /**
     * Returns the ad hoc sub process instance by the given super process instance node id and ad hoc sub process id.
     *
     * @param piNodeId the super process instance node id
     * @param adHocId the ad hoc sub process id
     * @return ProcessInstance
     */
    ProcessInstance findAdHocSubProcess(Long piNodeId, String adHocId);

}
