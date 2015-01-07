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

package org.arrow.data.neo4j.store.impl;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Component;
import org.arrow.runtime.execution.Execution;

import java.util.HashMap;
import java.util.Map;

/**
 * Execution store implementation class. This class is used to create {@link Execution} instances.
 *
 * @since 1.0.0
 * @author christian.weber
 */
@Component
public class ExecutionStoreImpl {

    @Autowired
    private Neo4jTemplate template;

    @SuppressWarnings("deprecation")
    public void saveExecution(Execution executionTemplate) {

        GraphDatabaseAPI api = (GraphDatabaseAPI) template.getGraphDatabaseService();
        Transaction transaction = api.tx().unforced().begin();

        Node executionNode;
        if (executionTemplate.getNodeId() == null) {
            executionNode = getExecution();
        } else {
            executionNode = template.getPersistentState(executionTemplate);
        }

        Node entityNode = template.getPersistentState(executionTemplate.getEntity());
        Node processInstanceNode = template.getPersistentState(executionTemplate.getProcessInstance());

        transaction.acquireWriteLock(executionNode);
        transaction.acquireWriteLock(entityNode);

        executionNode.createRelationshipTo(processInstanceNode, DynamicRelationshipType.withName("PROCESS_INSTANCE"));
        entityNode.createRelationshipTo(executionNode, DynamicRelationshipType.withName("EXECUTION"));

        executionNode.setProperty("state", executionTemplate.getState().name());

        for (String flowId : executionTemplate.getEnabledFlowIdsContainer()) {
            executionNode.setProperty("enabledFlowIds-" + flowId, true);
        }

        transaction.success();
        transaction.close();

    }

    public Node getExecution() {

        Map<String, Object> properties = new HashMap<>();
        properties.put("id", String.valueOf(System.nanoTime()));

        Node node = template.createNode(properties);
        node.addLabel(DynamicLabel.label("Execution"));
        node.addLabel(DynamicLabel.label("_Execution"));

        return node;
    }


}
