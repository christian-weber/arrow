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

package org.arrow.model.transition.impl;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;
import org.arrow.model.AbstractBpmnRelationshipEntity;
import org.arrow.model.BpmnNodeEntity;
import org.arrow.model.transition.Flow;
import org.arrow.model.visitor.BpmnRelationshipEntityVisitor;

/**
 * Container for transition information. Stores the source and destination
 * object used by the process engine to execute/continue the process flow.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@RelationshipEntity(type = "ASSOCIATION")
public class Association extends AbstractBpmnRelationshipEntity implements Flow {

	/** The source ref. */
	@Fetch
	@StartNode
	private BpmnNodeEntity sourceRef;

	/** The target ref. */
	@Fetch
	@EndNode
	private BpmnNodeEntity targetRef;

	/** The enabled. */
	private transient Boolean enabled;

	/** The finished. */
	private transient Boolean finished;

	/**
	 * Sets the source ref.
	 * 
	 * @param sourceRef
	 *            the new source ref
	 */
	public void setSourceRef(BpmnNodeEntity sourceRef) {
		this.sourceRef = sourceRef;
	}

	/**
	 * Sets the target ref.
	 * 
	 * @param targetRef
	 *            the new target ref
	 */
	public void setTargetRef(BpmnNodeEntity targetRef) {
		this.targetRef = targetRef;
	}

	/**
	 * {@inheritDoc}
	 */
	public BpmnNodeEntity getSourceRef() {
		return sourceRef;
	}

	/**
	 * {@inheritDoc}
	 */
	public BpmnNodeEntity getTargetRef() {
		return targetRef;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {
		return enabled == null ? false : enabled;
	}

	/**
	 * Sets the finished.
	 * 
	 * @param finished
	 *            the new finished
	 */
	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isFinished() {
		return finished == null ? false : finished;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(BpmnRelationshipEntityVisitor visitor) {
		visitor.visitAssociation(this);
	}

	@Override
	public void enable() {
		this.finished = true;
		this.enabled = true;
	}
}
