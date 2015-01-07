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

import org.arrow.runtime.api.task.TaskSpecification;

/**
 * The task repository definition.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public interface TaskRepository {

    /**
     * Returns a collection of {@code ReceiveTask} instances by the given messageRef.
     *
     * @param messageRef  the message reference value
     * @param instantiate the instantiate flag
     * @return TaskSpecification
     */
    <T extends TaskSpecification> T findReceiveTask(String messageRef, boolean instantiate);

    /**
     * Returns a collection of {@code ReceiveTask} instances by the given messageRef.
     *
     * @param piNodeId the process node id
     * @param taskId   the task id
     * @return TaskSpecification
     */
    <T extends TaskSpecification> T findAdHocTask(Long piNodeId, String taskId);

}
