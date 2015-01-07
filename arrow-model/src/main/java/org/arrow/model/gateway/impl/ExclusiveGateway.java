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

import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.arrow.model.gateway.AbstractGateway;
import org.arrow.model.transition.Flow;
import org.arrow.model.transition.impl.ConditionExpression;
import org.arrow.model.transition.impl.SequenceFlow;
import org.arrow.runtime.execution.service.ExecutionDataService;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.infrastructure.TokenEventMessage;

import java.util.Arrays;
import java.util.List;

import static org.springframework.util.Assert.notNull;
import static org.arrow.runtime.message.infrastructure.TokenEventMessage.TokenAction.CONSUME;

@NodeEntity
public class ExclusiveGateway extends AbstractGateway {

	@Override
	public List<EventMessage> fork(Execution execution, ExecutionService service) {

		ExpressionParser parser = new SpelExpressionParser();

		for (Flow flow : getOutgoingFlows()) {

			// handle multiple outgoing flows
			ConditionExpression ce = ((SequenceFlow)flow).getConditionExpression();

			if (ce == null) {
				continue;
			}

			StandardEvaluationContext context = new StandardEvaluationContext();
			context.setVariables(execution.getVariables());

			Expression expr = parser.parseExpression(ce.getCondition());
			Boolean result = expr.getValue(context, Boolean.class);

			if ((result != null) && (result)) {
                flow.enableRelation(execution);
                break;
			}
		}
		
		// mark gateway as finished
		execution.setState(State.SUCCESS);
        finish(execution, service);

        return Arrays.asList();
	}

	public JoinResult join(Execution execution, ExecutionService service) {
		return new JoinResult(true);
	}

}
