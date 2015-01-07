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

package org.arrow.model.visitor.adapter;

import org.arrow.model.event.boundary.BoundaryEvent;
import org.arrow.model.event.endevent.EndEvent;
import org.arrow.model.event.intermediate.catching.IntermediateCatchEvent;
import org.arrow.model.event.intermediate.throwing.IntermediateThrowEvent;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.model.gateway.Gateway;
import org.arrow.model.process.SubProcess;
import org.arrow.model.task.Task;
import org.arrow.model.visitor.BpmnNodeEntityVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link BpmnNodeEntityVisitor} implementation used to wrap multiple
 * {@link BpmnNodeEntityVisitor} implementations in order to handle them as one
 * instance. With this adapter class multiple visitor implementations can be
 * processed by a single visit tree call.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class BpmnNodeEntityVisitorAdapter implements BpmnNodeEntityVisitor {

	private List<BpmnNodeEntityVisitor> visitors = new ArrayList<>();

	public BpmnNodeEntityVisitorAdapter() {
		super();
	}

	public void addVisitor(BpmnNodeEntityVisitor visitor) {
		this.visitors.add(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitStartEvent(StartEvent startEvent) {
		for (BpmnNodeEntityVisitor visitor : visitors) {
			visitor.visitStartEvent(startEvent);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitEndEvent(EndEvent endEvent) {
		for (BpmnNodeEntityVisitor visitor : visitors) {
			visitor.visitEndEvent(endEvent);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitIntermediateThrowEvent(IntermediateThrowEvent ite) {
		for (BpmnNodeEntityVisitor visitor : visitors) {
			visitor.visitIntermediateThrowEvent(ite);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitIntermediateCatchEvent(IntermediateCatchEvent ice) {
		for (BpmnNodeEntityVisitor visitor : visitors) {
			visitor.visitIntermediateCatchEvent(ice);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitBoundaryEvent(BoundaryEvent be) {
		for (BpmnNodeEntityVisitor visitor : visitors) {
			visitor.visitBoundaryEvent(be);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitTask(Task task) {
		for (BpmnNodeEntityVisitor visitor : visitors) {
			visitor.visitTask(task);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitSubProcess(SubProcess subProcess) {
		for (BpmnNodeEntityVisitor visitor : visitors) {
			visitor.visitSubProcess(subProcess);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitGateway(Gateway gateway) {
		for (BpmnNodeEntityVisitor visitor : visitors) {
			visitor.visitGateway(gateway);
		}
	}

}
