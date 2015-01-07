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

package org.arrow.runtime.api.task;

import org.arrow.runtime.execution.Execution;

/**
 * Classes which implements this interface can be executed by the process engine
 * during the execution of a BPMN service task.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public interface JavaDelegate {

    /**
     * Executes the external Java delegate code.
     *
     * @param execution the execution instance
     */
    void execute(Execution execution);

}
