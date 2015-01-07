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

package org.arrow.model.event.intermediate.catching;

import org.arrow.model.event.intermediate.AbstractIntermediateEvent;
import org.arrow.model.visitor.BpmnNodeEntityVisitor;

/**
 * Abstract BPMN 2.0 Intermediate Catch Event class.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public abstract class AbstractIntermediateCatchEvent extends
		AbstractIntermediateEvent implements IntermediateCatchEvent {

	/** The parallel multiple. */
	private boolean parallelMultiple;

	/**
	 * Checks if is parallel multiple.
	 * 
	 * @return true, if is parallel multiple
	 */
	public boolean isParallelMultiple() {
		return parallelMultiple;
	}

	/**
	 * Sets the parallel multiple.
	 * 
	 * @param parallelMultiple
	 *            the new parallel multiple
	 */
	public void setParallelMultiple(boolean parallelMultiple) {
		this.parallelMultiple = parallelMultiple;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(BpmnNodeEntityVisitor visitor) {
		visitor.visitIntermediateCatchEvent(this);
	}

}
