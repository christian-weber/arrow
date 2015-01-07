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

package org.arrow.service.impl

import org.arrow.test.Given
import org.arrow.test.WorkflowTest
import org.arrow.test.spock.spring.WorkflowDslTrait
import spock.lang.Specification
import spock.lang.Timeout

@WorkflowTest
//@Timeout(10)
public class WorkflowIntermediateThrowEventTest extends Specification implements WorkflowDslTrait {

    @Given("intermediate/throw/messageIntermediateThrowEvent.bpmn20.xml")
    def "test intermediate throw event (message)"() throws Exception {
        when:
            def pi = startById "messageIntermediateThrowEventTest"
        then:
            await(pi)
            assertSuccess pi
    }

    @Given("intermediate/throw/signalIntermediateThrowEvent.bpmn20.xml")
    def "test intermediate throw event (signal)"() throws Exception {
        when:
            def pi = startById "signalIntermediateThrowEventTest"
        then:
            await(pi)
            assertSuccess pi
    }

    @Given("intermediate/throw/noneIntermediateThrowEvent.bpmn20.xml")
    def "test intermediate throw event (none)"() throws Exception {
        when:
            def pi = startById "noneIntermediateThrowEventTest"
        then:
            await(pi)
            assertSuccess pi
    }

    @Given("intermediate/throw/linkIntermediateThrowEvent.bpmn20.xml")
    def "test intermediate throw event (link)"() throws Exception {
        when:
            def pi = startById "linkIntermediateThrowEventTest"
        then:
            await(pi)
            assertSuccess pi
    }

    @Given("intermediate/throw/multipleIntermediateThrowEvent.bpmn20.xml")
    def "test intermediate throw event (multiple, non-parallel)"() throws Exception {
        when:
            def pi = startById "multipleIntermediateThrowEventTestNonParallel"
        then:
            await(pi)
            assertSuccess pi
    }

    @Given("intermediate/throw/multipleIntermediateThrowEvent.bpmn20.xml")
    def "test intermediate throw event (multiple, parallel)"() throws Exception {
        when:
            def pi = startById "multipleIntermediateThrowEventTestParallel"
        then:
            await(pi)
            assertSuccess pi
    }

    @Given("intermediate/throw/escalationIntermediateThrowEvent.bpmn20.xml")
    def "test intermediate throw event (escalation)"() throws Exception {
        when:
            def pi = startById "escalationIntermediateThrowEventTest"
        then:
            await(pi)
            assertSuccess pi
    }

}
