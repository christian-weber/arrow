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

import org.arrow.data.neo4j.query.cypher.CypherCriteria;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.helpers.collection.IteratorUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.conversion.QueryResultBuilder;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.data.neo4j.support.query.CypherQueryEngine;
import org.arrow.runtime.api.query.ExecutionQuery;
import org.arrow.runtime.execution.Execution;

import java.util.*;

/**
 * {@link ExecutionQuery} implementation class.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class ExecutionQueryImpl implements ExecutionQuery {

	private final GraphDatabaseService service;
	private final ApplicationContext context;

	private final Set<String> processInstanceIds = new HashSet<>();
	private final Set<String> signalRefs = new HashSet<>();
	private final Set<String> messageRefs = new HashSet<>();

	private ExecutionQueryImpl(ApplicationContext context) {
		this.context = context;
		this.service = context.getBean(GraphDatabaseService.class);
	}

	/**
	 * Creates a new {@link ExecutionQuery} instance.
	 * 
	 * @param context the application context
	 * @return ExecutionQuery
	 */
	public static ExecutionQuery create(ApplicationContext context) {
		return new ExecutionQueryImpl(context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExecutionQuery processInstanceId(String id) {
		this.processInstanceIds.add(id);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExecutionQuery signalEventDefinition(String signalRef) {
		this.signalRefs.add(signalRef);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExecutionQuery messageEventDefinition(String messageRef) {
		this.messageRefs.add(messageRef);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
    @SuppressWarnings("unused")
	public Execution singleResult() {
		try (Transaction trx = service.beginTx()) {
			Map<String, Object> params = new HashMap<>();
			String statement = toCypher(params);

			Result<?> result = getEngine().query(statement, params);
			QueryResultBuilder<?> builder = (QueryResultBuilder<?>) result;

			return builder.to(Execution.class).single();
		}

    }

	/**
	 * {@inheritDoc}
	 */
	public Iterable<Execution> list() {
		Map<String, Object> params = new HashMap<>();
		String statement = toCypher(params);

		Result<?> result = getEngine().query(statement, params);
		QueryResultBuilder<?> builder = (QueryResultBuilder<?>) result;

		Iterator<Execution> iterator = builder.to(Execution.class).iterator();
		return IteratorUtil.asIterable(iterator);
	}

	private CypherQueryEngine getEngine() {
		Neo4jTemplate template = context.getBean(Neo4jTemplate.class);
		return template.queryEngineFor();
	}

	private String toCypher(Map<String, Object> params) {

		CypherCriteria c = new CypherCriteria();

		c.match("(execution:Execution)-[:PROCESS_INSTANCE]->(pi)");

		if (!signalRefs.isEmpty() || !messageRefs.isEmpty()) {
			c.match("(execution)<-[:EXECUTION]-(node)");
			c.match("(node)-[:EVENT_DEFINITION]->(definition)");
		}

		int i = 0;
		for (String piId : processInstanceIds) {
			String key = "piId" + i++;
			c.where("pi.id = {" + key + "} ");
			params.put(key, piId);
		}

		int j = 0;
		for (String signal : signalRefs) {
			String key = "signal" + j++;
			c.where("definition.signalRef = {" + key + "} ");
			params.put(key, signal);
		}

		int k = 0;
		for (String msg : messageRefs) {
			String key = "message" + k++;
			c.where("definition.messageRef = {" + key + "} ");
			params.put(key, msg);
		}

		c.close("return execution");
		return c.toCypher();
	}

}
