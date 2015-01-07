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

import akka.actor.ActorSystem;
import com.thoughtworks.xstream.XStream;
import org.arrow.data.neo4j.store.ProcessInstanceStore;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.arrow.model.definition.message.MessageEventDefinition;
import org.arrow.model.process.*;
import org.arrow.model.task.impl.CallActivityTask;
import org.arrow.model.task.impl.ReceiveTask;
import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.api.StartEventSpecification;
import org.arrow.runtime.api.task.TaskSpecification;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.execution.service.ExecutionService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * {@link ProcessInstanceStore} implementation class.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@Component
@SuppressWarnings("deprecation")
public class ProcessInstanceStoreImpl implements ProcessInstanceStore {

    @Autowired
    private ExecutionService executionService;

    @Autowired
    private Neo4jTemplate template;

    @Autowired
    private ActorSystem actorSystem;

    @Autowired
    private ConversionService conversionService;

    private Node getProcessInstance() {

        Map<String, Object> properties = new HashMap<>();
        properties.put("id", String.valueOf(System.nanoTime()));

        Node pi = template.createNode(properties);
        pi.addLabel(DynamicLabel.label("ProcessInstance"));
        pi.addLabel(DynamicLabel.label("_ProcessInstance"));

        return pi;
    }

    public ProcessInstance store(TaskSpecification event, Map<String, Object> map) {
        return store(event, map, null, null);
    }

    /**
     * {@inheritDoc}
     */
    public ProcessInstance store(StartEventSpecification event, Map<String, Object> map) {
        return store(event, map, null, null);
    }

    /**
     * {@inheritDoc}
     */
    public ProcessInstance store(StartEventSpecification event) {
        return store(event, Collections.<String, Object>emptyMap(), null, null);
    }

    /**
     * {@inheritDoc}
     */
    public ProcessInstance store(StartEventSpecification event, ProcessInstance parentPi) {
        return store(event, Collections.<String, Object>emptyMap(), null, parentPi);
    }

    /**
     * {@inheritDoc}
     */
    public ProcessInstance store(StartEventSpecification event, ProcessInstance parentPi, Map<String, Object> map) {
        return store(event, map, null, parentPi);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessInstance store(SubProcessEntity sub, StartEventSpecification event, ProcessInstance parentPi) {
        return store(event, Collections.<String, Object>emptyMap(), sub, parentPi);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessInstance store(SubProcessEntity sub, StartEventSpecification event, ProcessInstance parentPi, Map<String, Object> map) {
        return store(event, map, sub, parentPi);
    }

    /**
     * {@inheritDoc}
     */
    private ProcessInstance store(BpmnNodeEntitySpecification event,
                                  Map<String, Object> map, SubProcessEntity sub, ProcessInstance parentPi) {

        GraphDatabaseAPI api = (GraphDatabaseAPI) template.getGraphDatabaseService();
        Transaction transaction = api.tx().unforced().begin();
        try {

            Node eventNode = template.getPersistentState(event);

            Assert.notNull(eventNode, "start event must not be null");
            Assert.notNull(map, "variables map must not be null");

            final Node node = getProcessInstance();


            Node processNode;
            if (sub instanceof CallActivityTask || sub == null) {

                if (sub != null) {
                    Node processTrigger = template.getPersistentState(sub);
                    node.createRelationshipTo(processTrigger, DynamicRelationshipType.withName("PROCESS_TRIGGER"));
                }

                Iterable<Relationship> relationships;

                if (event instanceof ReceiveTask) {
                    MessageEventDefinition definition = ((ReceiveTask) event).getMessageEventDefinition();
                    Node source = template.getPersistentState(definition);

                    relationships = source.getRelationships(Direction.OUTGOING, DynamicRelationshipType.withName("PROCESS"));
                } else {
                    relationships = eventNode.getRelationships(Direction.OUTGOING, DynamicRelationshipType.withName("PROCESS_OF_STARTEVENT"));
                }


                processNode = relationships.iterator().next().getEndNode();
            } else {
                processNode = template.getPersistentState(sub);
            }

            transaction.acquireWriteLock(processNode);

            for (String key : map.keySet()) {
                Object value = map.get(key);
                Class<?> cls = value.getClass();

                if (cls.isArray() || ClassUtils.isPrimitiveOrWrapper(cls)) {
                    node.setProperty("variables-" + key, value);
                } else if (conversionService.canConvert(cls, String.class)) {
                    value = conversionService.convert(value, String.class);
                    node.setProperty("variables-" + key, value);
                    node.setProperty("variables-" + key + "-type", cls.getName());
                } else {
                    node.setProperty("variables-" + key, toXml(value));
                    node.setProperty("variables-" + key + "-type", cls.getName());
                }
            }

            if (parentPi != null) {
                Node parentPiNode = template.getPersistentState(parentPi);
                node.createRelationshipTo(parentPiNode, DynamicRelationshipType.withName("PARENT_PROCESS_INSTANCE"));
            }

            node.setProperty("key", processNode.getProperty("id"));
            node.createRelationshipTo(processNode, DynamicRelationshipType.withName("PROCESS"));

            transaction.success();
            return executionService.data().processInstance().findOne(node.getId());
        } catch (Throwable throwable) {
            transaction.failure();
            throw new RuntimeException(throwable);
        } finally {
            transaction.close();
        }

    }

    @Override
    public ProcessInstance store(AdHocSubProcess adHocSubProcess, ProcessInstance parentProcessInstance) {
        GraphDatabaseAPI api = (GraphDatabaseAPI) template.getGraphDatabaseService();
        Transaction transaction = api.tx().unforced().begin();
        try {

            final Node node = getProcessInstance();
            Node processNode = template.getPersistentState(adHocSubProcess);

            transaction.acquireWriteLock(processNode);


            if (parentProcessInstance != null) {
                Node parentPiNode = template.getPersistentState(parentProcessInstance);
                node.createRelationshipTo(parentPiNode, DynamicRelationshipType.withName("PARENT_PROCESS_INSTANCE"));
            }

            node.setProperty("key", processNode.getProperty("id"));
            node.createRelationshipTo(processNode, DynamicRelationshipType.withName("PROCESS"));

            transaction.success();
            return executionService.data().processInstance().findOne(node.getId());
        } catch (Throwable throwable) {
            transaction.failure();
            throw new RuntimeException(throwable);
        } finally {
            transaction.close();
        }
    }

    public class ParentProcessInstanceCallable implements Callable<Node> {

        private final ProcessInstance pi1;
        private final ProcessInstance pi2;

        private ParentProcessInstanceCallable(ProcessInstance node, ProcessInstance parentNode) {
            this.pi1 = node;
            this.pi2 = parentNode;
        }

        @Override
        public Node call() throws Exception {
            Transaction transaction = template.getGraphDatabaseService().beginTx();

            final Node node1 = template.getPersistentState(pi1);
            final Node node2 = template.getPersistentState(pi2);

            node1.createRelationshipTo(node2, DynamicRelationshipType.withName("PARENT_PROCESS_INSTANCE"));

            transaction.success();
            transaction.close();
            return node1;
        }
    }

    private String toXml(Object value) {
        XStream xstream = new XStream();

        xstream.processAnnotations(value.getClass());
        return xstream.toXML(value);
    }

}
