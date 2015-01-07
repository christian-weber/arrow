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

package org.arrow.model.definition.message.introduction;

import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.arrow.model.definition.AbstractEventHandlerIntroduction;
import org.arrow.model.definition.message.MessageEventDefinition;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.EventMessage;
import scala.concurrent.Future;

/**
 * {@link DelegatingIntroductionInterceptor} implementation used for
 * {@link MessageEventHandler} mixin introduction.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public class MessageEventHandlerIntroduction extends
		AbstractEventHandlerIntroduction implements MessageEventHandler {

	private final MessageEventDefinition eventDefinition;

	public MessageEventHandlerIntroduction(
			MessageEventDefinition eventDefinition) {
		this.eventDefinition = eventDefinition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Iterable<EventMessage>> handleMessageEvent(Execution execution, ExecutionService service) {
		return handleEvent(execution, service, eventDefinition);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MessageEventDefinition getMessageEventDefinition() {
		return eventDefinition;
	}

}
