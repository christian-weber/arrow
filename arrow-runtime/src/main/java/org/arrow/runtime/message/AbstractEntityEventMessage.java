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

package org.arrow.runtime.message;

import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ExecutionGroup;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.support.EngineSynchronizationManager;

import java.util.Objects;

/**
 * Abstract node class definition. Classes which implements this interface could
 * be executed by a NodeWorker instance.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public abstract class AbstractEntityEventMessage extends AbstractEventMessage  {

	/** The entity. */
	private final BpmnNodeEntitySpecification entity;

	/** The execution. */
	private final Execution execution;

    private final ExecutionGroup executionGroup;

    private final Execution interruptableExecution;

	/**
	 * Instantiates a new node.
	 *
	 * @param entity
	 *            the entity
	 * @param processInstance
	 *            the process instance
	 */
	public AbstractEntityEventMessage(BpmnNodeEntitySpecification entity, ProcessInstance processInstance) {
        this(entity, processInstance, null);
	}

    public AbstractEntityEventMessage(BpmnNodeEntitySpecification entity, ProcessInstance processInstance, ExecutionGroup executionGroup) {
        super(processInstance);
        this.entity = entity;

        // create a new execution
        this.execution = new Execution();
        this.execution.setEntity(entity);
        this.execution.setProcessInstance(processInstance);
        final long id = Objects.hash(processInstance.getId(), (entity != null ? entity.getId() : 0), System.nanoTime());
        this.execution.setId(processInstance.getId() + ":" + id);
        this.executionGroup = executionGroup;

this.interruptableExecution = null;
    }

	/**
	 * Instantiates a new node.
	 *
	 * @param execution
	 *            the execution
	 */
	public AbstractEntityEventMessage(Execution execution) {
        this(execution, null);
	}

    /**
     * Instantiates a new node.
     *
     * @param execution
     *            the execution
     */
    public AbstractEntityEventMessage(Execution execution, Execution interruptableExecution) {
        super(execution.getProcessInstance());
        this.entity = execution.getEntity();
        this.execution = execution;
        this.executionGroup = null;

        this.interruptableExecution = interruptableExecution;
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
	 * Gets the execution.
	 * 
	 * @return the execution
	 */
	public Execution getExecution() {
        execution.setCurrentEventMessage(this);
		return execution;
	}

    @SuppressWarnings("unused")
    public ExecutionGroup getExecutionGroup() {
        return executionGroup;
    }

    public Execution getInterruptableExecution() {
        return interruptableExecution;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if (getEntity() == null) {
            return getClass().getSimpleName() + "]";
        }
        return getClass().getSimpleName() + " [" + getEntity().getClass().getSimpleName() + ":" + getEntity().getId() + "]";
    }

}
