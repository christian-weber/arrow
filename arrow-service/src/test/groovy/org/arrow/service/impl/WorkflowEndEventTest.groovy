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
@Timeout(10)
public class WorkflowEndEventTest extends Specification implements WorkflowDslTrait {

    @Given("endevent/noneEndEvent.bpmn20.xml")
    def "test end event (none)"() {
        when:
            def pi = startById "noneEndEventTest"
        then:
            await(pi)
            assertSuccess pi
    }

    @Given("endevent/messageEndEvent.bpmn20.xml")
    def "test end event (message)"() {
        when:
            def pi = startById "noneEndEventTest"
        then:
            await(pi)
            assertSuccess pi
    }

    @Given("endevent/signalEndEvent.bpmn20.xml")
    def "test end event (signal)"() {
        when:
            def pi = startById "noneEndEventTest"
        then:
            await(pi)
            assertSuccess pi
    }

    @Given("endevent/multipleEndEvent.bpmn20.xml")
    def "test end event (multiple, non parallel)"() {
        when:
            def pi = startById "noneEndEventTest"
        then:
            await(pi)
            assertSuccess pi
    }

    @Given("endevent/multipleEndEvent.bpmn20.xml")
    def "test end event (multiple, parallel)"() {
        when:
            def pi = startById "noneEndEventTest"
        then:
            await(pi)
            assertSuccess pi
    }

}
