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

import java.util.List;

import akka.dispatch.Futures;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.arrow.model.BpmnEntity;
import org.arrow.model.BpmnNodeEntity;
import org.arrow.util.FutureUtil;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.message.EventMessage;
import scala.concurrent.Future;

/**
 * Adapter implementation which determines the relevant
 * {@link ExecutionInterceptor} used to initialize BPMN entities.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@Component
public class BpmnEntityInitializerAdapter {

    @Autowired
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private List<ExecutionInterceptor> initializers;

	/**
	 * Initializes the given {@link BpmnEntity} instance.
	 *
     * @param execution the execution instance
	 * @param entity the bpmn node entity instance
	 * @return BpmnEntity
	 */
	public BpmnNodeEntity beforeExecution(Execution execution, BpmnNodeEntity entity) {

		for (ExecutionInterceptor initializer : initializers) {
			if (initializer.supports(entity)) {
				return initializer.beforeExecution(execution, entity);
			}
		}
		return entity;
	}

	/**
	 * Initializes the given {@link BpmnEntity} instance after the execution of
	 * it.
	 * 
	 * @param execution the execution instance
	 * @param entity the bpmn node entity instance
	 * @return BpmnEntity
	 */
	public Future<Iterable<EventMessage>> afterExecution(Execution execution,
			BpmnNodeEntity entity) {

		for (ExecutionInterceptor initializer : initializers) {
			if (initializer.supports(entity)) {
				return initializer.afterExecution(execution, entity);
			}
		}
		return Futures.successful(FutureUtil.iterableOf());
	}

}
