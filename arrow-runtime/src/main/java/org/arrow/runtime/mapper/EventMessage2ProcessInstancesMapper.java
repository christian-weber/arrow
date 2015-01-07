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

import java.util.HashSet;
import java.util.Set;

/**
 * Created by christian.weber on 24.07.2014.
 */
public final class EventMessage2ProcessInstancesMapper extends Mapper<Iterable<EventMessage>, Iterable<ProcessInstance>> {

    public static final EventMessage2ProcessInstancesMapper INSTANCE = new EventMessage2ProcessInstancesMapper();

    private EventMessage2ProcessInstancesMapper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<ProcessInstance> apply(Iterable<EventMessage> parameter) {
        Set<ProcessInstance> processInstanceSet = new HashSet<>();

        for (EventMessage message : parameter) {
            processInstanceSet.add(message.getProcessInstance());
        }
        return processInstanceSet;
    }
}

