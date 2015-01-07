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

package org.arrow.model.process;

import org.arrow.model.BpmnNodeEntity;
import org.arrow.model.event.boundary.BoundaryEventAware;
import org.arrow.model.transition.IncomingFlowAware;
import org.arrow.model.transition.OutgoingFlowAware;
import org.arrow.runtime.api.process.ProcessSpecification;

/**
 * Marker interface for all bpmn elements which have the capability to run
 * as a sub process.
 *
 * @see org.arrow.model.process.SubProcess
 * @see org.arrow.model.task.impl.CallActivityTask
 *
 * @since 1.0.0
 * @author christian.weber
 */
public interface SubProcessEntity extends ProcessSpecification, BpmnNodeEntity, BoundaryEventAware, IncomingFlowAware, OutgoingFlowAware {

}
