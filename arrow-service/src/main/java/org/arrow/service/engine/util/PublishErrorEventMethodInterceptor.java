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

package org.arrow.service.engine.util;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.logger.LoggerFacade;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.impl.ErrorEventMessage;
import org.arrow.util.FutureUtil;
import scala.concurrent.Future;

/**
 * AOP advice implementation used to publish error events in case of 'task'
 * errors.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class PublishErrorEventMethodInterceptor implements MethodInterceptor {

	private final static LoggerFacade LOGGER = new LoggerFacade(PublishErrorEventMethodInterceptor.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		try {
			return mi.proceed();
		} catch (Throwable ex) {
			LOGGER.error(ex);
			return handleException(mi, ex);
		}
	}
	
	private Future<Iterable<EventMessage>> handleException(MethodInvocation mi, Throwable ex) {
		final Execution taskExecution = (Execution) mi.getArguments()[0];
		final ExecutionService service = (ExecutionService) mi.getArguments()[1];

		// update the task execution
		taskExecution.setState(State.FAILURE);
		service.saveEntity(taskExecution);

		// publish the error event
		String execId = taskExecution.getId();
		Execution eventExecution = service.data().execution().findByErrorEvent(execId);

		// prepare the event message result
		EventMessage message = new ErrorEventMessage(eventExecution, ex);
		return FutureUtil.result(message);
	}

}
