package org.arrow.test

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@ComponentScan("org.arrow.test.context")
@EnableTransactionManagement
@ImportResource("META-INF/spring/spring-workflow-testcontext.xml")
class ArrowTestConfiguration {
}
