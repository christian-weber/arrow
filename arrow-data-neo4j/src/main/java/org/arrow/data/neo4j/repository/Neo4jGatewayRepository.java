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
import org.arrow.model.gateway.impl.InclusiveGateway;
import org.arrow.model.task.Task;
import org.arrow.runtime.execution.service.data.GatewayRepository;

import java.util.Set;

/**
 * {@link org.springframework.data.neo4j.repository.GraphRepository} interface for {@link org.arrow.model.process.SubProcess} instances.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@SuppressWarnings("unused") // spring bean
public interface Neo4jGatewayRepository extends GraphRepository<Task>, GatewayRepository {

    @Override
    @Query("match(gateway:InclusiveGateway)-[:EXECUTION]->(execution)-[:PROCESS_INSTANCE]->(pi) " +
            "where pi.id = {0} and execution.state <> {1} return distinct gateway")
    Set<InclusiveGateway> findInclusiveGatewayNotInState(String piId, String state);

}
