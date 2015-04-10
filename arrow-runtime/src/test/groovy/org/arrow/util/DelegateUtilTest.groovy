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

package org.arrow.util

import org.arrow.runtime.api.gateway.AbstractTransitionEvaluation
import org.arrow.runtime.api.task.JavaDelegate
import org.arrow.runtime.execution.Execution
import org.arrow.runtime.message.EventMessage
import scala.concurrent.Future
import spock.lang.Specification

public class DelegateUtilTest extends Specification {

    def "java delegate resolution with a valid class name should return an instance"() {
        when:
            def className = JavaDelegateBean.class.name
            def delegate = DelegateUtil.getJavaDelegate(className);
        then:
            delegate != null
    }

    def "java delegate resolution with an invalid class name should throw an exception"() {
        when:
            DelegateUtil.getJavaDelegate("unknown");
        then:
            thrown IllegalArgumentException
    }

    def "java delegate resolution with a null argument should throw an exception"() {
        when:
            DelegateUtil.getJavaDelegate(null);
        then:
            thrown IllegalArgumentException
    }

    def "transition evaluation resolution with a valid class name should return an instance"() {
        when:
            def className = TransitionEvaluationBean.class.name
            def delegate = DelegateUtil.getTransitionEvaluation(className);
        then:
            delegate != null
    }

    def "transition evaluation resolution with an invalid class name should throw an exception"() {
        when:
            DelegateUtil.getTransitionEvaluation("unknown")
        then:
            thrown IllegalArgumentException
    }

    def "transition evaluation resolution with a null argument should throw an exception"() {
        when:
            DelegateUtil.getTransitionEvaluation(null);
        then:
            thrown IllegalArgumentException
    }

    public static class JavaDelegateBean implements JavaDelegate {

        @Override
        public Future<Iterable<EventMessage>> execute(Execution execution) {
            return FutureUtil.result();
        }

    }

    public static class TransitionEvaluationBean extends AbstractTransitionEvaluation {

    }

}
