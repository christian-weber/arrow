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

package org.arrow.service.engine.actor;

import akka.actor.UntypedActor;
import org.spockframework.util.Assert;
import org.springframework.context.ApplicationContext;
import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.support.EngineSynchronizationManager;
import scala.PartialFunction;
import scala.concurrent.ExecutionContext;
import scala.runtime.BoxedUnit;

import java.util.Map;

/**
 * Abstract {@link UntypedActor} class which defines the super class for all
 * worker classes. This class contains a set of general methods which can be
 * used by the specific implementation classes for message handling.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public abstract class AbstractActor extends UntypedActor {

	private final ApplicationContext context;
    private final Map<String, Object> scopeMap;

    public AbstractActor(ApplicationContext context, Map<String, Object> scopeMap) {
        Assert.notNull(context);

		this.context = context;
        this.scopeMap = scopeMap;
	}

    /**
     * Returns the scope map.
     *
     * @return Map
     */
    protected Map<String, Object> getScopeMap() {
        return scopeMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void aroundReceive(PartialFunction<Object, BoxedUnit> receive, Object msg) {
        if (msg instanceof EventMessage) {
            initEngineSynchronizationManager((EventMessage) msg);
        }
        super.aroundReceive(receive, msg);
        if (msg instanceof EventMessage) {
            unsetEngineSynchronizationManager();
        }
    }

    /**
	 * Returns the {@link ApplicationContext} instance.
	 *
	 * @return ApplicationContext
	 */
	protected ApplicationContext getApplicationContext() {
		return context;
	}

	/**
	 * Returns a bean instance of the given type.
	 *
	 * @param type the type of the container managed bean
	 * @return T
	 */
	protected <T> T getBean(Class<T> type) {
		return context.getBean(type);
	}

	/**
	 * Returns the {@link ExecutionService} instance.
	 *
	 * @return ExecutionService
	 */
	protected ExecutionService getExecutionService() {
		return getBean(ExecutionService.class);
	}

    protected void initEngineSynchronizationManager(EventMessage msg) {
        EngineSynchronizationManager.setProcessScope(getScopeMap());
        EngineSynchronizationManager.setCurrentActor(getSelf());

        ProcessInstance pi = msg.getProcessInstance();
        EngineSynchronizationManager.setProcessInstanceId(pi == null ? null : pi.getId());
    }

    protected void unsetEngineSynchronizationManager() {
        EngineSynchronizationManager.setProcessScope(null);
        EngineSynchronizationManager.setCurrentActor(null);
        EngineSynchronizationManager.setProcessInstanceId(null);
    }

    protected void fetchEntityIfNecessary(BpmnNodeEntitySpecification entity) {
        if (entity.getId() == null) {
            getExecutionService().fetchEntity(entity);
        }
    }

    protected ExecutionContext dispatcher() {
        return getContext().dispatcher();
    }

}
