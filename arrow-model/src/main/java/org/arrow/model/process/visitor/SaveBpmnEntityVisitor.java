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

package org.arrow.model.process.visitor;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.arrow.model.BpmnNodeEntity;
import org.arrow.model.event.boundary.BoundaryEvent;
import org.arrow.model.event.endevent.EndEvent;
import org.arrow.model.event.intermediate.catching.IntermediateCatchEvent;
import org.arrow.model.event.intermediate.throwing.IntermediateThrowEvent;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.model.gateway.Gateway;
import org.arrow.model.process.SubProcess;
import org.arrow.model.task.Task;
import org.arrow.model.visitor.BpmnNodeEntityVisitor;

/**
 * {@link BpmnNodeEntityVisitor} implementation used to save the visited BPMN node
 * elements. In addition to that every visited BPMN node element is cached
 * within a cache map so that other components could use the cache map to gain
 * access to the stored BPMN elements without fetching it from the database.
 * Beside the BPMN node elements which are going to be stored the eventually
 * present event definition is also stored within the cache map.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class SaveBpmnEntityVisitor implements BpmnNodeEntityVisitor {

	private Logger logger = Logger.getLogger(getClass());

	private final Neo4jTemplate template;
	private final ApplicationContext context;

	public SaveBpmnEntityVisitor(ApplicationContext context) {
		this.context = context;
		this.template = context.getBean(Neo4jTemplate.class);
	}

	/**
	 * Returns the {@link ApplicationContext} instance.
	 * 
	 * @return ApplicationContext
	 */
	public ApplicationContext getApplicationContext() {
		return context;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitStartEvent(StartEvent startEvent) {
		saveElement(startEvent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitEndEvent(EndEvent endEvent) {
		saveElement(endEvent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitIntermediateThrowEvent(IntermediateThrowEvent ite) {
		saveElement(ite);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitIntermediateCatchEvent(IntermediateCatchEvent ice) {
		saveElement(ice);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitBoundaryEvent(BoundaryEvent be) {
		saveElement(be);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitTask(Task task) {
		saveElement(task);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitSubProcess(SubProcess subProcess) {
		saveElement(subProcess);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitGateway(Gateway gateway) {
		saveElement(gateway);
	}

	/**
	 * Stores the given {@link BpmnNodeEntity} instance.
	 * 
	 * @param element
	 */
	private void saveElement(BpmnNodeEntity element) {
		template.save(element);
		logger.info("save " + element + " - " + element.getId());
	}

}
