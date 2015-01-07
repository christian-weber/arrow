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

package org.arrow.model.process.visitor.relationship;

import java.util.Map;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.util.Assert;
import org.arrow.model.BpmnEntity;
import org.arrow.model.BpmnNodeEntity;
import org.arrow.model.PlaceholderBpmnEntity;
import org.arrow.model.transition.IncomingFlowAware;
import org.arrow.model.transition.OutgoingFlowAware;
import org.arrow.model.transition.impl.Association;
import org.arrow.model.transition.impl.LinkFlow;
import org.arrow.model.transition.impl.SequenceFlow;
import org.arrow.model.visitor.BpmnRelationshipEntityVisitor;

public class InitBpmnRelationshipEntityVisitor implements
		BpmnRelationshipEntityVisitor {

	private final Neo4jTemplate template;
	private final Map<String, BpmnNodeEntity> cache;

	public InitBpmnRelationshipEntityVisitor(ApplicationContext context,
			Map<String, BpmnNodeEntity> cache) {
		this.cache = cache;
		this.template = context.getBean(Neo4jTemplate.class);
	}

	/**
	 * Returns the bpmn entity which should be replaced with the placeholder.
	 * 
	 * @param element
	 * @param cache
	 * @return BpmnNodeEntity
	 */
	private BpmnNodeEntity swapElement(BpmnEntity element,
			Map<String, BpmnNodeEntity> cache) {
		PlaceholderBpmnEntity pe = (PlaceholderBpmnEntity) element;
		return cache.get(pe.getId());
	}

	@Override
	public void visitSequenceFlow(SequenceFlow flow) {
		BpmnNodeEntity source = flow.getSourceRef();
		BpmnNodeEntity target = flow.getTargetRef();

		BpmnNodeEntity element1 = swapElement(source, cache);
		BpmnNodeEntity element2 = swapElement(target, cache);

		flow.setSourceRef(element1);
		flow.setTargetRef(element2);

        Assert.notNull(element1, "could not find element " + source);
        Assert.notNull(element2, "could not find element " + target);

		((OutgoingFlowAware) element1).addOutgoingFlow(flow);
		((IncomingFlowAware) element2).addIncomingFlow(flow);

		template.save(flow);
	}

	@Override
	public void visitLinkFlow(LinkFlow flow) {
		// do nothing
	}

	@Override
	public void visitAssociation(Association association) {
		BpmnNodeEntity source = association.getSourceRef();
		BpmnNodeEntity target = association.getTargetRef();

		BpmnNodeEntity element1 = swapElement(source, cache);
		BpmnNodeEntity element2 = swapElement(target, cache);

		association.setSourceRef(element1);
		association.setTargetRef(element2);

		Assert.notNull(element1, "could not find element " + source);
		Assert.notNull(element2, "could not find element " + target);

		((OutgoingFlowAware) element1).addOutgoingFlow(association);
		((IncomingFlowAware) element2).addIncomingFlow(association);

		template.save(association);
	}

}
