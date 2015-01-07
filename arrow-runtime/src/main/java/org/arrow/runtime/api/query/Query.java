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

package org.arrow.runtime.api.query;

/**
 * Classes which implements this interface are able to query for
 * BPMN model instances.
 *
 * @param <S> the BPMN model instance type
 * @param <T> the concrete query type
 */
public interface Query<S, T> {

    /**
     * Adds the process instance id restriction used to lookup the BPMN model instance.
     *
     * @param id the process instance id
     * @return T
     */
    T processInstanceId(String id);

    /**
     * Adds the signal reference restriction used to lookup the BPMN model instance.
     *
     * @param signalRef the signal reference
     * @return T
     */
    T signalEventDefinition(String signalRef);

    /**
     * Adds the message reference restriction used to lookup the BPMN model instance.
     *
     * @param messageRef the message reference
     * @return T
     */
    T messageEventDefinition(String messageRef);

    /**
     * Returns a single BPMN model instance.
     *
     * @return S
     */
    S singleResult();

    /**
     * Returns an iterable of BPMN model instances.
     *
     * @return Iterable
     */
    Iterable<S> list();

}
