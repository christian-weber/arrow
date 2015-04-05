package org.arrow.test.context

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource

/**
 * Created by christian.weber on 05.04.2015.
 */
@Configuration
@ImportResource("META-INF/spring/arrow-service-testcontext.xml")
class ArrowServiceTestConfiguration {
}
