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
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.fieldaccess.DynamicProperties;
import org.springframework.data.neo4j.fieldaccess.DynamicPropertiesContainer;
import org.arrow.runtime.AbstractRuntimeEntity;
import org.arrow.runtime.TimestampAware;
import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.api.process.ProcessSpecification;

import java.util.*;

/**
 * Container for process instance relevant information. Each process invocation
 * has its own process instance reference.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("ProcessInstance")
@SuppressWarnings("unused")
public class ProcessInstance extends AbstractRuntimeEntity implements
		TimestampAware {

	/** The executions. */
	@RelatedTo(type = "PROCESS_INSTANCE", direction = Direction.INCOMING)
	private Set<Execution> executions;

	/** The process. */
	@RelatedTo(type = "PROCESS", direction = Direction.OUTGOING)
	private ProcessSpecification process;

    @Fetch
	@RelatedTo(type = "PARENT_PROCESS_INSTANCE", direction = Direction.OUTGOING)
	private ProcessInstance parentProcessInstance;

	@RelatedTo(type = "PROCESS_TRIGGER", direction = Direction.OUTGOING)
	private BpmnNodeEntitySpecification processTrigger;

	/** The variables. */
	private DynamicProperties variables = new DynamicPropertiesContainer();

	/** The finished. */
	private boolean finished;

	/** The key. */
	private String key;

	/** the entity timestamp when its saved/updated. */
	private Date timestamp;

    private State state;

	/**
	 * Returns the process instance variables.
	 * 
	 * @return Map
	 */
	public Map<String, Object> getVariables() {
		return variables.asMap();
	}

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    /**
	 * Checks if is finished.
	 * 
	 * @return true, if is finished
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * Gets the executions.
	 *
	 * @return the executions
	 */
	public Set<Execution> getExecutions() {
		return executions;
	}

	/**
	 * Sets the executions.
	 *
	 * @param executions
	 *            the new executions
	 */
	public void setExecutions(Set<Execution> executions) {
		this.executions = executions;
	}

	/**
	 * Gets the process.
	 * 
	 * @return the process
	 */
	public ProcessSpecification getProcess() {
		return process;
	}

	/**
	 * Sets the process.
	 * 
	 * @param process
	 *            the new process
	 */
	public void setProcess(ProcessSpecification process) {
		this.process = process;
	}

	/**
	 * Sets the process instance variables.
	 * 
	 * @param variables
	 *            the variables
	 */
	public void setVariables(Map<String, Object> variables) {
		this.variables.setPropertiesFrom(variables);
	}

	/**
	 * Sets the finished.
	 * 
	 * @param finished
	 *            the new finished
	 */
	public void setFinished(boolean finished) {
        this.finished = finished;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key.
	 * 
	 * @param key
	 *            the new key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Blocks the current thread till the process instance is marked as
	 * finished.
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public void waitOnCompletion() throws InterruptedException {
		if (finished) {
			return;
		}
		synchronized (this) {
			this.wait();
		}
	}

	/**
	 * Blocks the current thread till the process instance is marked as
	 * finished. The block state is interrupted after the given timeout.
	 *
	 * @param timeout
	 *            the timeout
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public void waitOnCompletion(long timeout) throws InterruptedException {
		synchronized (this) {
			this.wait(timeout);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets the timestamp.
	 * 
	 * @param timestamp the timestamp instance value
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public ProcessInstance getParentProcessInstance() {
		return parentProcessInstance;
	}

	public void setParentProcessInstance(ProcessInstance parentProcessInstance) {
		this.parentProcessInstance = parentProcessInstance;
	}

	public BpmnNodeEntitySpecification getProcessTrigger() {
		return processTrigger;
	}

	public void setProcessTrigger(BpmnNodeEntitySpecification processTrigger) {
		this.processTrigger = processTrigger;
	}

    public boolean isSuspend() {
        return state != null && state.isSuspend();
    }

	public void addVariable(String key, Object value) {
		this.variables.setProperty(key, value);
	}

}
