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

package org.arrow.model.definition.escalation;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.arrow.model.definition.AbstractEventDefinition;
import org.arrow.model.definition.EventDefinition;
import org.arrow.model.process.event.Escalation;

/**
 * {@link EventDefinition} implementation which represents a escalation event
 * definition.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("EscalationEventDefinition")
public class EscalationEventDefinition extends AbstractEventDefinition {

	/** The escalation ref. */
	@Fetch
	@RelatedTo(type="ESCALATION_REF")
	private Escalation escalationRef;

	/**
	 * Gets the escalation ref.
	 * 
	 * @return the escalation ref
	 */
	public Escalation getEscalationRef() {
		return escalationRef;
	}

	/**
	 * Sets the escalation ref.
	 * 
	 * @param escalationRef
	 *            the new escalation ref
	 */
	public void setEscalationRef(Escalation escalationRef) {
		this.escalationRef = escalationRef;
	}

}
