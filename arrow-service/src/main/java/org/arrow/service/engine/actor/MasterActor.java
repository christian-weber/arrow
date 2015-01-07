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

import akka.actor.ActorRef;
import akka.dispatch.Futures;
import akka.dispatch.OnSuccess;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.arrow.data.neo4j.store.ProcessInstanceStore;
import org.arrow.model.BpmnNodeEntity;
import org.arrow.model.process.AdHocSubProcess;
import org.arrow.model.process.SubProcess;
import org.arrow.model.process.SubProcessEntity;
import org.arrow.model.task.impl.CallActivityTask;
import org.arrow.runtime.api.BpmnNodeEntitySpecification;
import org.arrow.runtime.api.StartEventSpecification;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ProcessInstance;
import org.arrow.runtime.execution.State;
import org.arrow.runtime.execution.service.ExecutionService;
import org.arrow.runtime.execution.service.data.ExecutionRepository;
import org.arrow.runtime.message.ContinueEventMessage;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.ExecuteEventMessage;
import org.arrow.runtime.message.FinishEventMessage;
import org.arrow.runtime.message.impl.*;
import org.arrow.runtime.message.infrastructure.CallableEventMessage;
import org.arrow.runtime.message.infrastructure.PersistEventMessage;
import org.arrow.runtime.support.EngineSynchronizationManagerCallableDecorator;
import org.arrow.service.engine.actor.template.MasterTemplate;
import org.arrow.service.engine.concurrent.SynchronisationCallable;
import org.arrow.service.engine.concurrent.dispatch.onfailure.PrintStacktraceOnFailure;
import org.arrow.service.engine.concurrent.dispatch.onsuccess.NotifyOnSuccess;
import org.arrow.service.engine.concurrent.dispatch.onsuccess.PublishEventMessagesOnSuccess;
import org.arrow.service.engine.concurrent.dispatch.onsuccess.TellActorForEachMessage;
import org.arrow.service.engine.concurrent.dispatch.recover.Recovers;
import org.arrow.util.FutureUtil;
import scala.concurrent.ExecutionContext;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.Future;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class MasterActor extends MasterTemplate {

    private ActorRef nodeWorker;
    private ProcessInstance monitor;

    /**
     * The super actor in the actor hierarchy. Represents the actor instance
     * in a process/sub-process constellation.
     */
    private ActorRef parentActor;

    public MasterActor(ApplicationContext context, Map<String, Object> scopeMap) {
        super(context, scopeMap);
    }

    public void setNodeWorker(ActorRef nodeWorker) {
        this.nodeWorker = nodeWorker;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveStart(StartEventMessage start) {

        getContext().watch(nodeWorker);

        this.parentActor = getSender();
        this.monitor = start.getProcessInstance();

        if (start.getEntity() != null) {
            nodeWorker.tell(start, getSelf());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveContinue(ContinueEventMessage msg) {
        final BpmnNodeEntitySpecification entity = msg.getEntity();

        fetchEntityIfNecessary(entity);
        nodeWorker.tell(msg, getSelf());
    }

    @Override
    protected void onReceiveExecute(ExecuteEventMessage executeEventMessage) {
        final BpmnNodeEntitySpecification entity = executeEventMessage.getEntity();

        fetchEntityIfNecessary(entity);
        nodeWorker.tell(executeEventMessage, getSelf());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveAbort(AbortEventMessage abort) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void onReceiveEnd(final EndEventMessage end) {

        if (!getTokenRegistry().hasToken()) {
            ExecutionContext ec = getContext().system().dispatcher();

            Future<Iterable<Object>> future = getStoredFutures();
            monitor.setState(State.SUCCESS);

            future.onSuccess(new NotifyOnSuccess(monitor), ec);
            future.onFailure(new PrintStacktraceOnFailure(monitor), ec);

            final ProcessInstance pi = end.getProcessInstance();

            final ExecutionService executionService = getExecutionService();

            future.onSuccess(new OnSuccess<Iterable<Object>>() {

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void onSuccess(Iterable<Object> objects) throws Throwable {

                    final ExecutionRepository repo = executionService.data().execution();
                    BpmnNodeEntity subProcess = (BpmnNodeEntity) firstNonNull(pi.getProcessTrigger(), pi.getProcess());

                    ProcessInstance parentPi = pi.getParentProcessInstance();
                    if (parentPi == null) {
                        // stop the actor
                        getContext().stop(getSelf());
                        return;
                    }

                    Execution execution = repo.findByEntityId(subProcess.getNodeId(), parentPi.getNodeId());
                    if (execution == null) {
                        getSelf().tell(end, getSelf());
                        return;
                    }

                    Object msg = new EndSubProcessEventMessage(execution);
                    parentActor.tell(msg, getSelf());
                }

            }, ec);

        } else {
            Execution execution = end.getExecution();
            SynchronisationCallable callable = new SynchronisationCallable(end.getExecution(), getExecutionService());

            EventMessage message = new PersistEventMessage(execution, new CallableEventMessage(execution, callable));
            getSelf().tell(message, getSelf());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveSignalEvent(final SignalEventMessage signalEvent) {
        nodeWorker.tell(signalEvent, getSelf());

    }

    private Callable<Iterable<EventMessage>> decorateAll(Callable<Iterable<EventMessage>> callable) {
        return new EngineSynchronizationManagerCallableDecorator<>(getScopeMap(), callable, getSelf());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveMessageEvent(final MessageEventMessage messageEvent) {
        nodeWorker.tell(messageEvent, getSelf());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveTimerEvent(final TimerEventMessage timerEvent) {
        nodeWorker.tell(timerEvent, getSelf());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveConditionalEvent(final ConditionEventMessage event) {
        nodeWorker.tell(event, getSelf());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveErrorEvent(final ErrorEventMessage errorEvent) {
        nodeWorker.tell(errorEvent, getSelf());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveStartSubProcess(StartSubProcessEventMessage msg) {

        final ProcessInstance pi = msg.getProcessInstance();
        final StartEventSpecification event = msg.getStartEvent();
        final SubProcessEntity sub = (SubProcessEntity) msg.getEntity();

        // store a sub process instance
        ProcessInstanceStore store = getBean(ProcessInstanceStore.class);

        if (sub instanceof AdHocSubProcess) {
            ProcessInstance subPi = store.store((AdHocSubProcess) sub, pi);

            StartEventMessage message = new StartEventMessage(subPi);
            ExecutionContextExecutor ec = getContext().dispatcher();

            Future<Iterable<EventMessage>> future = FutureUtil.result(message);
            future.onSuccess(new PublishEventMessagesOnSuccess(getApplicationContext(), getSelf()), ec);
        } else {
            ProcessInstance subPi = store.store(sub, event, pi);

            StartEventMessage message = new StartEventMessage(event, subPi);

            ExecutionContextExecutor ec = getContext().dispatcher();

            Future<Iterable<EventMessage>> future = FutureUtil.result(message);
            future.onSuccess(new PublishEventMessagesOnSuccess(getApplicationContext(), getSelf()), ec);
        }



    }

    @Override
    protected void onReceiveFinishMessage(FinishEventMessage message) {
        final BpmnNodeEntitySpecification entity = message.getEntity();

        fetchEntityIfNecessary(entity);
        nodeWorker.tell(message, getSelf());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveEndSubProcess(final EndSubProcessEventMessage msg) {

        final BpmnNodeEntity sub = (BpmnNodeEntity) msg.getEntity();
        final Execution execution = msg.getExecution();

        if (isCallActivityOrStandardSubProcess(sub)) {
            FinishEventMessage message = new DefaultFinishEventMessage(execution);
            getSelf().tell(message, getSelf());

            ContinueEventMessage node = new DefaultContinueEventMessage(execution);
            getSelf().tell(node, getSelf());
        } else {
            FinishEventMessage message = new DefaultFinishEventMessage(execution);
            getSelf().tell(message, getSelf());
        }

    }

    private boolean isCallActivityOrStandardSubProcess(BpmnNodeEntity entity) {

        if (entity instanceof CallActivityTask) {
            return true;
        }
        SubProcess subProcess = (SubProcess) entity;
        return !subProcess.isForCompensation() && !subProcess.isTriggeredByEvent();
    }

    /**
     * Handles the event when an event based sub process is triggered. This event handler
     * sets the execution state to 'WAITING' due to the specification that end events should
     * stay in state 'WAITING' as long as event based sub processes are running.
     *
     * @param message the sub process event message
     */
    @Override
    protected void onReceiveSubProcessEventMessage(EventSubProcessEventMessage message) {
        Execution execution = message.getExecution();
        execution.setState(State.WAITING);

        getExecutionService().saveEntity(execution);

        nodeWorker.tell(message, getSelf());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveEscalationEvent(final EscalationEventMessage escalationEvent) {
        nodeWorker.tell(escalationEvent, getSelf());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveCancelEvent(CancelEventMessage cancelEvent) {

        final ExecutionContextExecutor ec = getContext().dispatcher();

        becomeBehavior(new CompensateBehavior());

        // prepare the callable instance
        // *****************************
        Callable<Iterable<EventMessage>> callable = () -> {
            ExecutionRepository repository = getBean(ExecutionRepository.class);

            String piId = cancelEvent.getProcessInstance().getId();
            Set<Execution> executions = repository.findCompensateBoundaryEventExecutions(piId);

            return executions.stream().map(DefaultFinishEventMessage::new).collect(Collectors.toList());
        };

        Future<Iterable<EventMessage>> future = Futures.future(decorateAll(callable), ec);
        future.recover(Recovers.logAndReturnNull(), ec);
        future.onSuccess(new TellActorForEachMessage(getSelf(), nodeWorker), ec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveCompensateEvent(CompensateEventMessage escalationEvent) {

        final ProcessInstance pi = escalationEvent.getProcessInstance();

        final ExecutionService service = getExecutionService();
        Set<Execution> executions = service.data().execution().findUnsuccessfulCompensateTaskExecutions(pi.getNodeId());

        if (!CollectionUtils.contains(executions.iterator(), null)) {
            unbecomeBehavior();
            Execution endEventExecution = service.data().execution().findCancelEndEventExecutionInStateWait(pi.getId());

            getSelf().tell(new ContinuingFinishEventMessage(endEventExecution), getSelf());
        }

    }

}
