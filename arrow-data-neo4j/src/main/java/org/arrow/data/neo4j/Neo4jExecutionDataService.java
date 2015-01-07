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

package org.arrow.data.neo4j;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.arrow.data.neo4j.traversal.BreadthFirstSynchronisationEvaluator;
import org.arrow.data.neo4j.traversal.DepthFirstSynchronisationEvaluator;
import org.arrow.model.transition.IncomingFlowAware;
import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionDataService;
import org.arrow.runtime.execution.service.data.*;
import org.arrow.runtime.logger.LoggerFacade;
import org.arrow.runtime.meta.ProcessMetaDataRepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Execution data service implementation.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@Service
@Transactional
public class Neo4jExecutionDataService implements ExecutionDataService {

    private static final transient LoggerFacade LOGGER = new LoggerFacade(Neo4jExecutionDataService.class);

    @Autowired
    private Neo4jTemplate neo4jTemplate;
    @Autowired
    private GraphDatabaseService graphDatabaseService;
    @Autowired
    private ExecutionGroupRepository executionGroupRepositoryDefinition;
    @Autowired
    private ExecutionRepository executionRepository;
    @Autowired
    private StartEventRepository startEventRepository;
    @Autowired
    private ProcessInstanceRepository processInstanceRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ProcessRepository processRepository;
    @Autowired
    private ProcessMetaDataRepository processMetaDataRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized SynchronisationResult depthFirstSynchronization(BpmnNodeEntitySpecification entity) {

        LOGGER.debug("start synchronization traversing at %s", entity.getId());

        Node node = neo4jTemplate.getPersistentState(entity);
        DepthFirstSynchronisationEvaluator evaluator = new DepthFirstSynchronisationEvaluator();

        // @formatter:off
        TraversalDescription description = graphDatabaseService.traversalDescription()
            // enable depth first search
            .depthFirst()
            // traverse all incoming SEQUENCE_FLOW relations
            .expand(PathExpanders.forType(DynamicRelationshipType.withName("SEQUENCE_FLOW")))
            .expand(PathExpanders.forDirection(Direction.INCOMING))
            // traverse all branches
            .uniqueness(Uniqueness.NONE)
            // evaluate the path
            .evaluator(evaluator);
        // @formatter:on

        for (Path position : description.traverse(node)) {
            LOGGER.debug(position.toString());
        }

        LOGGER.debug("finish synchronization traversing at %s with state %s", entity.getId(), evaluator.isFinished());
        return new SynchronisationResult(evaluator.isFinished(), evaluator.getExpect());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public SynchronisationResult breadthFirstSynchronization(BpmnNodeEntitySpecification entity) {

        Assert.isTrue(entity instanceof IncomingFlowAware);
        IncomingFlowAware ifa = (IncomingFlowAware) entity;

        LOGGER.debug("start breadth first synchronization at %s", entity.getId());

        Node node = neo4jTemplate.getPersistentState(entity);
        BreadthFirstSynchronisationEvaluator evaluator = new BreadthFirstSynchronisationEvaluator();

        // @formatter:off
        TraversalDescription description = graphDatabaseService.traversalDescription()
            // enable depth first search
            .depthFirst()
            // traverse all incoming SEQUENCE_FLOW relations
            .expand(PathExpanders.forType(DynamicRelationshipType.withName("SEQUENCE_FLOW")))
            .expand(PathExpanders.forDirection(Direction.INCOMING))
            // traverse all branches
            .uniqueness(Uniqueness.NONE)
            // evaluate the path
            .evaluator(evaluator);
        // @formatter:on

        for (Path position : description.traverse(node)) {
            LOGGER.debug(position.toString());
        }

        LOGGER.debug("finish synchronization traversing at %s with state %s", entity.getId(), evaluator.isSynchronised());
        return new SynchronisationResult(evaluator.isSynchronised(), ifa.getIncomingFlows().size());

    }

    @Override
    public Set<Execution> followingExecutions(BpmnNodeEntitySpecification startNode, State state) {

        Node node = neo4jTemplate.getPersistentState(startNode);

        // @formatter:off
        TraversalDescription description = graphDatabaseService.traversalDescription()
            // enable depth first search
            .breadthFirst()
            // traverse all incoming SEQUENCE_FLOW relations
            .expand(PathExpanders.forTypeAndDirection(DynamicRelationshipType.withName("SEQUENCE_FLOW"), Direction.OUTGOING))
            // traverse all branches
            .uniqueness(Uniqueness.NONE)
            // evaluate the path
            .evaluator(Evaluators.excludeStartPosition())
            .evaluator(Evaluators.toDepth(1));
        // @formatter:on

        Collection<Long> ids = new HashSet<>();
        for (Path position : description.traverse(node)) {

            Iterable<Relationship> iterable = position.endNode().getRelationships(DynamicRelationshipType.withName("EXECUTION"), Direction.OUTGOING);
            for (Relationship relationship : iterable) {
                String str = (String) relationship.getEndNode().getProperty("state");
                if (str.equalsIgnoreCase(state.name())) {
                    ids.add(relationship.getEndNode().getId());
                }
            }
        }

        return executionRepository.findByNodeIds(ids);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExecutionRepository execution() {
        return executionRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StartEventRepository startEvent() {
        return startEventRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessInstanceRepository processInstance() {
        return processInstanceRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskRepository task() {
        return taskRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GatewayRepository gateway() {
        return applicationContext.getBean(GatewayRepository.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessRepository process() {
        return processRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessMetaDataRepository processMetaData() {
        return processMetaDataRepository;
    }
}
