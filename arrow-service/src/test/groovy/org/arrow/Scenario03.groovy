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

import org.springframework.util.StopWatch
import org.arrow.test.Given
import org.arrow.test.WorkflowTest
import org.arrow.test.spock.spring.WorkflowDslTrait
import spock.lang.Specification

@WorkflowTest
class Scenario03 extends Specification implements WorkflowDslTrait {

    @Given("scenario03.bpmn20.xml")
    def "test scenario 03 ..."() {
        expect:
            for (int i = 0; i < 10; i++) {
                def args = [branch1:true, branch2:true, branch3:true, branch4:true, include:false]
                def pi = startById "scenario03", args

                assertSuccess(await(pi), "endevent1")

            }

    }

    @Given("scenario03.bpmn20.xml")
    def "test scenario 03 "() {
        expect:
            for (int i = 0; i < 10; i++) {
                def args = [branch1:true, branch2:true, branch3:true, branch4:true, include:true]
                def pi = startById "scenario03", args

                assertSuccess(await(pi), "endevent1")

            }

    }

}
