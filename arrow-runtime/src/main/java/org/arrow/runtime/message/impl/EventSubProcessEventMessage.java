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

package org.arrow.runtime.message.impl;

import org.springframework.context.ApplicationEvent;
import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.message.AbstractExecuteEventMessage;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.message.event.ExecuteApplicationEvent;
import org.arrow.runtime.message.SubProcessEventMessage;

/**
 * Immutable message which starts an event based sub process.
 * While the sub process runs
 * within the parent process instance the elements within the sub process run
 * within the isolated sub process instance.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class EventSubProcessEventMessage extends AbstractExecuteEventMessage implements SubProcessEventMessage {

	public EventSubProcessEventMessage(BpmnNodeEntitySpecification subProcess, ProcessInstance pi) {
		super(subProcess, pi);
	}

}
