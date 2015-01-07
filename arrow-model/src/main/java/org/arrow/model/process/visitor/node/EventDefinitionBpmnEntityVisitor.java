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

package org.arrow.model.process.visitor.node;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.util.Assert;
import org.arrow.model.definition.escalation.EscalationEventDefinition;
import org.arrow.model.event.boundary.BoundaryEvent;
import org.arrow.model.event.boundary.impl.EscalationBoundaryEvent;
import org.arrow.model.event.endevent.EndEvent;
import org.arrow.model.event.endevent.impl.EscalationEndEvent;
import org.arrow.model.event.intermediate.catching.IntermediateCatchEvent;
import org.arrow.model.event.intermediate.throwing.IntermediateThrowEvent;
import org.arrow.model.event.intermediate.throwing.impl.EscalationIntermediateThrowEvent;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.model.gateway.Gateway;
import org.arrow.model.process.SubProcess;
import org.arrow.model.process.event.BpmnEventDefinitionEntity;
import org.arrow.model.process.event.Escalation;
import org.arrow.model.task.Task;
import org.arrow.model.visitor.BpmnNodeEntityVisitor;

/**
 * {@link BpmnNodeEntityVisitor} implementation used to cache the visited BPMN
 * node entities so that other components could use the cache map to gain access
 * to the stored BPMN elements without fetching it from the database.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class EventDefinitionBpmnEntityVisitor implements BpmnNodeEntityVisitor {

	private final Neo4jTemplate template;
	private final Map<String, BpmnEventDefinitionEntity> defCache;

	public EventDefinitionBpmnEntityVisitor(ApplicationContext context,
			Map<String, BpmnEventDefinitionEntity> defCache) {
		this.defCache = defCache;
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

		// prepare the EscalationEndEvent entity
		if (endEvent instanceof EscalationEndEvent) {
			EscalationEndEvent eee = (EscalationEndEvent) endEvent;
			EscalationEventDefinition def = eee.getEscalationEventDefinition();
			
			String id = def.getEscalationRef().getId();
			def.setEscalationRef((Escalation) defCache.get(id));
			
			template.save(def);
			template.save(eee);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitIntermediateThrowEvent(IntermediateThrowEvent ite) {
		
		// prepare the EscalationEndEvent entity
		if (ite instanceof EscalationIntermediateThrowEvent) {
			EscalationIntermediateThrowEvent eee = (EscalationIntermediateThrowEvent) ite;
			EscalationEventDefinition def = eee.getEscalationEventDefinition();
			
			String id = def.getEscalationRef().getId();
            Assert.notNull(defCache.get(id));
            def.setEscalationRef((Escalation) defCache.get(id));
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
		
		// prepare the EscalationEndEvent entity
		if (be instanceof EscalationBoundaryEvent) {
			EscalationBoundaryEvent ebe = (EscalationBoundaryEvent) be;
			EscalationEventDefinition def = ebe.getEscalationEventDefinition();
			
			String id = def.getEscalationRef().getId();
			def.setEscalationRef((Escalation) defCache.get(id));

            template.save(def);
            template.save(ebe);
		}
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
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitGateway(Gateway gateway) {
		// do nothing
	}

}
