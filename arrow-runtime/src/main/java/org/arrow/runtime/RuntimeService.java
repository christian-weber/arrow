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

package org.arrow.runtime;

import akka.actor.ActorRef;
import org.arrow.runtime.api.StartEventSpecification;
import org.arrow.runtime.api.TimerStartEventSpecification;
import org.arrow.runtime.api.event.BusinessCondition;
import org.arrow.runtime.api.event.BusinessCondition.BusinessConditionContext;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ProcessInstance;
import scala.concurrent.Future;

import java.util.Map;

/**
 * Defines BPMN runtime process service mechanisms which can be used by the
 * process engine to handle process instance executions.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public interface RuntimeService {

	/**
	 * Starts the BPMN process by the given id.
	 * 
	 * @param id the process id
	 * @return ProcessInstance
	 */
	Future<ProcessInstance> startProcessById(String id);

	/**
	 * Starts the BPMN process by the given id and the given variables.
	 * 
	 * @param id the process id
	 * @return ProcessInstance
	 */
	Future<ProcessInstance> startProcessById(String id, Map<String, Object> variables);

    /**
	 * Starts the BPMN process by a signal reference.
	 * 
	 * @param signalRef the signal reference
	 * @return ProcessInstance
	 */
	Future<Iterable<ProcessInstance>> startProcessBySignal(String signalRef);

	/**
	 * Starts the BPMN process by a signal reference with the given variables.
	 * 
	 * @param signalRef the signal reference
	 * @param variables the variables map
	 * @return ProcessInstance
	 */
	Future<Iterable<ProcessInstance>> startProcessBySignal(String signalRef,
			Map<String, Object> variables);

	/**
	 * Starts the BPMN process by a message reference.
	 * 
	 * @param messageRef the message reference
	 * @return ProcessInstance
	 */
	Future<ProcessInstance> startProcessByMessage(String messageRef);

	/**
	 * Starts the BPMN process by a message reference with the given variables.
	 * 
	 * @param variables the variables map
	 * @return ProcessInstance
	 */
	Future<ProcessInstance> startProcessByMessage(String messageRef,
			Map<String, Object> variables);

	/**
	 * Signals the process engine to trigger signal based BPMN elements e.g. a
	 * signal start event, signal intermediate event or signal boundary event.
	 * 
	 * @param execution the execution instance
	 * @return Future
	 */
	Future<Iterable<EventMessage>> signal(Execution execution);

	/**
	 * Signals the process engine to trigger signal based BPMN elements e.g. a
	 * signal start event, signal intermediate event or signal boundary event.
	 * The process engine starts/continues the process with the given variables.
	 * 
	 * @param execution the execution instance
	 * @param variables the variables map
	 * @return Future
	 */
	Future<Iterable<EventMessage>> signal(Execution execution,
			Map<String, Object> variables);

	/**
	 * Signals the process engine to trigger signal based BPMN elements e.g. a
	 * signal start event, signal intermediate event or signal boundary event.
	 * The process engine starts/continues the process.
	 * 
	 * @param signalRef the signal reference
	 * @return Future
	 */
	Future<Iterable<EventMessage>> signal(String signalRef);

	/**
	 * Signals the process engine to trigger signal based BPMN elements e.g. a
	 * signal start event, signal intermediate event or signal boundary event.
	 * The process engine starts/continues the process with the given variables.
	 * 
	 * @param signalRef the signal reference
	 * @param variables the variables map
	 * @return Future
	 */
	Future<Iterable<EventMessage>> signal(String signalRef, Map<String, Object> variables);

	/**
	 * Signals the process engine to trigger signal based BPMN elements e.g. a
	 * signal start event, signal intermediate event or signal boundary event.
	 * The process engine starts/continues the process.
	 * 
	 * @param signalRef the signal reference
	 * @param execution the execution instance
	 * @return Future
	 */
	Future<Iterable<EventMessage>> signal(String signalRef, Execution execution);

	/**
	 * Signals the process engine to trigger signal based BPMN elements e.g. a
	 * signal start event, signal intermediate event or signal boundary event.
	 * The process engine starts/continues the process with the given variables.
	 * 
	 * @param signalRef the signal reference
	 * @param execution the execution instance
	 * @param variables the variables map
	 * @return Future
	 */
	Future<Iterable<EventMessage>> signal(String signalRef, Execution execution,
			Map<String, Object> variables);

	/**
	 * Signals the process engine to trigger message based BPMN elements e.g. a
	 * message start event, message intermediate event or message boundary
	 * event. The process engine starts/continues the process.
	 * 
	 * @param message the message reference
	 * @param execution the execution instance
	 * @return Future
	 */
	Future<EventMessage> message(String message, Execution execution);

	/**
	 * Signals the process engine to trigger message based BPMN elements e.g. a
	 * message start event, message intermediate event or message boundary
	 * event. The process engine starts/continues the process with the given
	 * variables.
	 * 
	 * @param execution the execution instance
	 * @param variables the variables map
	 * @return Future
	 */
	Future<EventMessage> message(String message, Execution execution,
			Map<String, Object> variables);

	/**
	 * Publishes a message event with the given message.
	 *
	 * @param message the message to publish
	 * @return Future
	 */
    Future<Iterable<EventMessage>> message(String message);

	/**
	 * Registers a {@link TimerStartEventSpecification} for scheduling.
	 * 
	 * @param timerStartEvent the timer start event instance
	 */
	void schedule(TimerStartEventSpecification timerStartEvent);

	/**
	 * Starts the process with the given {@link org.arrow.runtime.api.StartEventSpecification}
	 * 
	 * @param event the start event instance
	 */
	Future<EventMessage> startProcessByStartEvent(StartEventSpecification event);

	/**
	 * Starts the process with the given {@link StartEventSpecification} and the given
	 * variables.
	 * 
	 * @param event the start event instance
	 * @param variables the variables map
	 */
	Future<EventMessage> startProcessByStartEvent(StartEventSpecification event,
			Map<String, Object> variables);

	/**
	 * Signals the process engine to publish an error event in order to trigger
	 * error boundary events. The process engine continues the process.
	 * 
	 * @param execution the execution instance
	 * @param ex the exception instance
	 * @return Future
	 */
	Future<EventMessage> publishErrorEvent(Execution execution, Exception ex);

    void publishStartEventMessage(StartEventSpecification entity, ProcessInstance pi, ActorRef sender);

    Future<Iterable<EventMessage>> publishConditionEvent(BusinessCondition condition, BusinessConditionContext context);

    void publishEventMessage(EventMessage eventMessage);

}
