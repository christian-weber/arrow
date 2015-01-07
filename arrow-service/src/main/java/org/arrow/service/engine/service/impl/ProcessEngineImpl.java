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

package org.arrow.service.engine.service.impl;

import org.arrow.data.neo4j.query.ExecutionQueryImpl;
import org.arrow.data.neo4j.query.ProcessInstanceQueryImpl;
import org.arrow.data.neo4j.query.TaskQueryImpl;
import org.arrow.model.api.TaskQuery;
import org.arrow.runtime.api.query.ExecutionQuery;
import org.arrow.runtime.api.query.ProcessInstanceQuery;
import org.arrow.runtime.execution.service.data.ExecutionRepository;
import org.arrow.service.engine.service.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Component;

/**
 * {@link ProcessEngine} implementation class.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@Component
public class ProcessEngineImpl implements ProcessEngine {

	@Autowired
	private Neo4jTemplate template;
	
	@Autowired
	private ExecutionRepository executionRepository;
	
	@Autowired
	private ApplicationContext context;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TaskQuery taskQuery() {
		return TaskQueryImpl.create(context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExecutionQuery executionQuery() {
		return ExecutionQueryImpl.create(context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProcessInstanceQuery processInstanceQuery() {
		return ProcessInstanceQueryImpl.create(context);
	}

}
