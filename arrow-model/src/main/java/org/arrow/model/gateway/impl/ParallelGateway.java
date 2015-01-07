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

package org.arrow.model.gateway.impl;

import org.arrow.model.gateway.AbstractGateway;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionDataService.SynchronisationResult;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.infrastructure.TokenEventMessage;
import org.springframework.data.neo4j.annotation.NodeEntity;

import java.util.Arrays;
import java.util.List;

import static org.arrow.runtime.message.infrastructure.TokenEventMessage.TokenAction.CONSUME;

/**
 * BPMN parallel gateway implementation. Joins all incoming sequence flows and
 * starts all outgoing sequence flows parallel.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
public class ParallelGateway extends AbstractGateway {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventMessage> fork(Execution execution, ExecutionService service) {

        getOutgoingFlows().stream().forEach(flow -> flow.enableRelation(execution));

        // mark gateway as finished
        execution.setState(State.SUCCESS);
        finish(execution, service);

        return Arrays.asList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JoinResult join(Execution execution, ExecutionService service) {
        SynchronisationResult result = service.data().breadthFirstSynchronization(this);

        if (result.isSynchronised()) {
            return new JoinResult(true);
        }

        // avoid gateway finish
        EventMessage tokenMsg = new TokenEventMessage(execution, CONSUME);
        return new JoinResult(false, tokenMsg);
    }

}
