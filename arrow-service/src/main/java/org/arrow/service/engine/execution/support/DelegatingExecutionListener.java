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

package org.arrow.service.engine.execution.support;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.listener.ExecutionListener;
import org.arrow.runtime.execution.service.ExecutionService;

/**
 * {@link ExecutionListener} implementation used to delegate to java classes.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("DelegatingExecutionListener")
public class DelegatingExecutionListener implements ExecutionListener {

	private final String className;

	public DelegatingExecutionListener(String className) {
		this.className = className;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onExecute(Execution execution, ExecutionService service) {
		ExecutionListener listener = getExecutionListener(service);
		listener.onExecute(execution, service);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onFinish(Execution execution, ExecutionService service) {
		ExecutionListener listener = getExecutionListener(service);
		listener.onFinish(execution, service);
	}

	/**
	 * Returns the {@link ExecutionListener} with the configured bean name.
	 * 
	 * @param service
	 * @return ExecutionListener
	 */
	private ExecutionListener getExecutionListener(ExecutionService service) {
		try {
			Class<?> cls = Class.forName(className);
			return (ExecutionListener) cls.newInstance();
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("could not instantiate to class", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("could not instantiate to class", e);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("could not instantiate to class", e);
		}
	}

}
