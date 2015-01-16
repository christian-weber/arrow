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

package org.arrow.model.task.impl;

import akka.dispatch.Futures;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.arrow.model.task.AbstractTask;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.api.task.JavaDelegate;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.service.ExecutionService;
import scala.concurrent.Future;

/**
 * BPMN 2 Service Task implementation used to execute Java methods.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("ServiceTask")
public class ServiceTask extends AbstractTask {

	private String delegate;

	private String serviceClass;
	private String expression;
	private String beanName;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Iterable<EventMessage>> executeTask(Execution execution, ExecutionService service) {
		
		// spring bean name delegate
		if (!isEmpty(beanName)) {
			JavaDelegate javaDelegate = service.getJavaDelegateByName(beanName);
			javaDelegate.execute(execution);
		}

		// spring expression delegate
		if (!isEmpty(expression)) {
			service.evaluateExpression(expression);
		}

		// class delegate
		if (!isEmpty(serviceClass)) {
			JavaDelegate javaDelegate = service.getJavaDelegateByClassName(serviceClass);
			javaDelegate.execute(execution);
		}

		// mark the task as finished
		finish(execution, service);

        return Futures.successful(iterableOf());
    }

	private boolean isEmpty(String beanName) {
		return beanName == null || beanName.length() == 0;
	}

    @SuppressWarnings("unused")
	public String getDelegate() {
		return delegate;
	}

    @SuppressWarnings("unused")
	public void setDelegate(String delegate) {
		this.delegate = delegate;
	}

    @SuppressWarnings("unused")
	public String getServiceClass() {
		return serviceClass;
	}

    @SuppressWarnings("unused")
	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
}
