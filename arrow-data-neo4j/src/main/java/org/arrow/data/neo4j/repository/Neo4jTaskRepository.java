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
import org.arrow.model.task.Task;
import org.arrow.runtime.execution.service.data.TaskRepository;

/**
 * {@link org.springframework.data.neo4j.repository.GraphRepository} interface for {@link org.arrow.model.process.SubProcess} instances.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public interface Neo4jTaskRepository extends GraphRepository<Task>, TaskRepository {

    @Query("match(task:ReceiveTask)-[:EVENT_DEFINITION]->(definition)-[:PROCESS]->(process) " +
            "where definition.messageRef = {0} and task.instantiate = {1}" +
            "return task order by process.timestamp desc limit 1")
    @Override
    @SuppressWarnings("unchecked")
    Task findReceiveTask(String messageRef, boolean instantiate);

    /**
     * Returns the Ad Hoc task with the given id.
     *
     * @param adHocProcessInstanceNodeId the process instance node id of the ad hoc sub process
     * @param taskId the task id
     * @return Task
     */
    @Query("START pi=node({0}) match(pi)-[:PROCESS]->(process)-[:ADHOC_TASK]->(task) return task")
    @Override
    @SuppressWarnings("unchecked")
    Task findAdHocTask(Long adHocProcessInstanceNodeId, String taskId);
}
