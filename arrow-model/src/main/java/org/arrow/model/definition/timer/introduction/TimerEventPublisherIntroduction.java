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

package org.arrow.model.definition.timer.introduction;

import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.arrow.model.definition.timer.TimerEventDefinition;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.service.ExecutionService;

/**
 * {@link DelegatingIntroductionInterceptor} implementation used for
 * {@link TimerEventDefinition} mixin introduction.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public class TimerEventPublisherIntroduction extends
		DelegatingIntroductionInterceptor implements TimerEventPublisher {

	private final TimerEventDefinition eventDefinition;

	public TimerEventPublisherIntroduction(TimerEventDefinition eventDefinition) {
		this.eventDefinition = eventDefinition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void publishTimerEvent(Execution execution, ExecutionService service) {
		service.publishTimerEvent(execution);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TimerEventDefinition getTimerEventDefinition() {
		return eventDefinition;
	}

}
