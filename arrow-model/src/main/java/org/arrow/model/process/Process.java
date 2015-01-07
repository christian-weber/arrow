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

import akka.dispatch.Futures;
import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.arrow.model.AbstractBpmnNodeEntity;
import org.arrow.model.BpmnEntity;
import org.arrow.model.BpmnNodeEntity;
import org.arrow.model.BpmnRelationshipEntity;
import org.arrow.model.event.boundary.BoundaryEvent;
import org.arrow.model.event.endevent.EndEvent;
import org.arrow.model.event.intermediate.catching.IntermediateCatchEvent;
import org.arrow.model.event.intermediate.throwing.IntermediateThrowEvent;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.model.event.startevent.impl.TimerStartEvent;
import org.arrow.model.gateway.impl.*;
import org.arrow.model.task.Task;
import org.arrow.model.task.impl.CallActivityTask;
import org.arrow.model.transition.impl.Association;
import org.arrow.model.transition.impl.SequenceFlow;
import org.arrow.model.visitor.BpmnNodeEntityVisitor;
import org.arrow.model.visitor.BpmnRelationshipEntityVisitor;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.api.process.ProcessSpecification;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.service.ExecutionService;
import scala.concurrent.Future;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.arrow.model.process.Process.ElementFilter.filter;

/**
 * Represents the BPMN 'process' XML tag.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("Process")
public class Process extends AbstractBpmnNodeEntity implements ProcessSpecification {

	/** The name. */
	private String name;

	/** The executable. */
	private boolean executable;

	/** the timestamp property when saved or updated. */
	private Date timestamp;

    private boolean hasEventSubProcess;

	@Fetch
	@RelatedTo(type = "PROCESS_OF_STARTEVENT", direction = Direction.INCOMING)
	private Set<StartEvent> parsedStartEvents;

	private transient Set<EndEvent> parsedEndEvents;
	private transient Set<SequenceFlow> parsedSequenceFlows;
	private transient Set<Association> parsedAssociations;
	private transient Set<Task> parsedTasks;

    // GATEWAYS
	private transient Set<ExclusiveGateway> parsedExclusiveGateways;
    private transient Set<InclusiveGateway> parsedInclusiveGateways;
	private transient Set<EventBasedGateway> parsedEventBasedGateways;
    private transient Set<ComplexGateway> parsedComplexGateways;

	private transient Set<IntermediateCatchEvent> parsedIntermediateCatchEvents;
	private transient Set<IntermediateThrowEvent> parsedIntermediateThrowEvents;
	private transient Set<BoundaryEvent> parsedBoundaryEvents;
	private transient Set<ParallelGateway> parsedParallelGateways;

	private transient Set<Transaction> parsedTransactions;
	private transient Set<AdHocSubProcess> parsedAdHocSubProcesses;

    @Fetch
    @RelatedTo(type = "EMBEDDED_SUB_PROCESSES", direction = Direction.INCOMING)
	private Set<SubProcess> parsedSubProcesses;

	private transient Set<CallActivityTask> parsedCallActivityTasks;

	@Deprecated
    @SuppressWarnings("unused")
	public void addStartEvent(StartEvent startEvent) {
		if (parsedStartEvents == null) {
			parsedStartEvents = new HashSet<>();
		}
		parsedStartEvents.add(startEvent);
	}

	/**
	 * Checks if is executable.
	 * 
	 * @return true, if is executable
	 */
	public boolean isExecutable() {
		return executable;
	}

	/**
	 * Sets the executable.
	 * 
	 * @param executable
	 *            the new executable
	 */
    @SuppressWarnings("unused")
	public void setExecutable(boolean executable) {
		this.executable = executable;
	}

	/**
	 * Returns the parsed {@link StartEvent} instances.
	 * 
	 * @return the start events
	 */
	public Set<StartEvent> getStartEvents() {
		return emptyIfNull(parsedStartEvents);
	}

	/**
	 * Sets the start events.
	 * 
	 * @param startEvents
	 *            the new start events
	 */
    @SuppressWarnings("unused")
	public void setStartEvents(Set<StartEvent> startEvents) {
		this.parsedStartEvents = startEvents;
	}

	/**
	 * Gets the end events.
	 * 
	 * @return the end events
	 */
	public Set<EndEvent> getEndEvents() {
		return parsedEndEvents;
	}

	/**
	 * Sets the end events.
	 * 
	 * @param endEvents
	 *            the new end events
	 */
    @SuppressWarnings("unused")
	public void setEndEvents(Set<EndEvent> endEvents) {
		this.parsedEndEvents = endEvents;
	}

	/**
	 * Gets the sequence flows.
	 * 
	 * @return the sequence flows
	 */
	public Set<SequenceFlow> getSequenceFlows() {
		return parsedSequenceFlows;
	}

	/**
	 * Sets the sequence flows.
	 * 
	 * @param sequenceFlows
	 *            the new sequence flows
	 */
    @SuppressWarnings("unused")
	public void setSequenceFlows(Set<SequenceFlow> sequenceFlows) {
		this.parsedSequenceFlows = sequenceFlows;
	}

	public Set<Association> getAssociations() {
		return parsedAssociations;
	}

	public void setAssociations(Set<Association> parsedAssociations) {
		this.parsedAssociations = parsedAssociations;
	}

	public Set<AdHocSubProcess> getParsedAdHocSubProcesses() {
		return parsedAdHocSubProcesses;
	}

	public void setParsedAdHocSubProcesses(Set<AdHocSubProcess> parsedAdHocSubProcesses) {
		this.parsedAdHocSubProcesses = parsedAdHocSubProcesses;
	}

	/**
	 * Gets the service tasks.
	 * 
	 * @return the service tasks
	 */
	public Set<Task> getTasks() {
		return parsedTasks;
	}

	/**
	 * Sets the service tasks.
	 * 
	 * @param serviceTasks
	 *            the new service tasks
	 */
    @SuppressWarnings("unused")
	public void setTasks(Set<Task> serviceTasks) {
		this.parsedTasks = serviceTasks;
	}

	/**
	 * Gets the exclusive gateways.
	 * 
	 * @return the exclusive gateways
	 */
	public Set<ExclusiveGateway> getExclusiveGateways() {
		return parsedExclusiveGateways;
	}

	/**
	 * Sets the exclusive gateways.
	 * 
	 * @param exclusiveGateways
	 *            the new exclusive gateways
	 */
    @SuppressWarnings("unused")
	public void setExclusiveGateways(Set<ExclusiveGateway> exclusiveGateways) {
		this.parsedExclusiveGateways = exclusiveGateways;
	}

    /**
     * Gets the inclusive gateways.
     *
     * @return the inclusive gateways
     */
    public Set<InclusiveGateway> getInclusiveGateways() {
        return parsedInclusiveGateways;
    }

    /**
     * Sets the inclusive gateways.
     *
     * @param inclusiveGateways
     *            the new inclusive gateways
     */
    @SuppressWarnings("unused")
    public void setInclusiveGateways(Set<InclusiveGateway> inclusiveGateways) {
        this.parsedInclusiveGateways = inclusiveGateways;
    }

	/**
	 * Gets the event based gateways.
	 * 
	 * @return the event based gateways
	 */
	public Set<EventBasedGateway> getEventBasedGateways() {
		return parsedEventBasedGateways;
	}

	/**
	 * Sets the event based gateways.
	 * 
	 * @param eventBasedGateways
	 *            the new event based gateways
	 */
    @SuppressWarnings("unused")
	public void setEventBasedGateways(Set<EventBasedGateway> eventBasedGateways) {
		this.parsedEventBasedGateways = eventBasedGateways;
	}

    /**
     * Gets the complex gateways.
     *
     * @return the complex gateways
     */
    public Set<ComplexGateway> getComplexGateways() {
        return parsedComplexGateways;
    }

    /**
     * Sets the complex gateways.
     *
     * @param complexGateways
     *            the newcomplex gateways
     */
    @SuppressWarnings("unused")
    public void setComplexGateway(Set<ComplexGateway> complexGateways) {
        this.parsedComplexGateways = complexGateways;
    }

	/**
	 * Sets the intermediate throw events.
	 * 
	 * @param intermediateThrowEvents
	 *            the new intermediate throw events
	 */
    @SuppressWarnings("unused")
	public void setIntermediateThrowEvents(
			Set<IntermediateThrowEvent> intermediateThrowEvents) {
		this.parsedIntermediateThrowEvents = intermediateThrowEvents;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the intermediate catch events.
	 * 
	 * @return the intermediate catch events
	 */
	public Set<IntermediateCatchEvent> getIntermediateCatchEvents() {
		return parsedIntermediateCatchEvents;
	}

	/**
	 * Sets the intermediate catching events.
	 * 
	 * @param intermediateCatchEvents
	 *            the new intermediate catching events
	 */
    @SuppressWarnings("unused")
	public void setIntermediateCatchingEvents(
			Set<IntermediateCatchEvent> intermediateCatchEvents) {
		this.parsedIntermediateCatchEvents = intermediateCatchEvents;
	}

	/**
	 * Gets the boundary events.
	 * 
	 * @return the boundary events
	 */
	public Set<BoundaryEvent> getParsedBoundaryEvents() {
		return parsedBoundaryEvents;
	}

	/**
	 * Sets the boundary events.
	 * 
	 * @param boundaryEvents
	 *            the new boundary events
	 */
    @SuppressWarnings("unused")
	public void setBoundaryEvents(Set<BoundaryEvent> boundaryEvents) {
		this.parsedBoundaryEvents = boundaryEvents;
	}

	/**
	 * Gets the parallel gateways.
	 * 
	 * @return the parallel gateways
	 */
	public Set<ParallelGateway> getParallelGateways() {
		return parsedParallelGateways;
	}

	public Set<Transaction> getParsedTransactions() {
		return parsedTransactions;
	}

	public void setParsedTransactions(Set<Transaction> parsedTransactions) {
		this.parsedTransactions = parsedTransactions;
	}

	/**
	 * Sets the parallel gateways.
	 * 
	 * @param parallelGateways
	 *            the new parallel gateways
	 */
    @SuppressWarnings("unused")
	public void setParallelGateways(Set<ParallelGateway> parallelGateways) {
		this.parsedParallelGateways = parallelGateways;
	}

	/**
	 * Sets the intermediate catch events.
	 * 
	 * @param intermediateCatchEvents
	 *            the new intermediate catch events
	 */
    @SuppressWarnings("unused")
	public void setIntermediateCatchEvents(
			Set<IntermediateCatchEvent> intermediateCatchEvents) {
		this.parsedIntermediateCatchEvents = intermediateCatchEvents;
	}

	/**
	 * Gets the intermediate throw events.
	 * 
	 * @return the intermediate throw events
	 */
	public Set<IntermediateThrowEvent> getIntermediateThrowEvents() {
		return parsedIntermediateThrowEvents;
	}

	/**
	 * Sets the intermediate throwing events.
	 * 
	 * @param intermediateThrowingEvents
	 *            the new intermediate throwing events
	 */
    @SuppressWarnings("unused")
	public void setIntermediateThrowingEvents(
			Set<IntermediateThrowEvent> intermediateThrowingEvents) {
		this.parsedIntermediateThrowEvents = intermediateThrowingEvents;
	}

	public Set<SubProcess> getSubProcesses() {
		return emptyIfNull(parsedSubProcesses);
	}

    @SuppressWarnings("unused")
	public void setSubProcesses(Set<SubProcess> subProcesses) {
		this.parsedSubProcesses = subProcesses;
	}

	public Set<CallActivityTask> getCallActivityTasks() {
		return parsedCallActivityTasks;
	}

    @SuppressWarnings("unused")
	public void setCallActivityTasks(Set<CallActivityTask> callActivityTasks) {
		this.parsedCallActivityTasks = callActivityTasks;
	}

	/**
	 * Returns a set of {@link TimerStartEvent} instances.
	 * 
	 * @return Set
	 */
	public Set<TimerStartEvent> getTimerStartEvents() {
		return filter(getStartEvents()).by(TimerStartEvent.class);
	}

    @Override
    protected Future<Iterable<EventMessage>> executeNode(Execution execution, ExecutionService service) {
        return Futures.successful(iterableOf());
    }

    /**
	 * The Class ElementFilter.
	 */
	@Deprecated
	public static class ElementFilter {

		/** The entities. */
		private final Set<? extends BpmnEntity> entities;

		/**
		 * Instantiates a new element filter.
		 * 
		 * @param entities
		 *            the entities
		 */
		private ElementFilter(Set<? extends BpmnEntity> entities) {
			this.entities = entities;
		}

		/**
		 * Filter.
		 * 
		 * @param entities
		 *            the entities
		 * @return the element filter
		 */
        @SuppressWarnings("deprecation")
		public static ElementFilter filter(Set<? extends BpmnEntity> entities) {
			return new ElementFilter(entities);
		}

		/**
		 * By.
		 * 
		 * @param <T>
		 *            the generic type
		 * @param type
		 *            the type
		 * @return the sets the
		 */
		@SuppressWarnings("unchecked")
		public <T> Set<T> by(Class<T> type) {
			Set<T> result = new HashSet<>();
			for (Object obj : entities) {
				if (type.isAssignableFrom(obj.getClass())) {
					result.add((T) obj);
				}
			}

			return result;
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
	 * Sets the timestamp property.
	 * 
	 * @param timestamp
	 *            the new timestamp
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

    public boolean isHasEventSubProcess() {
        return hasEventSubProcess;
    }

    public void setHasEventSubProcess(boolean hasEventSubProcess) {
        this.hasEventSubProcess = hasEventSubProcess;
    }

    /**
	 * Returns a set of all parsed BPMN node entities.
	 * 
	 * @return Set
	 */
	private Set<BpmnNodeEntity> getParsedElements() {
		Set<BpmnNodeEntity> entities = new HashSet<>();
		entities.addAll(emptyIfNull(getStartEvents()));
		entities.addAll(emptyIfNull(getEndEvents()));
		entities.addAll(emptyIfNull(getParsedBoundaryEvents()));
		entities.addAll(emptyIfNull(getIntermediateCatchEvents()));
		entities.addAll(emptyIfNull(getIntermediateThrowEvents()));
		entities.addAll(emptyIfNull(getTasks()));
		entities.addAll(emptyIfNull(getSubProcesses()));
		entities.addAll(emptyIfNull(getParallelGateways()));
        // GATEWAYS
		entities.addAll(emptyIfNull(getExclusiveGateways()));
		entities.addAll(emptyIfNull(getInclusiveGateways()));
        entities.addAll(emptyIfNull(getEventBasedGateways()));
        entities.addAll(emptyIfNull(getComplexGateways()));

        // TASKS
		entities.addAll(emptyIfNull(getCallActivityTasks()));

		entities.addAll(emptyIfNull(getParsedTransactions()));
		entities.addAll(emptyIfNull(getParsedAdHocSubProcesses()));

		return entities;
	}

	private Set<BpmnRelationshipEntity> getParsedRelationshipEntities() {
		Set<BpmnRelationshipEntity> entities = new HashSet<>();
		entities.addAll(emptyIfNull(getSequenceFlows()));
		entities.addAll(emptyIfNull(getAssociations()));
		
		return entities;
	}

	public void visit(BpmnNodeEntityVisitor visitor) {
		for (BpmnNodeEntity entity : getParsedElements()) {
			entity.accept(visitor);
		}
	}

	public void visit(BpmnRelationshipEntityVisitor visitor) {
		for (BpmnRelationshipEntity entity : getParsedRelationshipEntities()) {
			entity.accept(visitor);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(BpmnNodeEntityVisitor visitor) {
		// delegate visitor to the process visitor implementation
		visit(visitor);
	}

}
