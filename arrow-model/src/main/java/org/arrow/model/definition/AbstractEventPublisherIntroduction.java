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

package org.arrow.model.definition;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.arrow.runtime.execution.Executable;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.EventMessage;

@SuppressWarnings("serial")
public abstract class AbstractEventPublisherIntroduction extends DelegatingIntroductionInterceptor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object doProceed(MethodInvocation mi) throws Throwable {
		
		Method method = mi.getMethod();
		Class<?> dc = method.getDeclaringClass();
		if (dc.isAssignableFrom(Executable.class) && method.getName().equals("execute")) {
			Execution execution = (Execution) mi.getArguments()[0];
			ExecutionService service = (ExecutionService) mi.getArguments()[1];

            Logger.getLogger(getClass()).info("publish event");
			publishEvent(execution, service);
		}
		
		return mi.proceed();
	}
	
	protected abstract void publishEvent(Execution execution, ExecutionService service);

	protected Iterable<EventMessage>iterableOf(EventMessage...messages) {
		return Arrays.asList(messages);
	}
	
}
