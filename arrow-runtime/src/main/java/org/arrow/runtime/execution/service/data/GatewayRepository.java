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

package org.arrow.runtime.execution.service.data;


import org.arrow.runtime.api.BpmnNodeEntitySpecification;

import java.util.Set;

/**
 * The gateway repository definition.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public interface GatewayRepository {

    /**
     * Returns all inclusive gateways of the given process which are not in the given state.
     *
     * @param piId  the process instance id
     * @param state the state instance
     * @return Set
     */
    Set<? extends BpmnNodeEntitySpecification> findInclusiveGatewayNotInState(String piId, String state);

}
