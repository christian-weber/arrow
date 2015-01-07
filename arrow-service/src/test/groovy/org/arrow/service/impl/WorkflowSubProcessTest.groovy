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
public class WorkflowSubProcessTest extends Specification implements WorkflowDslTrait {

    @Given("subprocess/signalEventSubProcess.bpmn20.xml")
    def "performance"() {
            when:
                def pi = startById "signalEventSubProcessTest"
            then:
                await(pi)
                assertSuccess(pi, "endevent1")
                assertSuccess(pi, "eventsubprocess")

    }

    @Given("subprocess/signalEventSubProcess.bpmn20.xml")
    def "test event sub process (signal, non interrupting)"() {
        when:
            def pi = startById "signalEventSubProcessTest"
        then:
            await(pi)
            assertSuccess(pi, "endevent1")
            assertSuccess(pi, "eventsubprocess")
    }

    @Given("subprocess/signalEventSubProcess.bpmn20.xml")
    def "test event sub process (signal, interrupting)"() {
        when:
            def pi = startById "signalEventSubProcessTest_interrupting"
        then:
            await(pi)
            assertSuspend(pi, "endevent4")
            assertSuccess(pi, "eventsubprocess2")
    }

    @Given("subprocess/messageEventSubProcess.bpmn20.xml")
    def "test event sub process (message, non interrupting)"() {
        when:
            def pi = startById "MESSAGE_EVENT_SUBPROCESS_WORKFLOW_TEST_1"
        then:
            await(pi)
            assertSuccess(pi, "endevent1")
            assertSuccess(pi, "eventsubprocess")
    }

    @Given("subprocess/messageEventSubProcess.bpmn20.xml")
    def "test event sub process (message, interrupting)"() {
        when:
            def pi = startById "MESSAGE_EVENT_SUBPROCESS_WORKFLOW_TEST_2"
        then:
            await(pi)
            assertSuspend(pi, "B_endevent1")
            assertSuccess(pi, "B_eventsubprocess")
    }

    @Given("subprocess/subProcess.bpmn20.xml")
    def "test sub process"() {
        when:
            def pi = startById "subProcessTest"
        then:
            await(pi)
            assertSuccess(pi, "endevent1")
            assertSuccess(pi, "subprocess")
    }

    @Given("subprocess/callActivity.bpmn20.xml")
    def "test call activity"() {
        when:
            def pi = startById "callActivityTest"
        then:
            await(pi)
            assertSuccess(pi, "endevent1")
            assertSuccess(pi, "callactivity")
    }

    @Given("subprocess/transaction.bpmn20.xml")
    def "test sub process (transaction)"() {
        when:
            def pi = startById "TRANSACTION_TEST"
        then:
            await(pi)
            assertSuccess(pi, "endevent1")
            assertSuccess(pi, "subprocess")
    }

    @Given("subprocess/transaction.bpmn20.xml")
    def "test sub process (transaction with compensation)"() {
        when:
            def pi = startById("TRANSACTION_TEST", [error:true])
        then:
            await(pi)
            assertSuccess(pi, "endevent1")
            assertSuccess(pi, "subprocess")
    }

    @Given("subprocess/adHoc.bpmn20.xml")
    def "test sub process (ad hoc)"() {
        when:
            def pi = startById "TRANSACTION_TEST"

            await 500
            executeAdHoc(pi, "subprocess", "task1")
            executeAdHoc(pi, "subprocess", "task1")

            await 500
            finishAdHoc(pi, "subprocess")

        then:
            await(pi)
            assertSuccess(pi, "endevent1")
            assertSuccess(pi, "subprocess")
    }


}
