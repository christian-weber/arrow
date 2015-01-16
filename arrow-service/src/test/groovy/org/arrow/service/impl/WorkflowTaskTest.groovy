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

import com.thoughtworks.xstream.annotations.XStreamAlias
import org.arrow.test.Given
import org.arrow.test.WorkflowTest
import org.arrow.test.spock.spring.WorkflowDslTrait
import spock.lang.Specification
import spock.lang.Timeout

@WorkflowTest
//@Timeout(10)
public class WorkflowTaskTest extends Specification implements WorkflowDslTrait {

    @Given("task/serviceTask.bpmn20.xml")
    def "test task (service, serviceClass)"() {
        when:
            def pi = startById "SERVICE_TASK_WORKFLOW_TEST"
        then:
            await(pi)
            assertSuccess pi, "startevent1"
            assertSuccess pi, "servicetask1"
            assertSuccess pi, "endevent1"
    }

    @Given("task/serviceTask.bpmn20.xml")
    def "test task (service, beanName)"() {
        when:
            def pi = startById "B_SERVICE_TASK_WORKFLOW_TEST"
        then:
            await(pi)
            assertSuccess pi, "B_startevent1"
            assertSuccess pi, "B_servicetask1"
            assertSuccess pi, "B_endevent1"
    }

    @Given("task/serviceTask.bpmn20.xml")
    def "test task (service, expression)"() {
        when:
            def pi = startById "C_SERVICE_TASK_WORKFLOW_TEST"
        then:
            await(pi)
            assertSuccess pi, "C_startevent1"
            assertSuccess pi, "C_servicetask1"
            assertSuccess pi, "C_endevent1"
    }

    @Given("task/manualTask.bpmn20.xml")
    def "test task (manual)"() {
        when:
            def pi = startById "MANUAL_TASK_WORKFLOW_TEST"
        then:
            await(pi)
            assertSuccess pi, "startevent1"
            assertSuccess pi, "manualtask1"
            assertSuccess pi, "endevent1"
    }

    @Given("task/userTask.bpmn20.xml")
    def "test task (user)"() {
        when:
            def pi = startById "USER_TASK_WORKFLOW_TEST"

            await 100
            finish pi, "usertask1"
        then:
            await(pi)
            assertSuccess pi, "startevent1"
            assertSuccess pi, "usertask1"
            assertSuccess pi, "endevent1"
    }

    @Given("task/scriptTask.bpmn20.xml")
    def "test task (script)"() {
        when:
            def pi = startById "SCRIPT_TASK_WORKFLOW_TEST"

        then:
            await(pi)
            assertSuccess pi, "startevent1"
            assertSuccess pi, "scripttask1"
            assertSuccess pi, "endevent1"
    }

    @Given("task/sendTask.bpmn20.xml")
    def "test task (send)"() {
        when:
            def pi = startById "SEND_TASK_WORKFLOW_TEST"

        then:
            await(pi)
            assertSuccess pi, "startevent1"
            assertSuccess pi, "sendtask1"
            assertSuccess pi, "endevent1"
    }

    @Given("task/receiveTask.bpmn20.xml")
    def "test task (receive)"() {
        when:
            def pi = startById "RECEIVE_TASK_WORKFLOW_TEST"

            await 100
            message("test-message")

        then:
            await(pi)
            assertSuccess pi, "startevent1"
            assertSuccess pi, "receivetask1"
            assertSuccess pi, "endevent1"
    }

    @Given("task/receiveTask.bpmn20.xml")
    def "test task (receive, instantiate)"() {
        when:
            def pi = startByMessage("test-message")

        then:
            await(pi)
            assertSuccess pi, "B_receivetask1"
            assertSuccess pi, "B_endevent1"
    }

    @Given("task/businessRuleTask.bpmn20.xml")
    def "test task (businessRule)"() {
        when:
            def pi = startById "BUSINESSRULE_TASK_WORKFLOW_TEST", [message: new RuleObject(), list: new ArrayList<>()]

        then:
            await(pi)
            assertSuccess pi, "startevent1"
            assertSuccess pi, "businessruletask1"
            assertSuccess pi, "endevent1"
    }

    @XStreamAlias("RuleObject")
    public static class RuleObject implements Serializable {

        public static final int STATUS_1 = 0;
        public static final int STATUS_2 = 1;

        private int status = STATUS_1;

        public int getStatus() {
            return this.status;
        }

        public void setStatus(final int status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "RuleObject{status=" + getStatus() + '}';
        }

    }

}
