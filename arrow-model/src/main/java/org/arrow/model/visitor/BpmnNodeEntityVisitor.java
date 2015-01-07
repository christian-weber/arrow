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

import org.arrow.model.event.boundary.BoundaryEvent;
import org.arrow.model.event.endevent.EndEvent;
import org.arrow.model.event.intermediate.catching.IntermediateCatchEvent;
import org.arrow.model.event.intermediate.throwing.IntermediateThrowEvent;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.model.gateway.Gateway;
import org.arrow.model.process.SubProcess;
import org.arrow.model.task.Task;

/**
 * Visitor pattern Visitor definition used to traverse BPMN entity elements.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public interface BpmnNodeEntityVisitor {

	/**
	 * Visits the given {@link StartEvent}.
	 * 
	 * @param startEvent the start event instance
	 */
	void visitStartEvent(StartEvent startEvent);

	/**
	 * Visits the given {@link EndEvent}.
	 * 
	 * @param endEvent the end event instance
	 */
	void visitEndEvent(EndEvent endEvent);

	/**
	 * Visits the given {@link IntermediateThrowEvent}.
	 * 
	 * @param ite the intermediate throw event instance
	 */
	void visitIntermediateThrowEvent(IntermediateThrowEvent ite);

	/**
	 * Visits the given {@link IntermediateCatchEvent}.
	 * 
	 * @param ice the intermediate catch event instance
	 */
	void visitIntermediateCatchEvent(IntermediateCatchEvent ice);

	/**
	 * Visits the given {@link BoundaryEvent}.
	 * 
	 * @param be the boundary event instance
	 */
	void visitBoundaryEvent(BoundaryEvent be);
	
	/**
	 * Visits the given {@link Task}.
	 * 
	 * @param task the task instance
	 */
	void visitTask(Task task);
	
	/**
	 * Visits the given {@link SubProcess}.
	 * 
	 * @param subProcess the subProcess instance
	 */
	void visitSubProcess(SubProcess subProcess);
	
	/**
	 * Visits the given {@link Gateway}.
	 * 
	 * @param gateway the gateway instance
	 */
	void visitGateway(Gateway gateway);

}
