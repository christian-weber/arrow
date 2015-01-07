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

package org.arrow.runtime.execution.service;

import akka.actor.Cancellable;
import org.arrow.runtime.api.gateway.TransitionEvaluation;
import org.arrow.runtime.api.query.ExecutionQuery;
import org.arrow.runtime.api.task.JavaDelegate;
import org.arrow.runtime.definition.RelationDef;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.message.EventMessage;
import org.springframework.scheduling.Trigger;
import scala.concurrent.Future;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Execution service definition class used by the process engine to access
 * runtime service calls during process executions.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public interface ExecutionService {

	/**
	 * Signals the process engine to trigger signal based BPMN elements e.g. a
	 * signal start event, signal intermediate event or signal boundary event.
	 * 
	 * @param signalRef the signal reference
	 * @return Future
	 */
	Future<Iterable<EventMessage>> signal(String signalRef);

	/**
	 * Signals the process engine to trigger signal based BPMN elements e.g. a
	 * signal start event, signal intermediate event or signal boundary event
	 * with the given variables.
	 * 
	 * @param signalRef the signal reference
	 * @param variables the variables
	 * @return Future
	 */
	Future<Iterable<EventMessage>> signal(String signalRef,
			Map<String, Object> variables);

	/**
	 * Signals the process engine to trigger message based BPMN elements e.g. a
	 * message start event, message intermediate event or message boundary
	 * event. The process engine starts/continues the process.
	 * 
	 * @param message the message
	 * @param execution the current execution
	 * @return Future
	 */
	Future<EventMessage> publishMessageEvent(String message,
			Execution execution);

	/**
	 * Signals the process engine to trigger message based BPMN elements e.g. a
	 * message start event, message intermediate event or message boundary
	 * event. The process engine starts/continues the process.
	 * 
	 * @param message the message
	 * @return Future
	 */
    Future<Iterable<EventMessage>> publishMessageEvent(String message);

	/**
	 * Signals the process engine to trigger message based BPMN elements e.g. a
	 * message start event, message intermediate event or message boundary
	 * event. The process engine starts/continues the process with the given
	 * variables.
	 * 
	 * @param message the message
	 * @param execution the current execution
	 * @param variables the variables
	 * @return Future
	 */
	Future<EventMessage> publishMessageEvent(String message,
			Execution execution, Map<String, Object> variables);

	/**
	 * Signals the process engine to notify the upper process in form of a
	 * escalation notification.
	 * 
	 * @param escalationCode the escalation code
	 * @param execution the current execution
	 * @return Future
	 */
	Future<Iterable<EventMessage>> publishEscalationEvent(String escalationCode,
			Execution execution);

    /**
     * Signals the process engine to notify the upper process in form of a
     * escalation notification.
     *
     * @param execution the current execution
     * @return Future
     */
    Future<EventMessage> publishCancelEvent(Execution execution);

	/**
	 * Returns a {@link JavaDelegate} instance by the given name.
	 * 
	 * @param beanName the bean name
	 * @return JavaDelegate
	 */
	JavaDelegate getJavaDelegateByName(String beanName);

	/**
	 * Returns a {@link JavaDelegate} instance by the given class.
	 * 
	 * @param className the java delegate class name
	 * @return JavaDelegate
	 */
	JavaDelegate getJavaDelegateByClassName(String className);

	/**
	 * Evaluates the given spring EL expression.
	 * 
	 * @param expression the SpEL expression
	 */
	void evaluateExpression(String expression);

	/**
	 * Returns an {@link ExecutionQuery} instance.
	 * 
	 * @return ExecutionQuery
	 */
	ExecutionQuery executionQuery();

	/**
	 * Converts the given entity object to a instance of the given type.
	 * 
	 * @param entity
	 * @param targetType
	 * @return T
	 */
	<T> T swapEntity(Object entity, Class<T> targetType);

	/**
	 * Saves the given entity.
	 * 
	 * @param entity
	 */
	void saveEntity(Object entity);

	<T> T fetchEntity(T entity);

	/**
	 * Indicates if the given entity has a persistent state.
	 * 
	 * @param entity
	 * @return boolean
	 */
	boolean hasPersistentState(Object entity);

	/**
	 * Signals the process engine to publish an error event in order to trigger
	 * error boundary events. The process engine continues the process.
	 * 
	 * @param execution the current execution
	 * @param ex the thrown exception
	 * @return Future
	 */
	Future<EventMessage> publishErrorEvent(Execution execution,
			Exception ex);

	/**
	 * Returns the spring bean of the given type.
	 * 
	 * @param cls
	 * @return T
	 */
	<T> T getBean(Class<T> cls);

	/**
	 * Returns the spring bean of the given type and name.
	 * 
	 * @param beanName the bean name
	 * @param cls the class type
	 * @return T
	 */
	<T> T getBean(String beanName, Class<T> cls);

	TransitionEvaluation getComplexGatewayTransitionEvaluationByClassName(
			String className);

	Execution getExecutionByEscalationCode(String escalationCode, String piId);

    Set<Execution> findFollowingByState(Execution execution, State state);

    Set<Execution> findExecutionsById(Collection<String> ids);
	
	/**
	 * Signals the process engine that a BPMN timer event occured.
	 * 
	 * @param execution
	 * @return Future
	 */
	Future<EventMessage> publishTimerEvent(Execution execution);
	
	/**
	 * Schedules the given {@link Runnable} instance triggered by the given
	 * {@link Trigger}. The method returns a
	 * {@link Cancellable} instance so that the scheduled task could be
	 * interrupted after this method call.
	 * 
	 * @param trigger
	 * @param runnable
	 * @return Cancellable
	 */
	Cancellable schedule(Trigger trigger, Runnable runnable);

    ExecutionDataService data();

    ExecutionUserService user();

    ExecutionRuleService rule();

    ExecutionScriptService script();

	ExecutionAdHocService adhoc();

    void enableFlow(Execution execution, RelationDef flow);

}
