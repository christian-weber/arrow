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

package org.arrow

import org.springframework.beans.factory.annotation.Autowired
import org.arrow.service.WorkflowDeployment
import org.arrow.test.Given
import org.arrow.test.WorkflowTest
import org.arrow.test.spock.spring.WorkflowDslTrait
import spock.lang.Specification

@WorkflowTest
class MemoryTest extends Specification implements WorkflowDslTrait {

    @Autowired
    private WorkflowDeployment deployment;

    def "memory test"() {

        deployment.deploy("scenario01-transaction-with-manual-tasks.bpmn20.xml", getClass())
        deployment.deploy("scenario01-transaction-with-script-tasks.bpmn20.xml", getClass())
        deployment.deploy("scenario01-transaction-with-error.bpmn20.xml", getClass())
        deployment.deploy("scenario02-parallel-gateway.bpmn20.xml", getClass())
        deployment.deploy("scenario03.bpmn20.xml", getClass())

        when:
            def pi;

            pi = startById "scenario01-transaction-with-manual-tasks"
            await(pi)

            pi = startById "scenario01-transaction-with-script-tasks"
            await(pi)

            pi = startById "scenario01-transaction-with-errors"
            await(pi)

            pi = startById "scenario02-parallel-gateway"
            await(pi)

            def args1 = [branch1: true, branch2: true, branch3: true, branch4: true, include: true]
            pi = startById "scenario03", args1
            await(pi)

            def args2 = [branch1: true, branch2: true, branch3: true, branch4: true, include: false]
            pi = startById "scenario03", args2

            await(pi)

        then:
            noExceptionThrown()
    }


}
