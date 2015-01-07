/*
 * Copyright 2014 Christian Weber
 *
 * This file is build on Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.arrow.data.neo4j.query;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.arrow.data.neo4j.query.cypher.CypherCriteria;
import org.neo4j.helpers.collection.IteratorUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.conversion.QueryResultBuilder;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.data.neo4j.support.query.CypherQueryEngine;
import org.arrow.runtime.api.query.ProcessInstanceQuery;
import org.arrow.runtime.execution.ProcessInstance;

/**
 * {@link ProcessInstanceQuery} implementation used to query
 * {@link ProcessInstance} instances.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class ProcessInstanceQueryImpl implements ProcessInstanceQuery {

	private final ApplicationContext context;

	private final Set<String> processInstanceIds = new HashSet<>();
	private final Set<String> signalRefs = new HashSet<>();
	private final Set<String> messageRefs = new HashSet<>();
	private final Set<String> keys = new HashSet<>();

	private ProcessInstanceQueryImpl(ApplicationContext context) {
		this.context = context;
	}

	/**
	 * Creates a new {@link ProcessInstanceQuery} instance.
	 * 
	 * @param context the application context
	 * @return ProcessInstanceQuery
	 */
	public static ProcessInstanceQuery create(ApplicationContext context) {
		return new ProcessInstanceQueryImpl(context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProcessInstanceQuery processInstanceId(String id) {
		this.processInstanceIds.add(id);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProcessInstanceQuery signalEventDefinition(String signalRef) {
		this.signalRefs.add(signalRef);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProcessInstanceQuery messageEventDefinition(String messageRef) {
		this.messageRefs.add(messageRef);
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProcessInstanceQuery key(String key) {
		this.keys.add(key);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public ProcessInstance singleResult() {
		Map<String, Object> params = new HashMap<>();
		String statement = toCypher(params);

		Result<?> result = getEngine().query(statement, params);
		QueryResultBuilder<?> builder = (QueryResultBuilder<?>) result;

		return builder.to(ProcessInstance.class).single();
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterable<ProcessInstance> list() {
		Map<String, Object> params = new HashMap<>();
		String statement = toCypher(params);

		Result<?> result = getEngine().query(statement, params);
		QueryResultBuilder<?> builder = (QueryResultBuilder<?>) result;

		Iterator<ProcessInstance> iterator = builder.to(ProcessInstance.class)
				.iterator();
		return IteratorUtil.asIterable(iterator);
	}

	private CypherQueryEngine getEngine() {
		Neo4jTemplate template = context.getBean(Neo4jTemplate.class);
		return template.queryEngineFor();
	}

	private String toCypher(Map<String, Object> params) {

		CypherCriteria c = new CypherCriteria();

		c.match("(pi:ProcessInstance)<-[:PROCESS_INSTANCE]->(execution)");
		c.match("(execution)<-[:EXECUTION]-(node)");

		int i = 0;
		for (String piId : processInstanceIds) {
			String key = "piId" + i++;
			c.where("pi.id = {" + key + "} ");
			params.put(key, piId);
		}

		int j = 0;
		for (String signal : signalRefs) {
			String key = "signal" + j++;
			c.where("node.signalRef = {" + key + "} ");
			params.put(key, signal);
		}

		int k = 0;
		for (String msg : messageRefs) {
			String key = "message" + k++;
			c.where("node.messageRef = {" + key + "} ");
			params.put(key, msg);
		}
		
		int l = 0;
		for (String entry : keys) {
			String key = "key" + l++;
			c.where("pi.key = {" + key + "} ");
			params.put(key, entry);
		}

		c.close("return pi");

		return c.toCypher();
	}

}
