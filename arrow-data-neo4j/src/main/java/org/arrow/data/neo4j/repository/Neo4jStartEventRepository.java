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

package org.arrow.data.neo4j.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.arrow.data.neo4j.postprocess.MultipleStartEventStartedByMessage;
import org.arrow.data.neo4j.postprocess.infrastructure.QueryPostProcess;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.model.event.startevent.impl.ConditionalStartEvent;
import org.arrow.model.event.startevent.impl.MessageStartEvent;
import org.arrow.model.event.startevent.impl.NoneStartEvent;
import org.arrow.model.event.startevent.impl.SignalStartEvent;
import org.arrow.runtime.execution.service.data.StartEventRepository;

import java.util.Set;

/**
 * {@link GraphRepository} definition for {@link StartEvent} BPMN entities.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public interface Neo4jStartEventRepository extends GraphRepository<StartEvent>, StartEventRepository {

	/**
	 * Returns a {@link Iterable} result of all {@link SignalStartEvent} events
	 * with the given signal ref.
	 * 
	 * @return Set
	 */
    @Override
	@Query(value = "match (event:SignalStartEvent)-[:EVENT_DEFINITION]->(definition), (event)-[:PROCESS_OF_STARTEVENT]->(process)"
			+ "where definition.signalRef = {0} "
			+ "with  event.id as eventId, process.id as processId, MAX(process.timestamp) as timestamp "
			+ "match (event)-[:PROCESS_OF_STARTEVENT]->(process) "
			+ "where process.id = processId and process.timestamp = timestamp "
            + "and NOT has(process.triggeredByEvent) " // exclude all event sub processes
			+ "and event.id = eventId "
            + "return event")
	Set<SignalStartEvent> findSignalStartEventsByReference(String signalRef);

	/**
	 * Returns a {@link MessageStartEvent} by the given messageRef.
	 * 
	 * @param messageRef the message reference value
	 * @return StartEvent
	 */
	@Query("match (eventDefinition:MessageEventDefinition)<-[:EVENT_DEFINITION]-(event)-[:PROCESS_OF_STARTEVENT]->(process)-[:PROCESS_DEFINITION]->(definition)-[:MESSAGE]->(message) "
			+ "where message.name = {0} "
			+ "AND message.id = eventDefinition.messageRef "
            +"return event "
			+ "order by process.timestamp DESC limit 1")
	@QueryPostProcess(MultipleStartEventStartedByMessage.class)
	StartEvent findMessageStartEvent(String messageRef);

	/**
	 * Returns a {@link NoneStartEvent} by the given process id.
	 * 
	 * @param processId the process id
	 * @return NoneStartEvent
	 */
	@Query("start process=node:entityId(id={0}) "
            + "match (process)<-[:PROCESS_OF_STARTEVENT]-(event:NoneStartEvent) return event "
			+ "order by process.timestamp desc limit 1")
	NoneStartEvent findNoneStartEventByProcessId(String processId);

	/**
	 * Returns all {@link ConditionalStartEvent} instances by the given
	 * conditional bean name.
	 * 
	 * @param beanName the bean name
	 * @return Iterable
	 */
    @Query(value = "match (event:ConditionalStartEvent)-[:EVENT_DEFINITION]->(definition)-[:CONDITION]->(condition), "
            + "(event)-[:PROCESS_OF_STARTEVENT]->(process)"
            + "where condition.beanName = {0} "
            + "with  event.id as eventId, process.id as processId, MAX(process.timestamp) as timestamp "
            + "match (event)-[:PROCESS_OF_STARTEVENT]->(process) "
            + "where process.id = processId and process.timestamp = timestamp "
            + "and event.id = eventId "
            + "return event")
	Set<ConditionalStartEvent> findAllConditionalStartEvents(
			String beanName);

}
