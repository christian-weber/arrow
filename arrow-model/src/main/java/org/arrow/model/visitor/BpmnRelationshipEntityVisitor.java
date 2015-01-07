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

package org.arrow.model.visitor;

import org.arrow.model.transition.impl.Association;
import org.arrow.model.transition.impl.LinkFlow;
import org.arrow.model.transition.impl.SequenceFlow;

/**
 * Visitor pattern Visitor definition used to traverse BPMN relationship entity
 * elements.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public interface BpmnRelationshipEntityVisitor {

	/**
	 * Visits the given {@link SequenceFlow}.
	 * 
	 * @param flow the flow instance
	 */
	void visitSequenceFlow(SequenceFlow flow);

	/**
	 * Visits the given {@link LinkFlow}.
	 * 
	 * @param flow the link flow instance
	 */
    @SuppressWarnings("unused")
	void visitLinkFlow(LinkFlow flow);

	/**
	 * Visits the given {@link Association}.
	 *
	 * @param association the association instance
	 */
	void visitAssociation(Association association);

}
