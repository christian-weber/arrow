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

package org.arrow.runtime.execution.service.data;

import org.arrow.runtime.api.StartEventSpecification;

import java.util.Set;


public interface StartEventRepository {

    void save(StartEventSpecification startEvent);

    /**
     * Returns a {@link Iterable} result of all {@link StartEventSpecification} events
     * with the given signal ref.
     *
     * @return Set
     */
    Set<? extends StartEventSpecification> findSignalStartEventsByReference(String signalRef);

    /**
     * Returns a {@link StartEventSpecification} by the given messageRef.
     *
     * @param messageRef the message reference value
     * @return StartEvent
     */
    StartEventSpecification findMessageStartEvent(String messageRef);

    /**
     * Returns a {@link StartEventSpecification} by the given process id.
     *
     * @param processId the process id
     * @return NoneStartEvent
     */
    StartEventSpecification findNoneStartEventByProcessId(String processId);

    /**
     * Returns all {@link StartEventSpecification} instances by the given
     * conditional bean name.
     *
     * @param beanName the bean name
     * @return Iterable
     */
    Set<? extends StartEventSpecification> findAllConditionalStartEvents(
            String beanName);

}
