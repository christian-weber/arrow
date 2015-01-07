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

package org.arrow.service.engine.concurrent;

import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.impl.DefaultExecuteEventMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
* Callable implementation used to notify gateway implementations of an synchronisation event.
 *
 * @since 1.0.0
 * @author christian.weber
*/
public class SynchronisationCallable implements Callable<Iterable<EventMessage>> {

    private final Execution execution;
    private final ExecutionService service;

    public SynchronisationCallable(Execution execution, ExecutionService service) {
        this.execution = execution;
        this.service = service;
    }

    /**
     * Notify all present inclusive gateways that a token is going to be consumed
     *
     * @return Iterable
     */
    @Override
    public Iterable<EventMessage> call() {

        final ProcessInstance pi = execution.getProcessInstance();
        final String piId = execution.getProcessInstance().getId();

        Set<? extends BpmnNodeEntitySpecification> gateways;
        gateways = service.data().gateway().findInclusiveGatewayNotInState(piId, State.SUCCESS.name());

        return gateways.stream().map(obj -> new DefaultExecuteEventMessage(obj, pi)).collect(Collectors.toList());
    }
}
