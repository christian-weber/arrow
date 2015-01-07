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

package org.arrow.model.definition;

import akka.dispatch.Futures;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.util.StopWatch;
import org.arrow.model.BpmnNodeEntity;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.MultipleEventExecution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.EventMessage;
import scala.concurrent.Future;

import java.util.Arrays;

/**
 * Abstract event handler introduction class.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public abstract class AbstractEventHandlerIntroduction extends
        DelegatingIntroductionInterceptor {

    /**
     * Handles the received event.
     *
     * @param execution       the execution instance
     * @param service         the service instance
     * @param eventDefinition the event definition instance
     */
    protected Future<Iterable<EventMessage>> handleEvent(Execution execution, ExecutionService service, EventDefinition eventDefinition) {

        Class<MultipleEventExecution> targetType = MultipleEventExecution.class;

        // if the execution has not been saved before
        // save it in order to enable projectTo functionality
        service.saveEntity(execution);

        MultipleEventExecution mee = service.swapEntity(execution, targetType);
        service.fetchEntity(mee);

        mee.addReceivedEventId(eventDefinition.getId());

        // finish the entity if state is SUCCESS
        State state = mee.getState();
        if (state != null && state.isSuccess()) {
            execution.setState(State.SUCCESS);
            return ((BpmnNodeEntity) execution.getEntity()).finish(execution, service);
        }

        service.saveEntity(mee);

        return Futures.successful(iterableOf());
    }

    protected Iterable<EventMessage> iterableOf(EventMessage... messages) {
        return Arrays.asList(messages);
    }

}
