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

import org.arrow.test.Given
import org.arrow.test.WorkflowTest
import org.arrow.test.spock.spring.WorkflowDslTrait
import spock.lang.Specification

@WorkflowTest
class Scenario02 extends Specification implements WorkflowDslTrait {

    @Given("scenario02-parallel-gateway.bpmn20.xml")
    def "test scenario 02 parallel gateway"() {
        expect:
            for (int i = 0; i < 5; i++) {
                def pi = startById "scenario02-parallel-gateway"

                assertSuccess(await(pi), "endevent1")
            }

    }

}
