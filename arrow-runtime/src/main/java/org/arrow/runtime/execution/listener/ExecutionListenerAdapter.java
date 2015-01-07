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

package org.arrow.runtime.execution.listener;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.service.ExecutionService;

/**
 * Classes which implements this interface are able to listen on
 * {@code Executable} method calls. This can be used to hook into the execution
 * process of BPMN entities.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
public class ExecutionListenerAdapter implements ExecutionListener {

    @GraphId
    @SuppressWarnings("unused")
    private Long nodeId;

	/**
	 * Listen to the BPMN entity execution call.
	 * 
	 * @param execution
	 *            the execution
	 * @param service
	 *            the service
	 */
	public void onExecute(Execution execution, ExecutionService service) {
        // do nothing
    }

	/**
	 * Listen to the BPMN entity finish call.
	 * 
	 * @param execution
	 *            the execution
	 * @param service
	 *            the service
	 */
	public void onFinish(Execution execution, ExecutionService service) {
        // do nothing
    }

}
