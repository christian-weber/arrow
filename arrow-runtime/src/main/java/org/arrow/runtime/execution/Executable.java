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

package org.arrow.runtime.execution;

import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.service.ExecutionService;
import scala.concurrent.Future;

/**
 * Classes which implements this interface are able to execute BPMN entities.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public interface Executable {

    /**
     * Executes the given {@link Execution}.
     *
     * @param execution the execution instance
     * @param service   the service instance
     * @return Future
     */
    Future<Iterable<EventMessage>> execute(Execution execution, ExecutionService service);

    /**
     * Finishes the given {@link Execution}.
     *
     * @param execution the execution instance
     * @param service   the service instance
     * @return Future
     */
    Future<Iterable<EventMessage>> finish(Execution execution, ExecutionService service);

}
