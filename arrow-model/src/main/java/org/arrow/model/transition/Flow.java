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

import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.arrow.model.BpmnNodeEntity;
import org.arrow.model.BpmnRelationshipEntity;
import org.arrow.runtime.definition.RelationDef;

/**
 * Container for transition information. Stores the source and destination
 * object used by the process engine to execute the process flow.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@RelationshipEntity(type = "SEQUENCE_FLOW")
public interface Flow extends BpmnRelationshipEntity, RelationDef {

	/**
	 * Gets the source ref.
	 * 
	 * @return the source ref
	 */
	BpmnNodeEntity getSourceRef();

	/**
	 * Gets the target ref.
	 * 
	 * @return the target ref
	 */
	BpmnNodeEntity getTargetRef();

	/**
	 * Indicates if the flow is enabled.
	 * @return boolean
	 */
	boolean isEnabled();

	/**
	 * Indicates if the flow is finished.
	 * @return boolean
	 */
	boolean isFinished();

	/**
	 * Enables the flow instance.
	 */
	void enable();
	
}
