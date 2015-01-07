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

package org.arrow.service.listener;

import java.util.Map;

import org.arrow.runtime.api.StartEventSpecification;
import org.arrow.runtime.execution.service.data.StartEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.arrow.model.event.startevent.impl.ConditionalStartEvent;
import org.arrow.runtime.message.event.ConditionApplicationEvent;
import org.arrow.runtime.RuntimeService;

/**
 * {@link ApplicationListener} used to start {@link ConditionalStartEvent}
 * instances.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@Component
public class ConditionStartEventApplicationListener implements
		ApplicationListener<ConditionApplicationEvent> {

	@Autowired
	private ApplicationContext context;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void onApplicationEvent(ConditionApplicationEvent event) {


        ConditionApplicationEvent.ConditionHolder holder = event.getSource();

		// due to cycle reference
		StartEventRepository startEventRepository = context.getBean(StartEventRepository.class);
		RuntimeService runtimeService = context.getBean(RuntimeService.class);

        Iterable<? extends StartEventSpecification> events;
		events = startEventRepository.findAllConditionalStartEvents(holder.getBeanName());

		Map<String, Object> variables = holder.getContext().getVariables();
		for (StartEventSpecification startEvent : events) {
			runtimeService.startProcessByStartEvent(startEvent, variables);
		}
	}

}
