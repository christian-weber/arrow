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

package org.arrow.service.engine.execution.interceptor;

import akka.dispatch.Futures;
import org.arrow.model.BpmnNodeEntity;
import org.arrow.util.FutureUtil;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.message.EventMessage;
import scala.concurrent.Future;

/**
 * Abstract {@link ExecutionInterceptor} implementation class.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public abstract class AbstractExecutionInterceptor implements
		ExecutionInterceptor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BpmnNodeEntity beforeExecution(Execution execution, BpmnNodeEntity entity) {
		return entity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Iterable<EventMessage>> afterExecution(Execution execution,
			BpmnNodeEntity entity) {
		return Futures.successful(FutureUtil.iterableOf());
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(Object entity) {
		return true;
	}

}
