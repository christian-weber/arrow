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

package org.arrow.model.event.endevent.impl;

import akka.dispatch.Futures;
import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.util.Assert;
import org.arrow.model.definition.escalation.EscalationEventDefinition;
import org.arrow.model.definition.escalation.EscalationEventDefinitionAware;
import org.arrow.model.event.endevent.AbstractEndEvent;
import org.arrow.model.event.endevent.EndEvent;
import org.arrow.model.process.event.Escalation;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.impl.EscalationEventMessage;
import scala.concurrent.Future;

/**
 * {@link EndEvent} implementation used for BPMN 2 'Escalation' end event type.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("EscalationEndEvent")
public class EscalationEndEvent extends AbstractEndEvent implements
        EscalationEventDefinitionAware {

    @Fetch
    @RelatedTo(type = "EVENT_DEFINITION", direction = Direction.OUTGOING)
    private EscalationEventDefinition eventDefinition;

    /**
     * {@inheritDoc}
     */
    @Override
    public EscalationEventDefinition getEscalationEventDefinition() {
        return eventDefinition;
    }

    /**
     * Sets the escalation event definition.
     *
     * @param eventDefinition the new escalation event definition
     */
    public void setEscalationEventDefinition(
            EscalationEventDefinition eventDefinition) {
        this.eventDefinition = eventDefinition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Future<Iterable<EventMessage>> executeEndEvent(Execution execution, ExecutionService service) {
//        ActorSystem actorSystem = service.getBean(ActorSystem.class);

        final Escalation escalation = eventDefinition.getEscalationRef();
        final ProcessInstance pi = execution.getProcessInstance();

        service.fetchEntity(pi);
        final ProcessInstance ppi = pi.getParentProcessInstance();

        final String code = escalation.getEscalationCode();
        Assert.notNull(code);
        Assert.notNull(ppi);

        Execution escalationExecution = service.getExecutionByEscalationCode(code, ppi.getId());

        EscalationEventMessage message = new EscalationEventMessage(escalation.getEscalationCode(), escalationExecution);
        message.setCancelTarget(execution);

        execution.setState(State.WAITING);
        return Futures.successful(iterableOf(message));
    }

}
