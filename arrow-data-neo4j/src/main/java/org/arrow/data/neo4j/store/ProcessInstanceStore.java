/*
 * Copyright 2014 Christian Weber
 *
 * This file is build on Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.arrow.data.neo4j.store;

import org.arrow.model.process.AdHocSubProcess;
import org.arrow.model.process.SubProcessEntity;
import org.arrow.runtime.api.StartEventSpecification;
import org.arrow.runtime.api.task.TaskSpecification;
import org.arrow.runtime.execution.ProcessInstance;

import java.util.Map;

/**
 * Store repository definition used to persist {@link ProcessInstance} instances.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public interface ProcessInstanceStore {

    /**
     * Stores a {@link ProcessInstance} with the given {@link StartEventSpecification} and
     * the given variables.
     *
     * @param event the start event instance
     * @param map   the variables map
     * @return ProcessInstance
     */
    public ProcessInstance store(StartEventSpecification event, Map<String, Object> map);
    public ProcessInstance store(TaskSpecification event, Map<String, Object> map);
    public ProcessInstance store(AdHocSubProcess adHocSubProcess, ProcessInstance parentProcessInstance);

    /**
     * Stores a {@link ProcessInstance}.
     *
     * @param event the start event instance
     * @return ProcessInstance
     */
    public ProcessInstance store(StartEventSpecification event);

    /**
     * Stores a {@link ProcessInstance} with the given {@link StartEventSpecification} and
     * the parent {@link ProcessInstance}.
     *
     * @param event    the start event instance
     * @param parentPi the parent process instance
     * @return ProcessInstance
     */
    public ProcessInstance store(StartEventSpecification event, ProcessInstance parentPi);

    /**
     * Stores a {@link ProcessInstance} with the given {@link StartEventSpecification} and
     * the parent {@link ProcessInstance} as well as the variables map.
     *
     * @param event    the start event instance
     * @param parentPi the parent process instance
     * @param map      the variables map
     * @return ProcessInstance
     */
    public ProcessInstance store(StartEventSpecification event, ProcessInstance parentPi,
                                 Map<String, Object> map);

    /**
     * Stores a {@link ProcessInstance} with the given {@link SubProcessEntity} and
     * the parent {@link ProcessInstance}.
     *
     * @param sub      the sub process instance
     * @param event    the start event instance
     * @param parentPi the parent process instance
     * @return ProcessInstance
     */
    public ProcessInstance store(SubProcessEntity sub, StartEventSpecification event, ProcessInstance parentPi);

    /**
     * Stores a {@link ProcessInstance} with the given {@link SubProcessEntity} and
     * the parent {@link ProcessInstance} as well as the variables map.
     *
     * @param sub      the sub process instance
     * @param event    the start event instance
     * @param parentPi the parent process instance
     * @param map      the variables map
     * @return ProcessInstance
     */
    public ProcessInstance store(SubProcessEntity sub, StartEventSpecification event, ProcessInstance parentPi,
                                 Map<String, Object> map);

}