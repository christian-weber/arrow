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

package org.arrow.runtime.mapper;

import akka.dispatch.Mapper;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.ProcessInstance;

import java.util.Iterator;

/**
 * Mapper implementation which maps event messages to process instances.
 *
 * @since 1.0.0
 * @author christian.weber
 */
public final class EventMessage2ProcessInstanceMapper extends Mapper<Iterable<EventMessage>, ProcessInstance> {

    public static final EventMessage2ProcessInstanceMapper INSTANCE = new EventMessage2ProcessInstanceMapper();

    private EventMessage2ProcessInstanceMapper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessInstance apply(Iterable<EventMessage> parameter) {
        Iterator<EventMessage> iterator = parameter.iterator();
        return iterator.hasNext() ? iterator.next().getProcessInstance() : null;
    }
}

