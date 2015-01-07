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

package org.arrow.service.engine.execution;

import org.arrow.model.event.endevent.EndEvent;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.model.gateway.Gateway;
import org.arrow.model.transition.Flow;
import org.arrow.model.transition.IncomingFlowAware;
import org.arrow.model.transition.OutgoingFlowAware;
import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ExecutionGroup;
import org.arrow.runtime.logger.LoggerFacade;

import java.util.HashMap;
import java.util.Map;

/**
 * This class can be used to enhance an execution instance of execution group information.
 *
 * @since 1.0.0
 * @author christian.weber
 */
public class ExecutionGroupEnhancer {

    private final static transient LoggerFacade LOGGER = new LoggerFacade(ExecutionGroupEnhancer.class);

    /**
     * Enhances the given {@link Execution} instance with {@link ExecutionGroup} information.
     * Returns a map of flow ids to {@link ExecutionGroup} instances.
     *
     * @param execution the execution instance
     * @return Map
     */
    public static Map<String, ExecutionGroup> enhance(Execution execution, ExecutionGroup executionGroup) {

        Map<String, ExecutionGroup> map = new HashMap<>();

        // ends new execution groups if necessary
        // **************************************
        if (executionGroup != null && isExecutionGroupEnd(execution.getEntity())) {
            executionGroup.setFinished(true);
//            LOGGER.debug("set to finished:  %s", execution.getEntity());

            IncomingFlowAware incomingFlowAware = (IncomingFlowAware) execution.getEntity();
            for (Flow flow : incomingFlowAware.getIncomingFlows()) {
                LOGGER.debug("end     execution group:  %s", execution.getEntity());

                execution.addExecutionGroup(executionGroup);
                map.put(flow.getId(), executionGroup);
            }
        }

        // starts new execution groups if necessary
        // ****************************************
        if (execution.isFinished() && isExecutionGroupStart(execution.getEntity())) {
            OutgoingFlowAware outgoingFlowAware = (OutgoingFlowAware) execution.getEntity();

            for (Flow flow : outgoingFlowAware.getOutgoingFlows()) {
                LOGGER.debug("start   execution group:  %s", execution.getEntity());

                if (execution.getEnabledFlowIds().contains(flow.getId())) {
                    ExecutionGroup group = new ExecutionGroup();
                    group.setId(String.valueOf(System.nanoTime()));
                    execution.addExecutionGroup(group);
                    map.put(flow.getId(), group);
                }
            }

        }

        if (executionGroup != null && isWithinExecutionGroup(execution.getEntity())) {
            OutgoingFlowAware outgoingFlowAware = (OutgoingFlowAware) execution.getEntity();
            for (Flow flow : outgoingFlowAware.getOutgoingFlows()) {
                LOGGER.debug("enhance execution group:  %s", execution.getEntity());

                execution.addExecutionGroup(executionGroup);
                map.put(flow.getId(), executionGroup);
            }
        }

        return map;
//        return new HashMap<>();
    }



    /**
     * Indicates if a new execution group should be generated.
     *
     * @param entity the bpmn node entity
     * @return boolean
     */
    private static boolean isExecutionGroupStart(BpmnNodeEntitySpecification entity) {
        return (entity instanceof StartEvent) || (entity instanceof Gateway);
    }

    /**
     * Indicates if a execution group should be closed.
     *
     * @param entity the bpmn node entity
     * @return boolean
     */
    private static boolean isExecutionGroupEnd(BpmnNodeEntitySpecification entity) {
        return (entity instanceof EndEvent) || (entity instanceof Gateway);
    }


    private static boolean isWithinExecutionGroup(BpmnNodeEntitySpecification entity) {
        return !isExecutionGroupStart(entity) && !isExecutionGroupEnd(entity);
    }

}
