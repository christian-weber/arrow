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

package org.arrow.data;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.mapping.MappingPolicy;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.arrow.model.process.Process;
import org.arrow.runtime.api.process.ProcessSpecification;

import java.io.IOException;

@Configuration
@EnableNeo4jRepositories("org.arrow.data.repository")
@ComponentScan("org.arrow.data")
@EnableTransactionManagement
public class Neo4JDataConfiguration extends Neo4jConfiguration {

    public Neo4JDataConfiguration() {
        setBasePackage("org.arrow.model",
                "org.arrow.runtime.execution",
                "org.arrow.runtime.api",
                "org.arrow.service.notification");
    }

    @Bean(destroyMethod = "shutdown")
    public GraphDatabaseService graphDatabaseService() throws IOException {
//        GraphDatabaseService gds = new GraphDatabaseFactory().newEmbeddedDatabase("build/neo4jdb");
//        GraphDatabaseService gds = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder("build/neo4jdb")

//		.setConfig( GraphDatabaseSettings.nodestore_mapped_memory_size, "100M" )
//				.setConfig( GraphDatabaseSettings.string_block_size, "60" )
//				.setConfig(GraphDatabaseSettings.use_memory_mapped_buffers, "true")
//				.setConfig(GraphDatabaseSettings.relationship_auto_indexing, "false")
//				.setConfig(GraphDatabaseSettings.relationship_keys_indexable, "false")
//				.setConfig(GraphDatabaseSettings.relationshipstore_mapped_memory_size, "100M")
//				.newGraphDatabase();

        GraphDatabaseService gds = new TestGraphDatabaseFactory().newImpermanentDatabase();

        registerShutdownHook(gds);
        return gds;
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Neo4jTemplate neo4jTemplate() throws Exception {
        return new Neo4jTemplate(mappingInfrastructure().getObject()) {

            @SuppressWarnings("unchecked")
            @Override
            public <S extends PropertyContainer, T> T createEntityFromState(S state, Class<T> type, MappingPolicy mappingPolicy) {

                if (type.isInterface() && type.isAssignableFrom(ProcessSpecification.class)) {
                    return (T) super.createEntityFromState(state, Process.class, mappingPolicy);
                }
                return super.createEntityFromState(state, type, mappingPolicy);
            }

        };
    }

}
