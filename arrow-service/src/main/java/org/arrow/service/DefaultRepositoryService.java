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

package org.arrow.service;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.arrow.model.process.*;
import org.arrow.model.process.Process;
import org.arrow.model.process.event.BpmnEventDefinitionEntity;
import org.arrow.model.process.visitor.CacheBpmnEventDefinitionEntityVisitor;
import org.arrow.model.process.visitor.SaveBpmnEntityVisitor;
import org.arrow.model.process.visitor.node.AddProcessRelationEntityVisitor;
import org.arrow.model.process.visitor.node.CacheBpmnEntityVisitor;
import org.arrow.model.process.visitor.node.EventDefinitionBpmnEntityVisitor;
import org.arrow.model.process.visitor.relationship.InitBpmnRelationshipEntityVisitor;
import org.arrow.model.visitor.BpmnNodeEntityVisitor;
import org.arrow.model.visitor.adapter.BpmnNodeEntityVisitorAdapter;
import org.arrow.runtime.RuntimeService;
import org.arrow.runtime.meta.ProcessMetaData;
import org.arrow.runtime.meta.ProcessMetaDataRepository;
import org.arrow.runtime.service.RepositoryService;
import org.arrow.service.repository.visitor.node.InitBpmnNodeEntityVisitor;
import org.arrow.util.IterableUtils;

import java.util.*;
import java.util.function.Predicate;

import static org.arrow.util.StreamUtils.containsInstanceOf;

/**
 * {@link RepositoryService} implementation class.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@Service
public class DefaultRepositoryService implements RepositoryService {

    private Logger logger = Logger.getLogger(DefaultRepositoryService.class);

    @Autowired
    private GraphDatabaseService graphService;
    @Autowired
    private Neo4jTemplate template;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private ProcessMetaDataRepository processMetaDataRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deploy(Definitions definitions) {

        if (CollectionUtils.isEmpty(definitions.getProcesses())) {
            logger.warn("no processes found, skip deployment");
            return;
        }

        // use a visitor to cache all BPMN event definitions
        CacheBpmnEventDefinitionEntityVisitor defVisitor = getDefVisitor();
        definitions.visit(defVisitor);

        for (Process process : definitions.getProcesses()) {
            Assert.notNull(process);
            prepareProcess(process, defVisitor.getCache());

            // add a relationship from a process to each element which demands one
            process.visit(new AddProcessRelationEntityVisitor(process, template));
        }

        // save the process meta data
        processMetaDataRepository.saveAll(prepareProcessMetaData(definitions));
        // save the definitions
        template.save(definitions);
    }

    private Collection<ProcessMetaData> prepareProcessMetaData(Definitions definitions) {
        List<ProcessMetaData> list = new ArrayList<>();
        definitions.getProcesses().forEach(process -> list.addAll(prepareProcessMetaData(process)));
        return list;
    }

    private Collection<ProcessMetaData> prepareProcessMetaData(Process process) {
        List<ProcessMetaData> list = new ArrayList<>();

        ProcessMetaData metaData = new ProcessMetaData();
        metaData.setProcessId(process.getId());
        metaData.setHasEventSubProcess(containsInstanceOf(process.getSubProcesses(), SubProcess.class));

        list.add(metaData);

        // call the method recursively for all sub processes
        process.getSubProcesses().forEach(subProcess -> list.addAll(prepareProcessMetaData(subProcess)));
        return list;
    }

    private CacheBpmnEventDefinitionEntityVisitor getDefVisitor() {
        return new CacheBpmnEventDefinitionEntityVisitor();
    }

    private void prepareProcess(Process process, Map<String, BpmnEventDefinitionEntity> defCache) {

        // prepare the BPMN entity visitors
        SaveBpmnEntityVisitor saveVisitor = new SaveBpmnEntityVisitor(context);
        CacheBpmnEntityVisitor cacheVisitor = new CacheBpmnEntityVisitor();
        BpmnNodeEntityVisitor defVisitor = new EventDefinitionBpmnEntityVisitor(context, defCache);

        BpmnNodeEntityVisitorAdapter entityVisitor = new BpmnNodeEntityVisitorAdapter();
        entityVisitor.addVisitor(defVisitor);
        entityVisitor.addVisitor(saveVisitor);
        entityVisitor.addVisitor(cacheVisitor);

        // visit the BPMN entities tree
        process.visit(entityVisitor);
        process.visit(new InitBpmnRelationshipEntityVisitor(context, cacheVisitor.getCache()));
        process.visit(new InitBpmnNodeEntityVisitor(context, cacheVisitor.getCache()));

        saveProcessDefinition(process);

        scheduleTimerStartEvents(process);
        prepareSubProcesses(process, defCache);

        process.visit(new AddProcessRelationEntityVisitor(process, template));

    }

    /**
     * Schedules the given {@link Process} instance.
     *
     * @param process the process instance
     */
    private void scheduleTimerStartEvents(Process process) {
        process.getTimerStartEvents().forEach(runtimeService::schedule);
    }

    private void prepareSubProcesses(Process process, Map<String, BpmnEventDefinitionEntity> defCache) {

        Set<SubProcess> subProcesses = process.getSubProcesses();
        for (SubProcess subProcess : subProcesses) {
            prepareProcess(subProcess, defCache);
        }

        Set<Transaction> transactions = process.getParsedTransactions();
        for (Transaction transaction : IterableUtils.emptyIfNull(transactions)) {
            prepareProcess(transaction, defCache);
        }

        Set<AdHocSubProcess> adHocSubProcesses = process.getParsedAdHocSubProcesses();
        for (AdHocSubProcess adHocSubProcess : IterableUtils.emptyIfNull(adHocSubProcesses)) {
            prepareProcess(adHocSubProcess, defCache);
        }

    }

    /**
     * Stores the process definition.
     *
     * @param process the process instance
     */
    private void saveProcessDefinition(Process process) {

        // store META information
        Predicate<? super SubProcess> filter = subProcess -> subProcess instanceof EventSubProcess;
        process.getSubProcesses().stream().filter(filter).forEach(subProcess -> process.setHasEventSubProcess(true));
        template.save(process);
    }

}
