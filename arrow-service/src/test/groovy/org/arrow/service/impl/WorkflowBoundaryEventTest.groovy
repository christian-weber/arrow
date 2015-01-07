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

import org.arrow.runtime.api.event.BusinessCondition
import org.arrow.runtime.api.event.BusinessCondition.BusinessConditionContext
import org.arrow.test.Given
import org.arrow.test.WorkflowTest
import org.arrow.test.spock.spring.WorkflowDslTrait
import spock.lang.Specification
import spock.lang.Timeout

@WorkflowTest
@Timeout(10)
public class WorkflowBoundaryEventTest extends Specification implements WorkflowDslTrait {

    @Given("boundaryevent/errorBoundaryEvent.bpmn20.xml")
    def "test boundary event (error)"() {
        when:
            def pi = startById "errorBoundaryEventTest"
        then:
            await(pi)
            assertSuccess(pi, "endevent2")
            assertSkipped(pi, "endevent1")
    }

    @Given("boundaryevent/signalBoundaryEvent.bpmn20.xml")
    def "test boundary event (signal, interrupting)"() {
        when:
            def pi = startById "signalBoundaryEventTest"
        and:
            await 250
            signal "signalBoundaryEventTest"
        then:
            await(pi)
            assertSuccess(pi, "endevent2")
            assertSkipped(pi, "endevent1")
    }

    @Given("boundaryevent/signalBoundaryEvent.bpmn20.xml")
    def "test boundary event (signal, non-interrupting)"() {
        when:
            def pi = startById "signalBoundaryEventTest2"
        and:
            await 250
            signal "signalBoundaryEventTest"
        then:
            await(pi)
            assertSuccess(pi, "endevent3")
            assertSuccess(pi, "endevent4")
    }

    @Given("boundaryevent/messageBoundaryEvent.bpmn20.xml")
    def "test boundary event (message, interrupting)"() {
        when:
            def pi = startById "messageBoundaryEventTest_1"
        and:
            await 250
            message "messageBoundaryEventTest1"
        then:
            await(pi)
            assertSuccess(pi, "endevent2")
            assertSkipped(pi, "endevent1")
    }

    @Given("boundaryevent/messageBoundaryEvent.bpmn20.xml")
    def "test boundary event (message, non-interrupting)"() {
        when:
            def pi = startById "messageBoundaryEventTest_2"
        and:
            await 250
            message "messageBoundaryEventTest2"
        then:
            await(pi)
            assertSuccess(pi, "endevent3")
            assertSuccess(pi, "endevent4")
    }

    @Given("boundaryevent/conditionalBoundaryEvent.bpmn20.xml")
    def "test boundary event (conditional, interrupting)"() {
        when:
            def pi = startById "conditionalBoundaryEventTest1"
        and:
            await 250
            condition businessConditionMock("conditionalBoundaryEventTest1")
        then:
            await(pi)
            assertSuccess(pi, "endevent2")
            assertSkipped(pi, "endevent1")
    }

    @Given("boundaryevent/conditionalBoundaryEvent.bpmn20.xml")
    def "test boundary event (conditional, non-interrupting)"() {
        when:
            def pi = startById "conditionalBoundaryEventTest2"
        and:
            await 250
            condition businessConditionMock("conditionalBoundaryEventTest2")
        then:
            await(pi)
            assertSuccess(pi, "endevent3")
            assertSuccess(pi, "endevent4")
    }

    @Given("boundaryevent/multipleBoundaryEvent.bpmn20.xml")
    def "test boundary event (multiple, non-parallel, non-interrupting)"() {
        when:
            def pi = startById "multipleBoundaryEventTest_nonparallel"
        and:
            await 500
            signal "multipleBoundaryEventTestId1_signal_nonparallel"
        then:
            await(pi)
            assertSuccess(pi, "endevent3")
            assertSuccess(pi, "endevent4")
    }

    @Given("boundaryevent/multipleBoundaryEvent.bpmn20.xml")
    def "test boundary event (multiple, parallel, non-interrupting)"() {
        when:
            def pi = startById "multipleBoundaryEventTest_parallel"
        and:
            await 250
            signal "multipleBoundaryEventTestId1_signal_parallel"
            await 250
            message "multipleBoundaryEventTestId1_message_parallel"
        then:
            await(pi)
            assertSuccess(pi, "endevent1")
            assertSuccess(pi, "endevent2")
    }

    @Given("boundaryevent/multipleBoundaryEvent.bpmn20.xml")
    def "test boundary event (multiple, parallel, interrupting)"() {
        when:
            def pi = startById "multipleBoundaryEventTest_parallel_interrupting"
        and:
            await 250
            signal "multipleBoundaryEventTestId1_signal_parallel_interrupting"
            message "multipleBoundaryEventTestId1_message_parallel_interrupting"
        then:
            await(pi)
            assertSuccess(pi, "endevent1_parallel_interrupting")
            assertSkipped(pi, "endevent2_parallel_interrupting")
    }

    @Given("boundaryevent/multipleBoundaryEvent.bpmn20.xml")
    def "test boundary event (multiple, non-parallel, interrupting)"() {
        when:
            def pi = startById "multipleBoundaryEventTest_nonparallel_interrupting"
        and:
            await 250
            signal "multipleBoundaryEventTestId1_signal_nonparallel_interrupting"
            message "multipleBoundaryEventTestId1_message_nonparallel_interrupting"
        then:
            await(pi)
            assertSuccess(pi, "endevent1_nonparallel_interrupting")
            assertSkipped(pi, "endevent2_nonparallel_interrupting")
    }

    @Given("boundaryevent/escalationBoundaryEvent.bpmn20.xml")
    def "test boundary event (escalation)"() {
        when:
            def pi = startById "escalationBoundaryEventTestProcess"
        then:
            await(pi)
            assertSuccess(pi, "endevent2")
            assertSuccess(pi, "endevent3")
    }

    @Given("boundaryevent/compensateBoundaryEvent.bpmn20.xml")
    def "test boundary event (compensate)"() {
        when:
            def pi = startById "COMPENSATE_BOUNDARYEVENT_WORKFLOWTEST_1"
        then:
            await(pi)
            assertSuccess(pi, "A_endevent_process")
    }



    def businessConditionMock(beanName) {
        def condition = Mock(BusinessCondition)
        condition.evaluate(_ as BusinessConditionContext) >> true
        condition.beanName >> beanName

        return condition
    }

}
