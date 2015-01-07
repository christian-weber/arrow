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

import org.arrow.runtime.execution.service.data.SubProcessRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.arrow.model.process.SubProcess;

import java.util.Set;

/**
 * {@link GraphRepository} interface for {@link SubProcess} instances.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public interface Neo4jSubProcessRepository extends GraphRepository<SubProcess>, SubProcessRepository {

    /**
     * Returns a {@link org.arrow.model.process.SubProcess} instance by the given parent process id.
     *
     * @param pid the parent process id
     * @return Set
     */
    @Query("match (startEvent:SignalStartEvent)-[:PROCESS_OF_STARTEVENT]->(subProcess)-[:EMBEDDED_SUB_PROCESSES]->(parentProcess)<-[:PROCESS]-(processInstance) "
            + "where processInstance.id = {0} "
            + "and subProcess.triggeredByEvent = true "
            + "match (startEvent)-[:EVENT_DEFINITION]->(definition) "
            + "where definition.signalRef = {1} "
            + "return subProcess "
            + "order by parentProcess.timestamp, ID(parentProcess) limit 1")
    Set<SubProcess> findAllBySignalEvent(String pid, String signalRef);

    /**
     * Returns a {@link org.arrow.model.process.SubProcess} instance by the given parent process id.
     *
     * @param pid the parent process id
     * @return Set
     */
    @Query("match (startEvent:MessageStartEvent)-[:PROCESS_OF_STARTEVENT]->(subProcess)-[:EMBEDDED_SUB_PROCESSES]->(parentProcess)<-[:PROCESS]-(processInstance) "
            + "where processInstance.id = {0} "
            + "and subProcess.triggeredByEvent = true "
            + "match (startEvent)-[:EVENT_DEFINITION]->(definition) "
            + "where definition.messageRef = {1} "
            + "return subProcess "
            + "order by parentProcess.timestamp, ID(parentProcess) limit 1")
    Set<SubProcess> findAllByMessageEvent(String pid, String messageRef);

    /**
     * Returns a {@link org.arrow.model.process.SubProcess} instance by the given parent process id.
     *
     * @param pid the parent process id
     * @return Set
     */
    @Query("match (startEvent:ConditionalStartEvent)-[:PROCESS_OF_STARTEVENT]->(subProcess)-[:EMBEDDED_SUB_PROCESSES]->(parentProcess)<-[:PROCESS]-(processInstance) "
            + "where processInstance.id = {0} "
            + "and subProcess.triggeredByEvent = true "
            + "match (startEvent)-[:EVENT_DEFINITION]->(definition) "
            + "where definition.beanName = {1} "
            + "return subProcess "
            + "order by parentProcess.timestamp, ID(parentProcess) limit 1")
    Set<SubProcess> findAllByConditionalEvent(String pid, String beanName);

}
