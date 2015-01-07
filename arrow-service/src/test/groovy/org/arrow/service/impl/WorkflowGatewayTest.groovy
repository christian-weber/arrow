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

import org.arrow.model.transition.IncomingFlowAware
import org.arrow.model.transition.OutgoingFlowAware
import org.arrow.runtime.api.gateway.AbstractTransitionEvaluation
import org.arrow.runtime.execution.Execution
import org.arrow.test.Given
import org.arrow.test.WorkflowTest
import org.arrow.test.spock.spring.WorkflowDslTrait
import spock.lang.Specification
import spock.lang.Timeout

@WorkflowTest
@Timeout(10)
public class WorkflowGatewayTest extends Specification implements WorkflowDslTrait {

    @Given("gateway/parallelGateway.bpmn20.xml")
    def "test gateway (parallel)"() {
        when:
            def pi = startById "parallelGatewayTest"
        then:
            await(pi)
            assertSuccess(pi, "endevent1")
            assertSuccess(pi, "endevent2")
            assertSuccess(pi, "servicetask1")
            assertSuccess(pi, "servicetask2")
    }

    @Given("gateway/exclusiveGateway.bpmn20.xml")
    def "test gateway (exclusive)"() {
        when:
            def pi = startById "exclusiveGatewayTest", [var: 1]
        then:
            await(pi)
            assertSkipped(pi, "servicetask1")
            assertSuccess(pi, "servicetask2")
    }

    @Given("gateway/inclusiveGateway.bpmn20.xml")
    def "test gateway (inclusive)"() {
        when:
            def pi = startById "inclusiveGatewayTest", [var1: 1, var2: 2]
        then:
            await(pi)
            assertSuccess(pi, "servicetask1")
            assertSuccess(pi, "servicetask2")
            assertSkipped(pi, "servicetask3")
            assertSkipped(pi, "servicetask4")
    }

    @Given("gateway/inclusiveGateway.bpmn20.xml")
    def "test gateway (inclusive)2"() {
        when:
            def pi = startById "B_inclusiveGatewayTest", [var1: 1, var2: 2]
        then:
            await(pi)
            assertSuccess(pi, "B_servicetask1")
            assertSuccess(pi, "B_servicetask2")
            assertSkipped(pi, "B_servicetask3")
            assertSkipped(pi, "B_servicetask4")
    }

    @Given("gateway/eventBasedGateway.bpmn20.xml")
    def "test gateway (eventBased, non-parallel)"() {
        when:
            def pi = startById "eventBasedGatewayTest1"
        and:
            sleep 500
            signal "eventBasedGatewayTest_signal1"
        then:
            await(pi)
            assertSuccess(pi, "event1")
            assertSkipped(pi, "event2")
    }

    @Given("gateway/eventBasedGateway.bpmn20.xml")
    def "test gateway (eventBased, parallel)"() {
        when:
            def pi = startById "eventBasedGatewayTest2"
        and:
            sleep 1500
            signal "eventBasedGatewayTest_signal2"
            message "eventBasedGatewayTest_message2"
        then:
            await(pi)
            assertSuccess(pi, "event3")
            assertSuccess(pi, "event4")
    }

    @Given("gateway/complexGateway.bpmn20.xml")
    def "test gateway (complex)"() {
        when:
            def pi = startById "complexGatewayTest"
        then:
            await(pi)
            assertSuccess(pi, "servicetask1")
            assertSuccess(pi, "servicetask2")
            assertSuccess(pi, "servicetask3")
            assertSkipped(pi, "servicetask4")
    }

    public static class CustomTransitionEvaluation1 extends AbstractTransitionEvaluation {

        @Override
        void fork(Execution execution) {
            def entity = execution.getEntity()
            def flows = ((OutgoingFlowAware) entity)?.outgoingFlows

            def result = flows.findAll { ["flow2", "flow3", "flow4"].contains(it?.id) }
            result.each { execution.addEnabledFlowId(it?.id) }
        }

    }

    public static class CustomTransitionEvaluation2 extends AbstractTransitionEvaluation {

        @Override
        void fork(Execution execution) {
            def entity = execution.getEntity()
            def flows = ((OutgoingFlowAware) entity)?.outgoingFlows

            flows.each { execution.addEnabledFlowId(it?.id) }
        }

        @Override
        boolean join(Execution execution) {
            def entity = execution.getEntity()
            def flows = ((IncomingFlowAware) entity)?.incomingFlows

            return flows.findAll { it.finished }.size() >= 3
        }

    }

}
