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

package org.arrow.runtime.execution;

import org.neo4j.graphdb.Direction;
import org.neo4j.helpers.collection.IteratorUtil;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.fieldaccess.DynamicProperties;
import org.springframework.data.neo4j.fieldaccess.DynamicPropertiesContainer;
import org.springframework.util.Assert;
import org.arrow.runtime.AbstractRuntimeEntity;
import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.logger.LoggerFacade;
import org.arrow.runtime.message.EventMessage;
import org.arrow.util.CompareToBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Entity node which represents BPMN entity executions. Stores execution based
 * variables and other execution relevant information.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("Execution")
public class Execution extends AbstractRuntimeEntity implements Comparable<Execution> {

    private final static transient LoggerFacade LOGGER = new LoggerFacade(Execution.class);

    // process engine annotation
    private transient boolean synchonisation;

    private transient ThreadLocal<EventMessage> currentEventMessage;

    /**
     * The process instance.
     */
    @Fetch
    @RelatedTo(type = "PROCESS_INSTANCE", direction = Direction.OUTGOING)
    private ProcessInstance processInstance;

    /**
     * The entity.
     */
    @Fetch
    @RelatedTo(type = "EXECUTION", direction = Direction.INCOMING)
    private BpmnNodeEntitySpecification entity;

    /**
     * The execution groups.
     */
//    @Fetch
    @RelatedTo(type = "EXECUTION_GROUP", direction = Direction.INCOMING)
    private Set<ExecutionGroup> executionGroups;

    {
        executionGroups = new HashSet<>();
    }

    /**
     * The state.
     */
    private volatile State state;

    /**
     * The enabled flow ids.
     */
//    private Set<String> enabledFlowIds;
    private DynamicProperties enabledFlowIds = new DynamicPropertiesContainer();
    private transient Set<String> enabledFlowIdsContainer;

    private DynamicProperties localVariables = new DynamicPropertiesContainer();

    public Set<String> getEnabledFlowIdsContainer() {
        if (enabledFlowIdsContainer == null) {
            enabledFlowIdsContainer = new HashSet<>();
        }
        return enabledFlowIdsContainer;
    }

    public void setEnabledFlowIdsContainer(Set<String> enabledFlowIdsContainer) {
        this.enabledFlowIdsContainer = enabledFlowIdsContainer;
    }

    /**
     * Returns the execution variables.
     */
    public Map<String, Object> getVariables() {
        Map<String, Object> variables = new HashMap<>();

        if (processInstance.getParentProcessInstance() != null) {
            variables.putAll(processInstance.getParentProcessInstance().getVariables());
        }
        variables.putAll(processInstance.getVariables());
        variables.putAll(getLocalVariables());

        return variables;
    }

    /**
     * Returns the local execution variables.
     */
    public Map<String, Object> getLocalVariables() {
        return localVariables.asMap();
    }

    /**
     * Sets the local variables.
     *
     * @param localVariables the local variables
     */
    @SuppressWarnings("unused")
    public void setLocalVariables(Map<String, Object> localVariables) {
        this.localVariables.setPropertiesFrom(localVariables);
    }

    /**
     * Returns the {@link ProcessInstance}.
     */
    public ProcessInstance getProcessInstance() {
        return processInstance;
    }

    /**
     * Gets the entity.
     *
     * @return the entity
     */
    public BpmnNodeEntitySpecification getEntity() {
        return entity;
    }

    /**
     * Sets the entity.
     *
     * @param entity the new entity
     */
    public void setEntity(BpmnNodeEntitySpecification entity) {
        this.entity = entity;
    }

    /**
     * Sets the process instance.
     *
     * @param processInstance the new process instance
     */
    public void setProcessInstance(ProcessInstance processInstance) {
        this.processInstance = processInstance;
    }

    /**
     * Gets the state.
     *
     * @return the state
     */
    public State getState() {
        return state;
    }

    /**
     * Sets the state.
     *
     * @param state the new state
     */
    public void setState(State state) {
        LOGGER.info("set state %s of %s", state, this.entity);
        this.state = state;
    }

    public Set<ExecutionGroup> getExecutionGroups() {
        return executionGroups;
    }

    public void addExecutionGroup(ExecutionGroup executionGroup) {
        getExecutionGroups().add(executionGroup);
    }

    /**
     * Checks if is finished.
     *
     * @return true, if is finished
     */
    public boolean isFinished() {
        return state != null && State.WAITING.compareTo(state) != 0;
    }

    /**
     * Gets the enabled flow ids.
     *
     * @return the enabled flow ids
     */
    public Set<String> getEnabledFlowIds() {
        return new HashSet<>(IteratorUtil.asList(enabledFlowIds.getPropertyKeys().iterator()));
//        return enabledFlowIds.getPropertyKeys();
    }

    public void addEnabledFlowId(String flowId) {
        Assert.notNull(flowId);
//        enabledFlowIds.add(flowId);
        enabledFlowIds.setProperty(flowId, true);
    }

    /**
     * Disables all outgoing flows.
     */
    public void disableFlows() {
        for (String key : enabledFlowIds.getPropertyKeys()) {
            enabledFlowIds.removeProperty(key);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    public int compareTo(Execution o) {
        CompareToBuilder builder = new CompareToBuilder();
        builder.append(getId(), o.getId());

        return builder.toComparison();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final String template = "%s [entity:%s piId:%s id:%s]";
        final String clsName = getClass().getSimpleName();
        final String piId = processInstance.getId();
        final String entityId = getEntity().getId();

        return String.format(template, clsName, entityId, piId, super.getId());
    }

    @SuppressWarnings("unused")
    public void addLocalVariable(String key, Object value) {
        localVariables.setProperty(key, value);
    }

    public void addVariable(String key, Object value) {
        this.processInstance.getVariables().put(key, value);
    }

    public boolean hasVariable(String key) {
        return getVariables().containsKey(key);
    }

    public Object getVariable(String key) {
        return getVariables().get(key);
    }

    public boolean isForSynchronisation() {
        return synchonisation;
    }

    public void setSynchonisation(boolean synchonisation) {
        this.synchonisation = synchonisation;
    }

    public EventMessage getCurrentEventMessage() {
        return currentEventMessage.get();
    }

    public void setCurrentEventMessage(EventMessage currentEventMessage) {
        if (this.currentEventMessage == null) {
            this.currentEventMessage = new ThreadLocal<>();
        }
        this.currentEventMessage.set(currentEventMessage);
    }
}
