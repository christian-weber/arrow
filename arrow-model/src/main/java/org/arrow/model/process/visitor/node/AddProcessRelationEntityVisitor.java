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

package org.arrow.model.process.visitor.node;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.arrow.model.definition.EventDefinition;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.model.process.*;
import org.arrow.model.process.Process;
import org.arrow.model.task.Task;
import org.arrow.model.task.impl.ReceiveTask;
import org.arrow.model.visitor.AbstractBpmnNodeEntityVisitor;

/**
 * Created by christian.weber on 13.12.2014.
 */
public class AddProcessRelationEntityVisitor extends AbstractBpmnNodeEntityVisitor {

    private final org.arrow.model.process.Process process;
    private final Neo4jTemplate neo4jTemplate;

    public AddProcessRelationEntityVisitor(Process process, Neo4jTemplate neo4jTemplate) {
        this.process = process;
        this.neo4jTemplate = neo4jTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitTask(Task task) {

        if (process instanceof AdHocSubProcess) {
            Node source = neo4jTemplate.getPersistentState(process);
            Node target = neo4jTemplate.getPersistentState(task);

            RelationshipType relationship = DynamicRelationshipType.withName("ADHOC_TASK");
            source.createRelationshipTo(target, relationship);
        }

        // add a relationship between a process and a receive task
        if (task instanceof ReceiveTask && ((ReceiveTask)task).isInstantiate()) {
            EventDefinition definition = ((ReceiveTask) task).getMessageEventDefinition();

            Node source = neo4jTemplate.getPersistentState(definition);
            Node target = neo4jTemplate.getPersistentState(process);

            RelationshipType relationship = DynamicRelationshipType.withName("PROCESS");
            source.createRelationshipTo(target, relationship);
        }

    }

}
