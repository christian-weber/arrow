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

import akka.dispatch.OnSuccess;
import org.springframework.context.ApplicationContext;
import org.arrow.model.BpmnNodeEntity;
import org.arrow.model.definition.escalation.introduction.EscalationEventHandler;
import org.arrow.model.definition.message.introduction.MessageEventHandler;
import org.arrow.model.definition.signal.introduction.SignalEventHandler;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.message.EntityEventMessage;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.FinishEventMessage;
import org.arrow.runtime.message.impl.EscalationEventMessage;
import org.arrow.runtime.message.impl.MessageEventMessage;
import org.arrow.runtime.message.impl.SignalEventMessage;
import org.arrow.service.engine.actor.template.NodeActorTemplate;
import org.arrow.service.engine.concurrent.dispatch.onfailure.PrintStacktraceOnFailure;
import org.arrow.service.engine.concurrent.dispatch.onsuccess.*;
import org.arrow.service.engine.execution.interceptor.BpmnEntityInitializerAdapter;
import org.arrow.util.FutureUtil;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;

import java.util.Map;

/**
 * Actor class designed to handle {@link org.arrow.runtime.message.EventMessage} messages.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class NodeActor extends NodeActorTemplate {

    public NodeActor(ApplicationContext context, Map<String, Object> scopeMap) {
        super(context, scopeMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveExecuteMessage(EntityEventMessage msg) {
        Future<Iterable<EventMessage>> future = execute(msg);
        savePublishHandle(future, msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveContinueMessage(EntityEventMessage msg) {
        Future<Iterable<EventMessage>> future = FutureUtil.result();
        saveHandle(future, msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveFinishMessage(FinishEventMessage msg) {

        Execution execution = msg.getExecution();
        execution.setCurrentEventMessage(msg);
        Future<Iterable<EventMessage>> future = finish(msg);

        ExecutionContext ec = getContext().system().dispatcher();

        OnSuccess<Iterable<EventMessage>> os1 = saveNodeOnSuccess(ec, msg);
        OnSuccess<Iterable<EventMessage>> os2 = publishNodeOnSuccess();
        OnSuccess<Iterable<EventMessage>> os4 = msg.continueNode() ? handleNodeOnSuccess(msg) : placeholder();

        future.onSuccess(new OnSuccessComposite(os1, os2, os4), ec);
        future.onFailure(new PrintStacktraceOnFailure(), ec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveMessageEventMessage(MessageEventMessage msg) {
        MessageEventHandler handler = ((MessageEventHandler) proxy(msg));
        Future<Iterable<EventMessage>> future = handler.handleMessageEvent(msg.getExecution(), getExecutionService());
        savePublishHandle(future, msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveEscalationEventMessage(EscalationEventMessage msg) {
        EscalationEventHandler handler = ((EscalationEventHandler) proxy(msg));
        Future<Iterable<EventMessage>> future = handler.handleEscalationEvent(msg.getExecution(), getExecutionService());
        savePublishHandle(future, msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveSignalEventMessage(SignalEventMessage msg) {
        SignalEventHandler handler = ((SignalEventHandler) proxy(msg));
        Future<Iterable<EventMessage>> future = handler.handleSignalEvent(msg.getExecution(), getExecutionService());
        savePublishHandle(future, msg);
    }

    private void savePublishHandle(Future<Iterable<EventMessage>> future, EntityEventMessage msg) {
        ExecutionContext ec = getContext().system().dispatcher();
        OnSuccess<Iterable<EventMessage>> os1 = saveNodeOnSuccess(ec, msg);
        OnSuccess<Iterable<EventMessage>> os2 = publishNodeOnSuccess();
//        OnSuccess<Iterable<EventMessage>> os3 = notifyNodeOnSuccess(msg);
        OnSuccess<Iterable<EventMessage>> os4 = interruptNodeOnSuccess(msg);
        OnSuccess<Iterable<EventMessage>> os5 = handleNodeOnSuccess(msg);

        future.onSuccess(new OnSuccessComposite(os1, os2, os4, os5), ec);
        future.onFailure(new PrintStacktraceOnFailure(), ec);
    }

    private void saveHandle(Future<Iterable<EventMessage>> future, EntityEventMessage msg) {
        ExecutionContext ec = getContext().system().dispatcher();
        OnSuccess<Iterable<EventMessage>> os1 = saveNodeOnSuccess(ec, msg);
        OnSuccess<Iterable<EventMessage>> os2 = handleNodeOnSuccess(msg);

        future.onSuccess(new OnSuccessComposite(os1, os2), ec);
        future.onFailure(new PrintStacktraceOnFailure(), ec);
    }


    /**
     * Returns a {@link SaveNodeOnSuccess} instance.
     *
     * @param ec the execution context
     * @return OnSuccess
     */
    private OnSuccess<Iterable<EventMessage>> saveNodeOnSuccess(ExecutionContext ec, EntityEventMessage entity) {
        return new SaveNodeOnSuccess(getApplicationContext(), ec, getContext(), entity);
    }

    /**
     * Returns a {@link HandleNodeOnSuccess} instance.
     *
     * @return OnSuccess
     */
    private OnSuccess<Iterable<EventMessage>> handleNodeOnSuccess(EntityEventMessage msg) {
        return new HandleNodeOnSuccess(getContext(), msg);
    }

    private OnSuccess<Iterable<EventMessage>> interruptNodeOnSuccess(EntityEventMessage msg) {
        return new InterruptNodeOnSuccess(getApplicationContext(), msg);
    }


    private OnSuccess<Iterable<EventMessage>> placeholder() {
        return new OnSuccess<Iterable<EventMessage>>() {
            @Override
            public void onSuccess(Iterable<EventMessage> eventMessages) throws Throwable {
                // do nothing
            }
        };
    }

    /**
     * Returns a {@link PublishEventMessagesOnSuccess} instance.
     *
     * @return OnSuccess
     */
    private OnSuccess<Iterable<EventMessage>> publishNodeOnSuccess() {
        return new PublishEventMessagesOnSuccess(getApplicationContext(), getSender());
    }

    private Future<Iterable<EventMessage>> execute(final EntityEventMessage msg) {

            final BpmnNodeEntity entity = (BpmnNodeEntity) msg.getEntity();
            final Execution execution = msg.getExecution();

            final ExecutionService service = getExecutionService();
            final BpmnEntityInitializerAdapter initializer = getBean(BpmnEntityInitializerAdapter.class);

            fetchEntityIfNecessary(entity);

            // create a proxy if context sensitive bpmn logic is configured
            final BpmnNodeEntity proxy = initializer.beforeExecution(execution, entity);
            // execute the bpmn entity
            Future<Iterable<EventMessage>> future1 = proxy.execute(execution, service);
            Future<Iterable<EventMessage>> future2 = initializer.afterExecution(execution, proxy);
            return FutureUtil.sequenceResult(getExecutionContextExecutor(), future1, future2);
    }

    private Future<Iterable<EventMessage>> finish(final EntityEventMessage msg) {

        final BpmnNodeEntity entity = (BpmnNodeEntity) msg.getEntity();
        final Execution execution = msg.getExecution();

        final ExecutionService service = getExecutionService();
        final BpmnEntityInitializerAdapter initializer = getBean(BpmnEntityInitializerAdapter.class);

        fetchEntityIfNecessary(entity);

        final BpmnNodeEntity proxy = initializer.beforeExecution(execution, entity);
        Future<Iterable<EventMessage>> future1 = proxy.finish(execution, service);
        Future<Iterable<EventMessage>> future2 = initializer.afterExecution(execution, proxy);

        return FutureUtil.sequenceResult(getExecutionContextExecutor(), future1, future2);
    }

    private BpmnNodeEntity proxy(EntityEventMessage msg) {
        final BpmnEntityInitializerAdapter initializer = getBean(BpmnEntityInitializerAdapter.class);
        return initializer.beforeExecution(msg.getExecution(), (BpmnNodeEntity) msg.getEntity());
    }

}
