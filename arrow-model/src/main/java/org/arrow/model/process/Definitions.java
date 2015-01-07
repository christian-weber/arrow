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

package org.arrow.model.process;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.arrow.model.AbstractBpmnEntity;
import org.arrow.model.process.event.BpmnEventDefinitionEntity;
import org.arrow.model.process.event.Escalation;
import org.arrow.model.process.event.Message;
import org.arrow.model.process.event.Signal;
import org.arrow.model.visitor.BpmnEventDefinitionEntityVisitor;
import org.arrow.model.visitor.BpmnNodeEntityVisitor;

/**
 * BPMN 2.0 process definitions implementation.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("ProcessDefinitions")
public class Definitions extends AbstractBpmnEntity {

	/** The processes. */
	@Fetch
	@RelatedTo(type = "PROCESS_DEFINITION", direction = Direction.INCOMING)
	private Set<Process> processes;

	/** The signals. */
	@Fetch
	@RelatedTo(type = "SIGNAL", direction = Direction.OUTGOING)
	private Set<Signal> signals;

	/** The messages. */
	@Fetch
	@RelatedTo(type = "MESSAGE", direction = Direction.OUTGOING)
	private Set<Message> messages;

	/** The escalations. */
	@Fetch
	@RelatedTo(type = "ESCALATION", direction = Direction.OUTGOING)
	private Set<Escalation> escalations;

	/**
	 * Gets the processes.
	 * 
	 * @return the processes
	 */
	public Set<Process> getProcesses() {
		return processes;
	}

	/**
	 * Sets the processes.
	 * 
	 * @param processes
	 *            the new processes
	 */
	public void setProcesses(Set<Process> processes) {
		this.processes = processes;
	}

	/**
	 * Gets the signals.
	 * 
	 * @return the signals
	 */
	public Set<Signal> getSignals() {
		return signals;
	}

	/**
	 * Sets the signals.
	 * 
	 * @param signals
	 *            the new signals
	 */
	public void setSignals(Set<Signal> signals) {
		this.signals = signals;
	}

	/**
	 * Gets the messages.
	 * 
	 * @return the messages
	 */
	public Set<Message> getMessages() {
		return messages;
	}

	/**
	 * Sets the messages.
	 * 
	 * @param messages
	 *            the new messages
	 */
	public void setMessages(Set<Message> messages) {
		this.messages = messages;
	}

	/**
	 * Gets the escalations.
	 * 
	 * @return the escalations
	 */
	public Set<Escalation> getEscalations() {
		return escalations;
	}

	/**
	 * Sets the escalations.
	 * 
	 * @param escalations
	 *            the new escalations
	 */
	public void setEscalations(Set<Escalation> escalations) {
		this.escalations = escalations;
	}

	private Set<BpmnEventDefinitionEntity> getBpmnEventDefinitionEntities() {
		Set<BpmnEventDefinitionEntity> entities = new HashSet<BpmnEventDefinitionEntity>();
		entities.addAll(emptyIfNull(getEscalations()));

		return entities;
	}

	/**
	 * Visits the tree of {@link BpmnEventDefinitionEntity} instances.
	 * 
	 * @param visitor
	 */
	public void visit(BpmnEventDefinitionEntityVisitor visitor) {
		for (BpmnEventDefinitionEntity entity : getBpmnEventDefinitionEntities()) {
			entity.accept(visitor);
		}
	}

	/**
	 * Visits the tree of {@link BpmnEventDefinitionEntity} instances.
	 * 
	 * @param visitor
	 */
	public void visit(BpmnNodeEntityVisitor visitor) {
		for (Process process : getProcesses()) {
			process.accept(visitor);
		}
	}

}
