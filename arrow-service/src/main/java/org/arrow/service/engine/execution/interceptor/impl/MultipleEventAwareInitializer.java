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

package org.arrow.service.engine.execution.interceptor.impl;

import java.util.Set;

import akka.dispatch.Futures;
import org.aopalliance.aop.Advice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.arrow.model.BpmnNodeEntity;
import org.arrow.model.definition.EventDefinition;
import org.arrow.model.definition.conditional.ConditionalEventDefinition;
import org.arrow.model.definition.conditional.introduction.ConditionalEventHandlerIntroduction;
import org.arrow.model.definition.conditional.introduction.ConditionalEventPublisherIntroduction;
import org.arrow.model.definition.message.MessageEventDefinition;
import org.arrow.model.definition.message.introduction.MessageEventHandler;
import org.arrow.model.definition.message.introduction.MessageEventHandlerIntroduction;
import org.arrow.model.definition.message.introduction.MessageEventPublisherIntroduction;
import org.arrow.model.definition.multiple.MultipleEventAware;
import org.arrow.model.definition.signal.SignalEventDefinition;
import org.arrow.model.definition.signal.introduction.SignalEventHandler;
import org.arrow.model.definition.signal.introduction.SignalEventHandlerIntroduction;
import org.arrow.model.definition.signal.introduction.SignalEventPublisherIntroduction;
import org.arrow.model.definition.timer.TimerEventDefinition;
import org.arrow.model.definition.timer.introduction.TimerEventHandlerIntroduction;
import org.arrow.model.definition.timer.introduction.TimerEventPublisherIntroduction;
import org.arrow.util.FutureUtil;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.impl.DefaultContinueEventMessage;
import org.arrow.service.engine.execution.interceptor.ExecutionInterceptor;
import scala.concurrent.Future;

/**
 * {@link ExecutionInterceptor} implementation used to initialize
 * {@link MultipleEventAware} BPMN entities.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@Component
public class MultipleEventAwareInitializer implements ExecutionInterceptor {

	@Autowired
	private ExecutionService executionService;
	@Autowired
	private Neo4jTemplate neo4jTemplate;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BpmnNodeEntity beforeExecution(Execution execution, BpmnNodeEntity entity) {

		MultipleEventAware mea = (MultipleEventAware) entity;
		Set<EventDefinition> definitions = mea.getEventDefinitions();

		Assert.notNull(definitions);

		if (mea.isThrowing()) {
			return initializeThrowingEvent(entity, definitions);
		}
		return initializeCatchingEvent(entity, definitions);
	}

	/**
	 * Initializes a throwing multiple event.
	 *
	 * @return BpmnNodeEntity
	 */
	private BpmnNodeEntity initializeThrowingEvent(BpmnNodeEntity entity, Set<EventDefinition> definitions) {

		ProxyFactory factory = new ProxyFactory(entity);

		for (EventDefinition definition : definitions) {

			// Signal event definition
			if (definition instanceof SignalEventDefinition) {
				SignalEventDefinition def = (SignalEventDefinition) definition;
				Advice advice = new SignalEventPublisherIntroduction(def);
				factory.addAdvice(advice);
			}

			// Message event definition
			else if (definition instanceof MessageEventDefinition) {
				MessageEventDefinition def = (MessageEventDefinition) definition;
				Advice advice = new MessageEventPublisherIntroduction(def);
				factory.addAdvice(advice);
			}

			// Timer event definition
			else if (definition instanceof TimerEventDefinition) {
				TimerEventDefinition def = (TimerEventDefinition) definition;
				Advice advice = new TimerEventPublisherIntroduction(def);
				factory.addAdvice(advice);
			}

			// Conditional event definition
			else if (definition instanceof ConditionalEventDefinition) {
				ConditionalEventDefinition def = (ConditionalEventDefinition) definition;
				Advice advice = new ConditionalEventPublisherIntroduction(def);
				factory.addAdvice(advice);
			}

		}
		factory.addInterface(BpmnNodeEntity.class);
		return (BpmnNodeEntity) factory.getProxy();
	}

	/**
	 * Initializes a catching multiple event.
	 *
	 * @return BpmnNodeEntity
	 */
	private BpmnNodeEntity initializeCatchingEvent(BpmnNodeEntity entity, Set<EventDefinition> definitions) {

		ProxyFactory factory = new ProxyFactory(entity);

		for (EventDefinition definition : definitions) {

			// Signal event definition
			if (definition instanceof SignalEventDefinition) {
				SignalEventDefinition def = (SignalEventDefinition) definition;
				Advice advice = new SignalEventHandlerIntroduction(def);
				factory.addAdvice(advice);
			}

			// Message event definition
			else if (definition instanceof MessageEventDefinition) {
				MessageEventDefinition def = (MessageEventDefinition) definition;
				Advice advice = new MessageEventHandlerIntroduction(def);
				factory.addAdvice(advice);
			}

			// Timer event definition
			else if (definition instanceof TimerEventDefinition) {
				TimerEventDefinition def = (TimerEventDefinition) definition;
				Advice advice = new TimerEventHandlerIntroduction(def);
				factory.addAdvice(advice);
			}

			// Conditional event definition
			else if (definition instanceof ConditionalEventDefinition) {
				ConditionalEventDefinition def = (ConditionalEventDefinition) definition;
				Advice advice = new ConditionalEventHandlerIntroduction(def);
				factory.addAdvice(advice);
			}

		}
		factory.addInterface(BpmnNodeEntity.class);
		return (BpmnNodeEntity) factory.getProxy();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(Object entity) {
		return entity instanceof MultipleEventAware;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Iterable<EventMessage>> afterExecution(Execution execution, BpmnNodeEntity entity) {

		MultipleEventAware mea = (MultipleEventAware) entity;
		if (mea.getStartedBy() != null) {
			EventDefinition eventDefinition = mea.getStartedBy();

			if (eventDefinition instanceof MessageEventDefinition) {
				((MessageEventHandler) mea).handleMessageEvent(execution, executionService);
			}
			if (eventDefinition instanceof SignalEventDefinition) {
				((SignalEventHandler) mea).handleSignalEvent(execution, executionService);
			}
			return Futures.successful(FutureUtil.iterableOf(new DefaultContinueEventMessage(execution)));
		}
		return Futures.successful(FutureUtil.iterableOf());
	}

}