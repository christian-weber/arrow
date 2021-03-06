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

import akka.actor.Actor;
import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.util.StopWatch;
import org.arrow.model.event.endevent.AbstractEndEvent;
import org.arrow.runtime.mapper.AppendMessageMapper;
import org.arrow.runtime.mapper.IterableOfIterable2IterableMessageMapper;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import scala.concurrent.Future;

import java.util.Arrays;

/**
 * BPMN 2.0 none end event implementation.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
public class NoneEndEvent extends AbstractEndEvent {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Future<Iterable<EventMessage>> executeEndEvent(Execution execution, ExecutionService service) {
        execution.setState(State.SUCCESS);
        return finish(execution, service);
    }

}
