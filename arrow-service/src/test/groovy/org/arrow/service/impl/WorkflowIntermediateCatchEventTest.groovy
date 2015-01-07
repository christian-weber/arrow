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
import org.arrow.test.Given
import org.arrow.test.WorkflowTest
import org.arrow.test.spock.spring.WorkflowDslTrait
import spock.lang.Specification
import spock.lang.Timeout

@WorkflowTest
@Timeout(10)
public class WorkflowIntermediateCatchEventTest extends Specification implements WorkflowDslTrait {

    @Given("intermediate/catch/messageIntermediateCatchEvent.bpmn20.xml")
    def "test intermediate catch event (message)"() throws Exception {
        when:
            def pi = startById "messageIntermediateCatchEventTest"
        and:
            sleep 250
            message "messageIntermediateCatchEventTest"
        then:
            await(pi)
            assertSuccess pi
    }

    @Given("intermediate/catch/signalIntermediateCatchEvent.bpmn20.xml")
    def "test intermediate catch event (signal)"() throws Exception {
        when:
            def pi = startById "signalIntermediateCatchEventTest"
        and:
            sleep 250
            signal "signalIntermediateCatchEventTest"
        then:
            await(pi)
            assertSuccess pi
    }

    @Given("intermediate/catch/timerIntermediateCatchEvent.bpmn20.xml")
    def "test intermediate catch event (timer)"() throws Exception {
        when:
            def pi = startById "timerIntermediateCatchEventTest"
        then:
            await(pi)
            assertSuccess pi
    }

    @Given("intermediate/catch/conditionalIntermediateCatchEvent.bpmn20.xml")
    def "test intermediate catch event (conditional)"() throws Exception {
        when:
            def pi = startById "conditionalIntermediateCatchEventTest"
        and:
            sleep 250
            condition(businessConditionMock("conditionBean"))
        then:
            await(pi)
            assertSuccess pi
    }

    @Given("intermediate/catch/linkIntermediateCatchEvent.bpmn20.xml")
    def "test intermediate catch event (link)"() throws Exception {
        when:
            def pi = startById "linkIntermediateCatchEventTest"
        then:
            await(pi)
            assertSuccess pi
    }

    @Given("intermediate/catch/multipleIntermediateCatchEvent.bpmn20.xml")
    def "test intermediate catch event (multiple, non-parallel)"() throws Exception {
        when:
            def pi = startById "multipleIntermediateCatchEventTestNonParallel"
        and:
            sleep 250
            message "multipleIntermediateCatchEventTestNonParallel"
        then:
            await(pi)
            assertSuccess pi
    }

    @Given("intermediate/catch/multipleIntermediateCatchEvent.bpmn20.xml")
    def "test intermediate catch event (multiple, parallel)"() throws Exception {
        when:
            def pi = startById "multipleIntermediateCatchEventTestParallel"
        and:
            sleep 500
            message "multipleIntermediateCatchEventTestParallel"
            sleep 500
            signal "multipleIntermediateCatchEventTestParallel"
        then:
            await(pi)
            assertSuccess pi
    }

    def businessConditionMock(beanName) {
        def condition = Mock(BusinessCondition)
        condition.evaluate(_ as BusinessCondition.BusinessConditionContext) >> true
        condition.beanName >> beanName

        return condition
    }

}
