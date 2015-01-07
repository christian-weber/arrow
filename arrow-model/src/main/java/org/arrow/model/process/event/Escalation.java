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

package org.arrow.model.process.event;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.support.index.IndexType;
import org.arrow.model.visitor.BpmnEventDefinitionEntityVisitor;

/**
 * BPMN 2.0 Escalation implementation. Defines the escalation code used by sub
 * processes to notify the upper process.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("Escalation")
public class Escalation extends AbstractBpmnEventDefinitionEntity {

	/** The escalation code. */
	@Indexed(indexName = "escalationCodes", indexType=IndexType.FULLTEXT)
	private String escalationCode;

	/** The structure ref. */
	private String structureRef;

	/**
	 * Gets the escalation code.
	 * 
	 * @return the escalation code
	 */
	public String getEscalationCode() {
		return escalationCode;
	}

	/**
	 * Sets the escalation code.
	 * 
	 * @param escalationCode
	 *            the new escalation code
	 */
	public void setEscalationCode(String escalationCode) {
		this.escalationCode = escalationCode;
	}

	/**
	 * Gets the structure ref.
	 * 
	 * @return the structure ref
	 */
	public String getStructureRef() {
		return structureRef;
	}

	/**
	 * Sets the structure ref.
	 * 
	 * @param structureRef
	 *            the new structure ref
	 */
	public void setStructureRef(String structureRef) {
		this.structureRef = structureRef;
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long getVersion() {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(BpmnEventDefinitionEntityVisitor visitor) {
		visitor.visitEscalation(this);
	}

}
