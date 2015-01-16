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

package org.arrow.service.engine.util;

import org.apache.log4j.Logger;
import org.arrow.model.transition.Flow;
import org.arrow.model.transition.OutgoingFlowAware;
import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.message.EntityEventMessage;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for node handling.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public final class NodeUtils {

	private static Logger logger = Logger.getLogger(NodeUtils.class);

	private NodeUtils() {
		super();
	}

	/**
	 * Returns all relevant flows of the given {@link org.arrow.runtime.message.EventMessage} instance. A relevant
	 * flow is specified as a flow which is enabled and therefore marked as
	 * executable.
	 * 
	 * @param msg the current traversed node
	 * @return Set
	 */
	public static Set<Flow> getRelevantFlows(EntityEventMessage msg) {

        BpmnNodeEntitySpecification entity = msg.getEntity();
		Execution execution = msg.getExecution();

		if (entity instanceof OutgoingFlowAware) {
			OutgoingFlowAware ota = (OutgoingFlowAware) entity;

			logger.debug("execute outgoing flows of " + entity.getId());

			if (ota.getOutgoingFlows().isEmpty()) {
				return Collections.emptySet();
			}

			Set<Flow> relevant = new HashSet<>();
			for (Flow flow : ota.getOutgoingFlows()) {

				if (isFlowEnabled(execution, flow)) {
					relevant.add(flow);
					flow.enable();
				} else {
					String id = flow.getId();
					logger.debug("flow with id " + id + " is disabled");
				}
			}
			return relevant;
		}
		return new HashSet<>();
	}

	private static boolean isFlowEnabled(Execution execution, Flow flow) {
		return execution.getEnabledFlowIds().contains(flow.getId()) || execution.getEnabledFlowIdsContainer().contains(flow.getId());
	}

}
