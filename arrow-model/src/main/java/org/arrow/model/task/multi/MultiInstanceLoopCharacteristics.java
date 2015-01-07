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

package org.arrow.model.task.multi;

import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

/**
 * BPMN 2.0 multi instance loop characteristics implementation.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("MultiInstanceLoopCharacteristics")
public class MultiInstanceLoopCharacteristics {

	/** The node id. */
	@GraphId
	private Long nodeId;

	/** The id. */
	@Indexed
	private String id;

	/** The behavior. */
	private Behavior behavior;

	/** The sequential. */
	private boolean sequential;

	/** The none behavior event ref. */
	private String noneBehaviorEventRef;

	/** The one behavior event ref. */
	private String oneBehaviorEventRef;

	/** The loop cardinality. */
	@Fetch
	@RelatedTo(type="LOOP_CARDINALITY", direction=Direction.OUTGOING)
	private LoopCardinality loopCardinality;

	/**
	 * Gets the node id.
	 * 
	 * @return the node id
	 */
	public Long getNodeId() {
		return nodeId;
	}

	/**
	 * Sets the node id.
	 * 
	 * @param nodeId
	 *            the new node id
	 */
	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the behavior.
	 * 
	 * @return the behavior
	 */
	public Behavior getBehavior() {
		return behavior;
	}

	/**
	 * Sets the behavior.
	 * 
	 * @param behavior
	 *            the new behavior
	 */
	public void setBehavior(Behavior behavior) {
		this.behavior = behavior;
	}

	/**
	 * Checks if is sequential.
	 * 
	 * @return true, if is sequential
	 */
	public boolean isSequential() {
		return sequential;
	}

	/**
	 * Sets the sequential.
	 * 
	 * @param sequential
	 *            the new sequential
	 */
	public void setSequential(boolean sequential) {
		this.sequential = sequential;
	}

	/**
	 * Gets the none behavior event ref.
	 * 
	 * @return the none behavior event ref
	 */
	public String getNoneBehaviorEventRef() {
		return noneBehaviorEventRef;
	}

	/**
	 * Sets the none behavior event ref.
	 * 
	 * @param noneBehaviorEventRef
	 *            the new none behavior event ref
	 */
	public void setNoneBehaviorEventRef(String noneBehaviorEventRef) {
		this.noneBehaviorEventRef = noneBehaviorEventRef;
	}

	/**
	 * Gets the one behavior event ref.
	 * 
	 * @return the one behavior event ref
	 */
	public String getOneBehaviorEventRef() {
		return oneBehaviorEventRef;
	}

	/**
	 * Sets the one behavior event ref.
	 * 
	 * @param oneBehaviorEventRef
	 *            the new one behavior event ref
	 */
	public void setOneBehaviorEventRef(String oneBehaviorEventRef) {
		this.oneBehaviorEventRef = oneBehaviorEventRef;
	}

	/**
	 * Gets the loop cardinality.
	 * 
	 * @return the loop cardinality
	 */
	public LoopCardinality getLoopCardinality() {
		return loopCardinality;
	}

	/**
	 * Sets the loop cardinality.
	 * 
	 * @param loopCardinality
	 *            the new loop cardinality
	 */
	public void setLoopCardinality(LoopCardinality loopCardinality) {
		this.loopCardinality = loopCardinality;
	}

	/**
	 * The multi instance loop characteristics behavior enumeration.
	 * 
	 * @author christian.weber
	 * @since 1.0.0
	 */
	public static enum Behavior {

		/** The None. */
		None,
		/** The One. */
		One,
		/** The All. */
		All,
		/** The Complex. */
		Complex
	}

}
