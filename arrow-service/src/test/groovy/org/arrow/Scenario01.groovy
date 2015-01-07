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
class Scenario01 extends Specification implements WorkflowDslTrait {

    @Autowired
    private WorkflowDeployment deployment;

    @Given("scenario01-transaction-with-manual-tasks.bpmn20.xml")
    def "scenario01 transaction with manual tasks"() {
        expect:
            for (int i = 1; i < 10; i++) {
                def pi = startById "scenario01-transaction-with-manual-tasks"
                assertSuccess(await(pi), "main_subprocess")
                assertSuccess(await(pi), "main_endevent")
            }

    }

    @Given("scenario01-transaction-with-script-tasks.bpmn20.xml")
    def "scenario01 transaction with script tasks"() {
        expect:
            for (int i = 1; i < 10; i++) {
                def pi = startById "scenario01-transaction-with-script-tasks"
                assertSuccess(await(pi), "main_subprocess")
                assertSuccess(await(pi), "main_endevent")
            }

    }

    @Given("scenario01-transaction-with-error.bpmn20.xml")
    def "scenario01 transaction with error"() {
        expect:
            for (int i = 0; i < 10; i++) {
                def pi = startById "scenario01-transaction-with-errors"
                assertSuccess(await(pi), "main_subprocess")
                assertSuccess(await(pi), "main_endevent")
            }

    }

}
