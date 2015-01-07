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

import org.junit.Ignore
import org.arrow.runtime.api.event.BusinessCondition
import org.arrow.test.Given
import org.arrow.test.WorkflowTest
import org.arrow.test.spock.spring.WorkflowDslTrait
import spock.lang.Specification
import spock.lang.Timeout

@WorkflowTest
@Timeout(10)
public class WorkflowStartEventTest extends Specification implements WorkflowDslTrait {

    @Given("startevent/conditionalStartEvent.bpmn20.xml")
    def "test start event (condition, spring call)"() {
        when:
            startByCondition "conditionBean"
        then:
            assertSuccess latestProcessInstance("conditionalStartEventTest")
    }

    @Given("startevent/conditionalStartEvent.bpmn20.xml")
    def "test start event (condition)"() {
        when:
            startByCondition(businessConditionMock("conditionBean"))
        then:
            assertSuccess latestProcessInstance("conditionalStartEventTest")
    }

    @Given("startevent/noneStartEvent.bpmn20.xml")
    def "test start event (none)"() throws Exception {
        when:
            def pi = startById "noneStartEventTest"
        then:
            await(pi)
            assertSuccess pi
            assertSuccess pi, "endevent1"
    }

    @Given("startevent/messageStartEvent.bpmn20.xml")
    def "test start event (message)"() {
        when:
            def pi = startByMessage "messageStartEventTest"
        then:
            await(pi)
            assertSuccess pi
    }

    @Given("startevent/signalStartEvent.bpmn20.xml")
    def "test start event (signal)"() {
        when:
            def pis = startBySignal "testsignal"
            pis = await(pis)
        then:
            assertSuccess pis
    }

    @Given("startevent/timerStartEvent.bpmn20.xml")
    def "test start event (timer)"() {
        when:
            sleep 1200
        then:
            assertSuccess latestProcessInstance("timerStartEventTest")
    }

    @Given("startevent/multipleStartEvent.bpmn20.xml")
    def "test start event (multiple, non parallel)"() {
        when:
            def pi = startByMessage "nonparallel_message"
        then:
            await(pi)
            assertSuccess pi
    }

    @Given("startevent/multipleStartEvent.bpmn20.xml")
    def "test start event (multiple, parallel)"() {
        when:
            def pi = startByMessage "parallel_message"
        and:
            sleep 200
            signal "parallel_signal"
        then:
            await(pi)
            assertSuccess pi
    }

//    @Ignore
//    @Given("startevent/noneStartEvent.bpmn20.xml")
//    def "test execution group"() throws Exception {
//        when:
//            def pi = startById "noneStartEventTest"
//        then:
//            await(pi)
//            assertSuccess pi
//            assertSuccess pi, "endevent1"
//            executionGroups pi, { assert it.size() == 1 }
//            executionGroups pi, { it.each { assert it.finished } }
//    }

    def businessConditionMock(beanName) {
        def condition = Mock(BusinessCondition)
        condition.evaluate(_ as BusinessCondition.BusinessConditionContext) >> true
        condition.beanName >> beanName

        return condition
    }

}
