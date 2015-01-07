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

package org.arrow.model.transition;

import java.util.Set;

import org.arrow.model.transition.impl.Association;
import org.arrow.model.transition.impl.SequenceFlow;

/**
 * TransitionAware interface used to indicate a BPMN element as
 * a element which has a income transition.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public interface IncomingFlowAware {

	/**
	 * Returns the set of all incoming flows.
	 * 
	 * @return Set
	 */
	Set<? extends Flow> getIncomingFlows();

	/**
	 * Adds the given {@link SequenceFlow} to the incoming flows.
	 * 
	 * @param flow the flow instance
	 */
	void addIncomingFlow(SequenceFlow flow);

	/**
	 * Adds the given {@link SequenceFlow} to the incoming flows.
	 *
	 * @param flow the flow instance
	 */
	default void addIncomingFlow(Association flow) {
		// do nothing
	}

}
