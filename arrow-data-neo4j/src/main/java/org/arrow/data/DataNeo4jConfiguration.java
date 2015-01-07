package org.arrow.data;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;

@Configuration
@ComponentScan("org.arrow.data.neo4j")
@EnableNeo4jRepositories("org.arrow.data.neo4j.repository")
public class DataNeo4jConfiguration {

}
