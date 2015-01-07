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

import java.util.HashMap;
import java.util.Map;

import org.arrow.model.process.event.BpmnEventDefinitionEntity;
import org.arrow.model.process.event.Escalation;
import org.arrow.model.visitor.BpmnEventDefinitionEntityVisitor;

/**
 * {@link BpmnEventDefinitionEntityVisitor} implementation used to cache
 * {@link BpmnEventDefinitionEntity} instances.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class CacheBpmnEventDefinitionEntityVisitor implements
		BpmnEventDefinitionEntityVisitor {

	private final Map<String, BpmnEventDefinitionEntity> cache;

	public CacheBpmnEventDefinitionEntityVisitor() {
		this.cache = new HashMap<>();
	}

	/**
	 * Returns the cache map.
	 * 
	 * @return Map
	 */
	public Map<String, BpmnEventDefinitionEntity> getCache() {
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitEscalation(Escalation escalation) {
		cache.put(escalation.getId(), escalation);
	}

}
