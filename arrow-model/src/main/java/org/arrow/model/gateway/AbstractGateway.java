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

package org.arrow.model.gateway;

import akka.dispatch.Futures;
import org.arrow.model.AbstractBpmnNodeEntity;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.model.transition.Flow;
import org.arrow.model.transition.impl.SequenceFlow;
import org.arrow.model.visitor.BpmnNodeEntityVisitor;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.listener.ExecutionListener;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.infrastructure.TokenEventMessage;
import org.arrow.util.FutureUtil;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedToVia;
import org.springframework.util.Assert;
import scala.concurrent.Future;

import java.util.*;
import java.util.function.Predicate;

import static org.arrow.runtime.message.infrastructure.TokenEventMessage.TokenAction.PRODUCE;

/**
 * Abstract {@link StartEvent} implementation.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
public abstract class AbstractGateway extends AbstractBpmnNodeEntity implements
		Gateway {

	@Fetch
	@RelatedToVia(type = "SEQUENCE_FLOW", direction = Direction.OUTGOING)
	private Set<SequenceFlow> outgoingFlows;

	@Fetch
	@RelatedToVia(type = "SEQUENCE_FLOW", direction = Direction.INCOMING)
	private Set<SequenceFlow> incomingFlows;

	private String defaultFlow;

	public Set<Flow> getOutgoingFlows() {
		if (outgoingFlows == null) {
			return Collections.emptySet();
		}
		return new HashSet<>(outgoingFlows);
	}

	public void setOutgoingFlows(Set<SequenceFlow> outgoingFlows) {
		this.outgoingFlows = outgoingFlows;
	}

	public Set<SequenceFlow> getIncomingFlows() {
		return incomingFlows;
	}

	public void setIncomingFlows(Set<SequenceFlow> incomingFlows) {
		this.incomingFlows = incomingFlows;
	}

	@Override
	public Future<Iterable<EventMessage>> executeNode(Execution execution, ExecutionService service) {

		Assert.notNull(getIncomingFlows(), "no incoming flows detected");
		Assert.notNull(getOutgoingFlows(), "no outgoing flows detected");

		// handle JOIN
        // handle single incoming flow
        JoinResult result;
        if (getIncomingFlows().size() == 1) {
            result = new JoinResult(true);
        }
        else {
            result = join(execution, service);
        }

		// handle FORK
		if (result.isJoined()) {

            // if the gateway has one outgoing flow
            // fork and continue without producing a token
            if (getOutgoingFlows().size() == 1) {
                Flow flow = getOutgoingFlows().iterator().next();
                service.enableFlow(execution, flow);

                execution.setState(State.SUCCESS);
                finish(execution, service);
            } else {
                result.addAllMessages(fork(execution, service));
            }

            // produce tokens when forking
            for (int i = 1; i < execution.getEnabledFlowIdsContainer().size(); i++) {
                result.addMessage(new TokenEventMessage(execution, PRODUCE));
            }
		}

        return Futures.successful(iterableOf(result.getMessages()));
    }

    @SuppressWarnings("unused")
	public abstract JoinResult join(Execution execution, ExecutionService service);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Iterable<EventMessage>> finish(Execution execution, ExecutionService service) {

		// handle default flow if necessary
		if (execution.getEnabledFlowIdsContainer().isEmpty()) {
			handleDefaultFlow(execution);
		}

        // avoid to call super finish method
        for (ExecutionListener listener : getExecutionListeners()) {
            listener.onFinish(execution, service);
        }
        return FutureUtil.result();
	}

	public abstract List<EventMessage> fork(Execution execution, ExecutionService service);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addOutgoingFlow(SequenceFlow flow) {
		if (outgoingFlows == null) {
			setOutgoingFlows(new HashSet<>());
		}
		outgoingFlows.add(flow);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addIncomingFlow(SequenceFlow flow) {
		if (incomingFlows == null) {
			setIncomingFlows(new HashSet<>());
		}
		incomingFlows.add(flow);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(BpmnNodeEntityVisitor visitor) {
		visitor.visitGateway(this);
	}

    protected class JoinResult {

        private final boolean joined;
        private final List<EventMessage> messages = new ArrayList<>();

        public JoinResult(boolean joined) {
            this.joined = joined;
        }

		public JoinResult(boolean joined, EventMessage...messages) {
			this.joined = joined;
			Collections.addAll(this.messages, messages);
		}

        public void addMessage(EventMessage eventMessage) {
            this.messages.add(eventMessage);
        }

        public void addAllMessages(Collection<EventMessage> eventMessages) {
            this.messages.addAll(eventMessages);
        }

        public boolean isJoined() {
            return joined;
        }

        public List<EventMessage> getMessages() {
            return messages;
        }
    }

	public void setDefaultFlow(String defaultFlow) {
		this.defaultFlow = defaultFlow;
	}

	/**
	 * Enables the default flow.
	 *
	 * @param execution the execution instance
	 */
	protected void handleDefaultFlow(Execution execution) {
		Predicate<? super Flow> p = flow -> defaultFlow != null && flow.getId().equals(defaultFlow);
		getOutgoingFlows().stream().filter(p).forEach(flow -> flow.enableRelation(execution));
	}

}
