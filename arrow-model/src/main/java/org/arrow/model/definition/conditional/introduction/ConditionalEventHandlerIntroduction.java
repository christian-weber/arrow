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

package org.arrow.model.definition.conditional.introduction;

import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.arrow.model.definition.conditional.ConditionalEventDefinition;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.MultipleEventExecution;
import org.arrow.runtime.execution.service.ExecutionService;

/**
 * {@link DelegatingIntroductionInterceptor} implementation used for
 * {@link ConditionalEventHandler} mixin introduction.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public class ConditionalEventHandlerIntroduction extends
		DelegatingIntroductionInterceptor implements ConditionalEventHandler {

	private final ConditionalEventDefinition eventDefinition;

	public ConditionalEventHandlerIntroduction(
			ConditionalEventDefinition eventDefinition) {
		this.eventDefinition = eventDefinition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleConditionalEvent(Execution execution,
			ExecutionService service) {

		Class<MultipleEventExecution> targetType = MultipleEventExecution.class;
		MultipleEventExecution mee = service.swapEntity(execution, targetType);
		mee.addReceivedEventId(eventDefinition.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConditionalEventDefinition getConditionalEventDefinition() {
		return eventDefinition;
	}

}
