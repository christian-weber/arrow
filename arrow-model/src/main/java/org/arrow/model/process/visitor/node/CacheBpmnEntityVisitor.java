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

import java.util.HashMap;
import java.util.Map;

import org.arrow.model.BpmnNodeEntity;
import org.arrow.model.definition.link.LinkEventDefinition;
import org.arrow.model.event.boundary.BoundaryEvent;
import org.arrow.model.event.endevent.EndEvent;
import org.arrow.model.event.intermediate.catching.IntermediateCatchEvent;
import org.arrow.model.event.intermediate.catching.impl.LinkIntermediateCatchEvent;
import org.arrow.model.event.intermediate.throwing.IntermediateThrowEvent;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.model.gateway.Gateway;
import org.arrow.model.process.SubProcess;
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
public class CacheBpmnEntityVisitor implements BpmnNodeEntityVisitor {

	private final Map<String, BpmnNodeEntity> cache;

	public CacheBpmnEntityVisitor() {
		this.cache = new HashMap<>();
	}

	/**
	 * Returns the {@link BpmnNodeEntity} cache.
	 * 
	 * @return Map
	 */
	public Map<String, BpmnNodeEntity> getCache() {
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitStartEvent(StartEvent startEvent) {
		cache.put(startEvent.getId(), startEvent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitEndEvent(EndEvent endEvent) {
		cache.put(endEvent.getId(), endEvent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitIntermediateThrowEvent(IntermediateThrowEvent ite) {
		cache.put(ite.getId(), ite);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitIntermediateCatchEvent(IntermediateCatchEvent ice) {
		cache.put(ice.getId(), ice);
		
		if (ice instanceof LinkIntermediateCatchEvent) {
			LinkIntermediateCatchEvent link = (LinkIntermediateCatchEvent) ice;
			LinkEventDefinition led = link.getLinkEventDefinition();
			cache.put(led.getLinkName(), ice);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitBoundaryEvent(BoundaryEvent be) {
		cache.put(be.getId(), be);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitTask(Task task) {
		cache.put(task.getId(), task);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitSubProcess(SubProcess subProcess) {
		cache.put(subProcess.getId(), subProcess);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitGateway(Gateway gateway) {
		cache.put(gateway.getId(), gateway);
	}

}
