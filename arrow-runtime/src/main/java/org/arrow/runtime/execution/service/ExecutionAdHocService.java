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

import org.arrow.runtime.execution.ProcessInstance;

/**
 * Execution service facade for ad hoc services.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public interface ExecutionAdHocService {

    /**
     * Executes the task with the given taskId within the given ad hoc process.
     *
     * @param pi      the process instance
     * @param adHocId the adHoc sub process id
     * @param taskId  the task id
     */
    void execute(ProcessInstance pi, String adHocId, String taskId);

    /**
     * Finishes the ad hoc process with the given ad hoc process instance.
     *
     * @param pi      the process instance
     * @param adHocId the ad hoc sub process id
     */
    void finish(ProcessInstance pi, String adHocId);

}
