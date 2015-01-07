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

package org.arrow.runtime.message.impl;

import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.message.AbstractEventMessage;
import org.arrow.runtime.execution.ProcessInstance;

/**
 * Immutable message for aborting BPMN processes.
 * 
 * @author christian.weber
 * @since 1.0
 */
public class AbortEventMessage extends AbstractEventMessage {

	private final BpmnNodeEntitySpecification entity;

	public AbortEventMessage(BpmnNodeEntitySpecification entity, ProcessInstance pi) {
        super(pi);
		this.entity = entity;
	}

	/**
	 * Returns the {@link BpmnNodeEntitySpecification} to abort.
	 * 
	 * @return BpmnNodeEntity
	 */
	public BpmnNodeEntitySpecification getEntity() {
		return entity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "abort the entity with id " + entity.getId();
	}

}
