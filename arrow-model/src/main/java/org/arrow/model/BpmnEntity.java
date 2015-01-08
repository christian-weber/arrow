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

package org.arrow.model;

import org.arrow.runtime.api.BpmnEntitySpecification;

/**
 * Top level contract for node entity and relationship entity BPMN nodes.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public interface BpmnEntity extends BpmnEntitySpecification {

	/**
	 * Returns the entity id.
	 * 
	 * @return String
	 */
	String getId();

	/**
	 * Returns the entity name.
	 * 
	 * @return String
	 */
	String getName();

	/**
	 * Returns the node id.
	 * 
	 * @return Long
	 */
	Long getNodeId();

}
