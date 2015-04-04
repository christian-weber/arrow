package org.arrow.parser.xml.bpmn

import com.thoughtworks.xstream.XStream
import groovy.transform.CompileStatic
import org.arrow.model.event.boundary.BoundaryEvent
import org.arrow.model.event.endevent.EndEvent
import org.arrow.model.event.intermediate.catching.IntermediateCatchEvent
import org.arrow.model.event.intermediate.throwing.IntermediateThrowEvent
import org.arrow.model.event.startevent.StartEvent
import org.arrow.model.gateway.impl.ComplexGateway
import org.arrow.model.gateway.impl.EventBasedGateway
import org.arrow.model.gateway.impl.ExclusiveGateway
import org.arrow.model.gateway.impl.InclusiveGateway
import org.arrow.model.gateway.impl.ParallelGateway
import org.arrow.model.process.AdHocSubProcess
import org.arrow.model.process.Definitions
import org.arrow.model.process.Process
import org.arrow.model.process.SubProcess
import org.arrow.model.process.Transaction
import org.arrow.model.process.event.Escalation
import org.arrow.model.process.event.Message
import org.arrow.model.process.event.Signal
import org.arrow.model.task.Task
import org.arrow.model.task.impl.CallActivityTask
import org.arrow.model.task.multi.MultiInstanceLoopCharacteristics
import org.arrow.model.transition.impl.Association
import org.arrow.model.transition.impl.SequenceFlow
import org.arrow.parser.xml.bpmn.element.BoundaryEventConverter
import org.arrow.parser.xml.bpmn.element.ComplexGatewayConverter
import org.arrow.parser.xml.bpmn.element.ConditionExpressionConverter
import org.arrow.parser.xml.bpmn.element.ElementConverter
import org.arrow.parser.xml.bpmn.element.EndEventConverter
import org.arrow.parser.xml.bpmn.element.EventBasedGatewayConverter
import org.arrow.parser.xml.bpmn.element.IntermediateCatchEventConverter
import org.arrow.parser.xml.bpmn.element.IntermediateThrowEventConverter
import org.arrow.parser.xml.bpmn.element.StartEventConverter
import org.arrow.parser.xml.bpmn.element.TaskConverter
import org.arrow.parser.xml.bpmn.element.flow.SequenceFlowConverter
import org.arrow.parser.xml.bpmn.element.gateway.GatewayConverter
import org.arrow.parser.xml.bpmn.element.task.CallActivityTaskConverter
import org.arrow.parser.xml.bpmn.element.task.multi.LoopCardinalityConverter

/**
 * Arrow XStream implementation.
 *
 * @since 1.0.0
 * @author christian.weber
 */
@Singleton
@CompileStatic
class BpmnXStream extends XStream {

    {
        // PROCESS DEFINITIONS
        // ###################
        alias("definitions", Definitions)
        addImplicitCollection(Definitions, "processes", Process)
        aliasField("process", Definitions, "processes")
        addImplicitCollection(Definitions, "signals", Signal)
        aliasField("signal", Definitions, "signals")
        useAttributeFor(Signal, "id")
        useAttributeFor(Signal, "name")
        addImplicitCollection(Definitions, "messages", Message)
        aliasField("message", Definitions, "messages")
        useAttributeFor(Message, "id")
        useAttributeFor(Message, "name")
        addImplicitCollection(Definitions, "escalations", Escalation)
        aliasField("escalation", Definitions, "escalations")
        useAttributeFor(Escalation, "id")
        useAttributeFor(Escalation, "escalationCode")

        // PROCESS
        // #######
        useAttributeFor(Process, "id")

        // SUB PROCESS
        // ###########
        useAttributeFor(SubProcess, "triggeredByEvent")
        useAttributeFor(SubProcess, "isForCompensation")
        aliasField("subProcess", Process, "parsedSubProcesses")
        addImplicitCollection(Process, "parsedSubProcesses", SubProcess)
        useAttributeFor(SubProcess, "id")

        // TRANSACTION
        // ###########
        useAttributeFor(Transaction, "triggeredByEvent")
        useAttributeFor(Transaction, "isForCompensation")
        aliasField("transaction", Process, "parsedTransactions")
        useAttributeFor(Transaction, "id")
        addImplicitCollection(Process, "parsedTransactions", Transaction)

        // AD HOC SUB PROCESS
        // ##################
        aliasField("adHocSubProcess", Process, "parsedAdHocSubProcesses")
        useAttributeFor(AdHocSubProcess, "id")
        addImplicitCollection(Process, "parsedAdHocSubProcesses", AdHocSubProcess)

        // START EVENTS
        // ############
        aliasField("startEvent", Process, "parsedStartEvents")
        addImplicitCollection(Process, "parsedStartEvents", StartEvent)
        registerConverter(new StartEventConverter())

        // END EVENTS
        // ##########
        aliasField("endEvent", Process, "parsedEndEvents")
        addImplicitCollection(Process, "parsedEndEvents", EndEvent)
        registerConverter(new EndEventConverter())

        // INTERMEDIATE CATCH EVENTS
        // #########################
        aliasField("intermediateCatchEvent", Process, "parsedIntermediateCatchEvents")
        addImplicitCollection(Process, "parsedIntermediateCatchEvents", IntermediateCatchEvent)
        registerConverter(new IntermediateCatchEventConverter())

        // INTERMEDIATE THROWING EVENTS
        // ############################
        aliasField("intermediateThrowEvent", Process, "parsedIntermediateThrowEvents")
        addImplicitCollection(Process, "parsedIntermediateThrowEvents", IntermediateThrowEvent)
        registerConverter(new IntermediateThrowEventConverter())

        // SEQUENCE FLOW
        // #############
        aliasField("sequenceFlow", Process, "parsedSequenceFlows")
        addImplicitCollection(Process, "parsedSequenceFlows", SequenceFlow)
        registerConverter(new SequenceFlowConverter())
        registerConverter(new ConditionExpressionConverter())

        // ASSOCIATION
        // ###########
        aliasField("association", Process, "parsedAssociations")
        addImplicitCollection(Process, "parsedAssociations", Association)
        useAttributeFor(Association, "targetRef")
        useAttributeFor(Association, "sourceRef")
        registerLocalConverter(Association, "sourceRef", new ElementConverter())
        registerLocalConverter(Association, "targetRef", new ElementConverter())

        // SERVICE TASKS
        // #############
        aliasField("serviceTask", Process, "parsedTasks")
        aliasField("manualTask", Process, "parsedTasks")
        aliasField("userTask", Process, "parsedTasks")
        aliasField("scriptTask", Process, "parsedTasks")
        aliasField("sendTask", Process, "parsedTasks")
        aliasField("receiveTask", Process, "parsedTasks")
        aliasField("businessRuleTask", Process, "parsedTasks")
        addImplicitCollection(Process, "parsedTasks", Task)
        registerConverter(new TaskConverter())

        // EXCLUSIVE GATEWAY
        // #################
        aliasField("exclusiveGateway", Process, "parsedExclusiveGateways")
        addImplicitCollection(Process, "parsedExclusiveGateways", ExclusiveGateway)
        registerConverter(new GatewayConverter())

        // INCLUSIVE GATEWAY
        // #################
        aliasField("inclusiveGateway", Process, "parsedInclusiveGateways")
        addImplicitCollection(Process, "parsedInclusiveGateways", InclusiveGateway)
        useAttributeFor(InclusiveGateway, "id")
        useAttributeFor(InclusiveGateway, "name")

        // COMPLEX GATEWAY
        // ###############
        aliasField("complexGateway", Process, "parsedComplexGateways")
        addImplicitCollection(Process, "parsedComplexGateways", ComplexGateway)
        registerConverter(new ComplexGatewayConverter())

        // PARALLEL GATEWAY
        // ################
        aliasField("parallelGateway", Process, "parsedParallelGateways")
        addImplicitCollection(Process, "parsedParallelGateways", ParallelGateway)
        useAttributeFor(ParallelGateway, "id")
        useAttributeFor(ParallelGateway, "name")

        // EVENT BASED GATEWAY
        // ###################
        aliasField("eventBasedGateway", Process, "parsedEventBasedGateways")
        addImplicitCollection(Process, "parsedEventBasedGateways", EventBasedGateway)
        registerConverter(new EventBasedGatewayConverter())

        // BOUNDARY EVENTS
        // ###############
        aliasField("boundaryEvent", Process, "parsedBoundaryEvents")
        addImplicitCollection(Process, "parsedBoundaryEvents", BoundaryEvent)
        registerConverter(new BoundaryEventConverter())

        // CALL ACTIVITY
        // #############
        aliasField("callActivity", Process, "parsedCallActivityTasks")
        addImplicitCollection(Process, "parsedCallActivityTasks", CallActivityTask)
        registerConverter(new CallActivityTaskConverter())

        // MULTI INSTANCE LOOP CHARACTERISTICS
        // ###################################
        aliasAttribute(MultiInstanceLoopCharacteristics, "sequential", "isSequential")

        // LOOP CARDINALITY
        // ################
        registerConverter(new LoopCardinalityConverter())
    }

}
