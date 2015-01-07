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

package org.arrow.model.gateway.impl;

import org.arrow.model.gateway.AbstractGateway;
import org.arrow.runtime.api.gateway.TransitionEvaluation;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.EventMessage;
import org.springframework.data.neo4j.annotation.NodeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * BPMN 2.0 complex gateway implementation.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
public class ComplexGateway extends AbstractGateway {

	/** The bean name. */
	private String beanName;

	/** The class name. */
	private String className;

	/**
	 * Gets the bean name.
	 * 
	 * @return the bean name
	 */
	public String getBeanName() {
		return beanName;
	}

	/**
	 * Sets the bean name.
	 * 
	 * @param beanName
	 *            the new bean name
	 */
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	/**
	 * Sets the class name.
	 * 
	 * @param className
	 *            the new class name
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EventMessage> fork(Execution execution, ExecutionService service) {

        // invoke a class
		if (className != null && !className.isEmpty()) {
			TransitionEvaluation cgte;
            cgte = service.getComplexGatewayTransitionEvaluationByClassName(className);
			cgte.fork(execution);
		}

        // invoke a spring bean
		else if (beanName != null && !beanName.isEmpty()) {
			TransitionEvaluation cgte;
            cgte = service.getBean(beanName, TransitionEvaluation.class);
			cgte.fork(execution);
		}

		// enable the outgoing flows for the execution
        else {
			getOutgoingFlows().stream().forEach(flow -> flow.enableRelation(execution));
        }

        execution.setState(State.SUCCESS);
        finish(execution, service);

        return new ArrayList<>();
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public JoinResult join(Execution execution, ExecutionService service) {

        // invoke a class
        if (className != null && !className.isEmpty()) {
            TransitionEvaluation cgte;
            cgte = service.getComplexGatewayTransitionEvaluationByClassName(className);
            return new JoinResult(cgte.join(execution));
        }

        // invoke a spring bean
        else if (beanName != null && !beanName.isEmpty()) {
            TransitionEvaluation cgte;
            cgte = service.getBean(beanName, TransitionEvaluation.class);
            return new JoinResult(cgte.join(execution));
        }

        return new JoinResult(false);
    }
}
