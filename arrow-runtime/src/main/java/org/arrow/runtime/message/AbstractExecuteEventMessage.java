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

package org.arrow.runtime.message;

import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ExecutionGroup;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.message.AbstractEntityEventMessage;
import org.arrow.runtime.message.ExecuteEventMessage;

/**
 * Abstract node class definition. Classes which implements this interface could
 * be executed by a NodeWorker instance.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public abstract class AbstractExecuteEventMessage extends AbstractEntityEventMessage implements ExecuteEventMessage {

	/**
	 * Instantiates a new node.
	 *
	 * @param entity
	 *            the entity
	 * @param processInstance
	 *            the process instance
	 */
	public AbstractExecuteEventMessage(BpmnNodeEntitySpecification entity, ProcessInstance processInstance) {
        super(entity, processInstance);
	}

    public AbstractExecuteEventMessage(BpmnNodeEntitySpecification entity, ProcessInstance processInstance, ExecutionGroup executionGroup) {
        super(entity, processInstance, executionGroup);
    }

	/**
	 * Instantiates a new node.
	 *
	 * @param execution
	 *            the execution
	 */
	public AbstractExecuteEventMessage(Execution execution) {
        super(execution);
	}

}
