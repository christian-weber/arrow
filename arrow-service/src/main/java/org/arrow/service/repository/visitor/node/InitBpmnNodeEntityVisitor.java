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

package org.arrow.service.repository.visitor.node;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.util.Assert;
import org.arrow.model.BpmnEntity;
import org.arrow.model.BpmnNodeEntity;
import org.arrow.model.PlaceholderBpmnEntity;
import org.arrow.model.definition.link.LinkEventDefinition;
import org.arrow.model.event.boundary.AbstractBoundaryEvent;
import org.arrow.model.event.boundary.BoundaryEvent;
import org.arrow.model.event.boundary.BoundaryEventAware;
import org.arrow.model.event.endevent.EndEvent;
import org.arrow.model.event.intermediate.catching.IntermediateCatchEvent;
import org.arrow.model.event.intermediate.throwing.IntermediateThrowEvent;
import org.arrow.model.event.intermediate.throwing.impl.LinkIntermediateThrowEvent;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.model.gateway.Gateway;
import org.arrow.model.process.SubProcess;
import org.arrow.model.task.Task;
import org.arrow.model.transition.impl.LinkFlow;
import org.arrow.model.visitor.BpmnNodeEntityVisitor;

/**
 * {@link BpmnNodeEntityVisitor} implementation used to cache the visited BPMN
 * node entities so that other components could use the cache map to gain access
 * to the stored BPMN elements without fetching it from the database.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class InitBpmnNodeEntityVisitor implements BpmnNodeEntityVisitor {

	private final Neo4jTemplate template;
	private final Map<String, BpmnNodeEntity> cache;

	public InitBpmnNodeEntityVisitor(ApplicationContext context,
			Map<String, BpmnNodeEntity> cache) {
		this.cache = cache;
		this.template = context.getBean(Neo4jTemplate.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitStartEvent(StartEvent startEvent) {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitEndEvent(EndEvent endEvent) {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitIntermediateThrowEvent(IntermediateThrowEvent ite) {

		if (ite instanceof LinkIntermediateThrowEvent) {
			LinkIntermediateThrowEvent event = (LinkIntermediateThrowEvent) ite;

			LinkEventDefinition definition = event.getLinkEventDefinition();
			final String linkName = definition.getLinkName();

			BpmnNodeEntity catchEvent = cache.get(linkName);
			Assert.notNull(catchEvent, "no link catch event found: " + linkName);

			LinkFlow flow = new LinkFlow();
			flow.setId(linkName);
			flow.setSourceRef(event);
			flow.setTargetRef(catchEvent);

			event.addOutgoingLinkFlow(flow);

			cache.put(event.getId(), event);

			template.save(flow);
			template.save(event);

		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitIntermediateCatchEvent(IntermediateCatchEvent ice) {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitBoundaryEvent(BoundaryEvent be) {

		BpmnNodeEntity placeholder = be.getAttachedToRef();
		BpmnNodeEntity element = swapElement(placeholder, cache);

		((AbstractBoundaryEvent) be).setAttachedToRef(element);
		((BoundaryEventAware) element).addBoundaryEvent(be);

		template.save(be);
	}

	/**
	 * Returns the bpmn entity which should be replaced with the placeholder.
	 * 
	 * @param element the bpmn entity
	 * @param cache the bpmn entities cache
	 * @return BpmnNodeEntity
	 */
	private BpmnNodeEntity swapElement(BpmnEntity element,
			Map<String, BpmnNodeEntity> cache) {
		PlaceholderBpmnEntity pe = (PlaceholderBpmnEntity) element;
		return cache.get(pe.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitTask(Task task) {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitSubProcess(SubProcess subProcess) {

        if (subProcess.isForCompensation()) {
            subProcess.setForCompensation(true);
        }

		// do nothing
//        if (subProcess.isTriggeredByEvent()) {
//            subProcess.addNotifier(new EventSubProcessNotifiable());
//            template.save(subProcess);
//        }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitGateway(Gateway gateway) {
		// do nothing
	}

}
