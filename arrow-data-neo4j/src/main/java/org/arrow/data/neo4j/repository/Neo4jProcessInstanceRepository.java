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
import org.arrow.model.process.Process;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.execution.service.data.ProcessInstanceRepository;

/**
 * {@link GraphRepository} definition for {@link ProcessInstance} entities.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public interface Neo4jProcessInstanceRepository extends GraphRepository<ProcessInstance>, ProcessInstanceRepository {

    /**
     * Returns the latest {@link ProcessInstance} instance by the given
     * {@link Process}.
     *
     * @param key the process id
     * @return ProcessInstance
     */
    @Override
    @Query("match (process:Process)-[:PROCESS]-(pi) "
            + "where process.id = {0} return pi "
            + "ORDER BY pi.timestamp, ID(pi) LIMIT 1")
    ProcessInstance findLatestProcessInstance(String key);

    @Override
    @Query("match (pi:ProcessInstance) "
            + "where pi.id = {0} return pi "
            + "ORDER BY pi.timestamp, ID(pi) LIMIT 1")
    ProcessInstance findById(String piId);

    @Override
    @Query("start subProcessExecution=node({0}) "
            + "match (subProcessExecution)-[:PROCESS_INSTANCE]->(pi)<-[:PARENT_PROCESS_INSTANCE]-(subPi) "
            + "return subPi ORDER BY subPi.timestamp desc LIMIT 1")
    ProcessInstance findSubProcessInstance(Execution subProcessExecution);

    @Query("start ppi=node({0}) match (ppi)-[:PARENT_PROCESS_INSTANCE]-(pi)-[:PROCESS]->(adHoc{id:{1}}) return pi")
    ProcessInstance findAdHocSubProcess(Long piNodeId, String adHocId);

}
