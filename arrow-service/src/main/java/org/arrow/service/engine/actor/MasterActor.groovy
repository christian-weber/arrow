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

package org.arrow.service.engine.actor

import akka.actor.ActorRef
import akka.dispatch.Futures
import akka.dispatch.OnSuccess
import groovy.transform.CompileStatic
import org.arrow.data.neo4j.store.ProcessInstanceStore
import org.arrow.model.BpmnNodeEntity
import org.arrow.model.process.AdHocSubProcess
import org.arrow.model.process.SubProcess
import org.arrow.model.process.SubProcessEntity
import org.arrow.model.task.impl.CallActivityTask
import org.arrow.runtime.execution.Execution
import org.arrow.runtime.execution.ProcessInstance
import org.arrow.runtime.execution.State
import org.arrow.runtime.execution.service.data.ExecutionRepository
import org.arrow.runtime.message.ContinueEventMessage
import org.arrow.runtime.message.EventMessage
import org.arrow.runtime.message.ExecuteEventMessage
import org.arrow.runtime.message.FinishEventMessage
import org.arrow.runtime.message.impl.*
import org.arrow.runtime.message.infrastructure.CallableEventMessage
import org.arrow.runtime.message.infrastructure.PersistEventMessage
import org.arrow.runtime.support.EngineSynchronizationManagerCallableDecorator
import org.arrow.service.engine.actor.template.MasterTemplate
import org.arrow.service.engine.concurrent.SynchronisationCallable
import org.arrow.service.engine.concurrent.dispatch.onfailure.PrintStacktraceOnFailure
import org.arrow.service.engine.concurrent.dispatch.onsuccess.NotifyOnSuccess
import org.arrow.service.engine.concurrent.dispatch.onsuccess.PublishEventMessagesOnSuccess
import org.arrow.service.engine.concurrent.dispatch.onsuccess.TellActorForEachMessage
import org.arrow.service.engine.concurrent.dispatch.recover.Recovers
import org.arrow.util.FutureUtil
import org.arrow.util.Objects
import org.springframework.context.ApplicationContext

import java.util.concurrent.Callable

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

        context.watch(nodeWorker);

        this.parentActor = sender
        this.monitor = start.processInstance

        if (start.getEntity() != null) {
            nodeWorker.tell(start, self)
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveContinue(ContinueEventMessage msg) {
        fetchEntityIfNecessary(msg.entity)
        nodeWorker.tell(msg, self)
    }

    @Override
    protected void onReceiveExecute(ExecuteEventMessage executeEventMessage) {
        fetchEntityIfNecessary(executeEventMessage.entity);
        nodeWorker.tell(executeEventMessage, self)
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
    protected void onReceiveEnd(final EndEventMessage end) {

        if (!tokenRegistry.hasToken()) {
            monitor.setState(State.SUCCESS)

            storedFutures.onSuccess(new NotifyOnSuccess(monitor), dispatcher())
            storedFutures.onFailure(new PrintStacktraceOnFailure(monitor), dispatcher())

            def pi = end.processInstance

            storedFutures.onSuccess(new OnSuccess<Iterable<Object>>() {

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void onSuccess(Iterable<Object> objects) throws Throwable {

                    def repo = executionService.data().execution();
                    BpmnNodeEntity subProcess = (BpmnNodeEntity) Objects.firstNonNull(pi.processTrigger, pi.process)

                    ProcessInstance parentPi = pi.parentProcessInstance
                    if (parentPi == null) {
                        // stop the actor
                        context.stop(self)
                        return;
                    }

                    Execution execution = repo.findByEntityId(subProcess.nodeId, parentPi.nodeId)
                    if (execution == null) {
                        self.tell(end, self)
                        return;
                    }

                    parentActor.tell(new EndSubProcessEventMessage(execution), self)
                }

            }, dispatcher());

        } else {
            def callable = new SynchronisationCallable(end.execution, executionService)
            def message = new PersistEventMessage(end.execution, new CallableEventMessage(end.execution, callable))

            self.tell(message, self)
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveSignalEvent(final SignalEventMessage signalEvent) {
        nodeWorker.tell(signalEvent, self)

    }

    private Callable<Iterable<EventMessage>> decorateAll(Callable<Iterable<EventMessage>> callable) {
        return new EngineSynchronizationManagerCallableDecorator<>(scopeMap, callable, self)
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

        def sub = (SubProcessEntity) msg.entity

        // store a sub process instance
        ProcessInstanceStore store = getBean(ProcessInstanceStore)

        if (sub instanceof AdHocSubProcess) {
            def subPi = store.store((AdHocSubProcess) sub, msg.processInstance)

            def message = new StartEventMessage(subPi)

            def future = FutureUtil.result(message);
            future.onSuccess(new PublishEventMessagesOnSuccess(applicationContext, self), dispatcher())
        } else {
            def subPi = store.store(sub, msg.startEvent, msg.processInstance)

            def message = new StartEventMessage(msg.startEvent, subPi)

            def future = FutureUtil.result(message);
            future.onSuccess(new PublishEventMessagesOnSuccess(applicationContext, self), dispatcher())
        }



    }

    @Override
    protected void onReceiveFinishMessage(FinishEventMessage message) {
        fetchEntityIfNecessary(message.entity)
        nodeWorker.tell(message, self)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveEndSubProcess(final EndSubProcessEventMessage msg) {

        final BpmnNodeEntity sub = (BpmnNodeEntity) msg.getEntity();
        final Execution execution = msg.getExecution();

        if (isCallActivityOrStandardSubProcess(sub)) {
            self().tell(new DefaultFinishEventMessage(execution), self());
            self().tell(new DefaultContinueEventMessage(execution), self());
        } else {
            self().tell(new DefaultFinishEventMessage(execution), self());
        }

    }

    /**
     * Indicates if the given entity is either a call activity/standard sub process or not.
     *
     * @param entity
     * @return boolean
     */
    private static boolean isCallActivityOrStandardSubProcess(BpmnNodeEntity entity) {
        switch (entity) {
            case CallActivityTask: return true
            default: !((SubProcess)entity).isForCompensation() && !((SubProcess)entity).isTriggeredByEvent()
        }
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
        Execution execution = message.getExecution()
        execution.setState(State.WAITING)

        getExecutionService().saveEntity(execution)

        nodeWorker.tell(message, self)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveEscalationEvent(final EscalationEventMessage escalationEvent) {
        nodeWorker.tell(escalationEvent, self)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveCancelEvent(CancelEventMessage cancelEvent) {

        becomeBehavior(new CompensateBehavior(this))

        // prepare the callable instance
        // *****************************
        def callable = new Callable() {
            @Override
            Object call() throws Exception {
                def piId = cancelEvent.processInstance.id
                def executions = getBean(ExecutionRepository).findCompensateBoundaryEventExecutions(piId)

                executions.collect {new DefaultFinishEventMessage(it)}
            }
        }

        def future = Futures.future(decorateAll(callable), dispatcher())
        future.recover(Recovers.logAndReturnNull(), dispatcher())
        future.onSuccess(new TellActorForEachMessage(self, nodeWorker), dispatcher())
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReceiveCompensateEvent(CompensateEventMessage escalationEvent) {
        println" mmmmmmmmmmmmmmmmmmmmmmmmm"
        def pi = escalationEvent.processInstance
        def service = getExecutionService()

        def executions = service.data().execution().findUnsuccessfulCompensateTaskExecutions(pi.nodeId)

        if (!executions.containsObject(null)) {
            // remove the compensate behavior
            unbecomeBehavior()
            // find all cancel end events which are in state 'WAIT'
            def endEventExecution = service.data().execution().findCancelEndEventExecutionInStateWait(pi.id)
            // continue the waiting end events
            self.tell(new ContinuingFinishEventMessage(endEventExecution), self)
        }

    }

}
