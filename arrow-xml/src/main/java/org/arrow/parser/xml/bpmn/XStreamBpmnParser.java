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

package org.arrow.parser.xml.bpmn;

import java.io.InputStream;

import org.springframework.util.Assert;
import org.arrow.model.event.boundary.BoundaryEvent;
import org.arrow.model.event.endevent.EndEvent;
import org.arrow.model.event.intermediate.catching.IntermediateCatchEvent;
import org.arrow.model.event.intermediate.throwing.IntermediateThrowEvent;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.model.gateway.impl.*;
import org.arrow.model.process.*;
import org.arrow.model.process.Process;
import org.arrow.model.process.event.Escalation;
import org.arrow.model.process.event.Message;
import org.arrow.model.process.event.Signal;
import org.arrow.model.task.Task;
import org.arrow.model.task.impl.CallActivityTask;
import org.arrow.model.task.impl.ServiceTask;
import org.arrow.model.task.multi.MultiInstanceLoopCharacteristics;
import org.arrow.model.transition.impl.Association;
import org.arrow.model.transition.impl.SequenceFlow;
import org.arrow.parser.xml.bpmn.element.*;
import org.arrow.parser.xml.bpmn.element.flow.SequenceFlowConverter;
import org.arrow.parser.xml.bpmn.element.gateway.GatewayConverter;
import org.arrow.parser.xml.bpmn.element.task.CallActivityTaskConverter;
import org.arrow.parser.xml.bpmn.element.task.multi.LoopCardinalityConverter;

import com.thoughtworks.xstream.XStream;

/**
 * XStream {@link BpmnParser} implementation.
 *   
 * @author christian.weber
 * @since 1.0.0
 */
public class XStreamBpmnParser implements BpmnParser {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Definitions parse(InputStream stream) {
		
		Assert.notNull(stream, "stream must not be null");
		
		XStream xstream = new XStream();
		
		// PROCESS DEFINITIONS
		xstream.alias("definitions", Definitions.class);
		xstream.addImplicitCollection(Definitions.class, "processes", Process.class);
		xstream.aliasField("process", Definitions.class, "processes");		
		xstream.addImplicitCollection(Definitions.class, "signals", Signal.class);
		xstream.aliasField("signal", Definitions.class, "signals");		
		xstream.useAttributeFor(Signal.class, "id");
		xstream.useAttributeFor(Signal.class, "name");
		xstream.addImplicitCollection(Definitions.class, "messages", Message.class);
		xstream.aliasField("message", Definitions.class, "messages");		
		xstream.useAttributeFor(Message.class, "id");
		xstream.useAttributeFor(Message.class, "name");
		xstream.addImplicitCollection(Definitions.class, "escalations", Escalation.class);
		xstream.aliasField("escalation", Definitions.class, "escalations");
		xstream.useAttributeFor(Escalation.class, "id");
		xstream.useAttributeFor(Escalation.class, "escalationCode");
		
		// PROCESS
		xstream.useAttributeFor(Process.class, "id");
//		xstream.useAttributeFor(Process.class, "executable");

        // SUB PROCESS
        configureSubProcess(xstream);
		configureTransaction(xstream);
		configureAdHocSubProcess(xstream);


		// START EVENTS
		xstream.aliasField("startEvent", Process.class, "parsedStartEvents");
		xstream.addImplicitCollection(Process.class, "parsedStartEvents", StartEvent.class);
		xstream.registerConverter(new StartEventConverter());

		// END EVENTS
		xstream.aliasField("endEvent", Process.class, "parsedEndEvents");		
		xstream.addImplicitCollection(Process.class, "parsedEndEvents", EndEvent.class);
		xstream.registerConverter(new EndEventConverter());
		
		// INTERMEDIATE CATCH EVENTS
		xstream.aliasField("intermediateCatchEvent", Process.class, "parsedIntermediateCatchEvents");
		xstream.addImplicitCollection(Process.class, "parsedIntermediateCatchEvents", IntermediateCatchEvent.class);
		xstream.registerConverter(new IntermediateCatchEventConverter());
		
		// INTERMEDIATE THROWING EVENTS
		xstream.aliasField("intermediateThrowEvent", Process.class, "parsedIntermediateThrowEvents");
		xstream.addImplicitCollection(Process.class, "parsedIntermediateThrowEvents", IntermediateThrowEvent.class);
		xstream.registerConverter(new IntermediateThrowEventConverter());
		
		configureSequenceFlow(xstream);

		xstream.registerConverter(new ConditionExpressionConverter());		// SEQUENCE FLOWS

		configureAssociation(xstream);

		// SERVICE TASKS
		configureTasks(xstream);
		
		configureExclusiveGateway(xstream);


        configureInclusiveGateway(xstream);
        configureComplexGateway(xstream);
		
		// PARALLEL GATEWAY
		xstream.aliasField("parallelGateway", Process.class, "parsedParallelGateways");
		xstream.addImplicitCollection(Process.class, "parsedParallelGateways", ParallelGateway.class);
		xstream.useAttributeFor(ParallelGateway.class, "id");
		xstream.useAttributeFor(ParallelGateway.class, "name");
		
		// EVENT BASED GATEWAY
		xstream.aliasField("eventBasedGateway", Process.class, "parsedEventBasedGateways");
		xstream.addImplicitCollection(Process.class, "parsedEventBasedGateways", EventBasedGateway.class);
		xstream.registerConverter(new EventBasedGatewayConverter());
		
		// BOUNDARY EVENTS
		xstream.aliasField("boundaryEvent", Process.class, "parsedBoundaryEvents");		
		xstream.addImplicitCollection(Process.class, "parsedBoundaryEvents", BoundaryEvent.class);
		xstream.registerConverter(new BoundaryEventConverter());

		// CALL ACTIVITY
		xstream.aliasField("callActivity", Process.class, "parsedCallActivityTasks");
		xstream.addImplicitCollection(Process.class, "parsedCallActivityTasks", CallActivityTask.class);
		xstream.registerConverter(new CallActivityTaskConverter());
		
		// MULTI INSTANCE LOOP CHARACTERISTICS
		xstream.aliasAttribute(MultiInstanceLoopCharacteristics.class, "sequential", "isSequential");
		
		// LOOP CARDINALITY
		xstream.registerConverter(new LoopCardinalityConverter());
		
		return (Definitions) xstream.fromXML(stream);
	}

    private void configureInclusiveGateway(XStream xstream) {
        xstream.aliasField("inclusiveGateway", Process.class, "parsedInclusiveGateways");
        xstream.addImplicitCollection(Process.class, "parsedInclusiveGateways", InclusiveGateway.class);
        xstream.useAttributeFor(InclusiveGateway.class, "id");
        xstream.useAttributeFor(InclusiveGateway.class, "name");
    }

    private void configureComplexGateway(XStream xstream) {
        xstream.aliasField("complexGateway", Process.class, "parsedComplexGateways");
        xstream.addImplicitCollection(Process.class, "parsedComplexGateways", ComplexGateway.class);
        xstream.registerConverter(new ComplexGatewayConverter());
    }

    private void configureTasks(XStream xstream) {
        xstream.aliasField("serviceTask", Process.class, "parsedTasks");
        xstream.aliasField("manualTask", Process.class, "parsedTasks");
        xstream.aliasField("userTask", Process.class, "parsedTasks");
        xstream.aliasField("scriptTask", Process.class, "parsedTasks");
        xstream.aliasField("sendTask", Process.class, "parsedTasks");
        xstream.aliasField("receiveTask", Process.class, "parsedTasks");
        xstream.aliasField("businessRuleTask", Process.class, "parsedTasks");
        xstream.addImplicitCollection(Process.class, "parsedTasks", Task.class);
        xstream.registerConverter(new TaskConverter());
    }

    private void configureSubProcess(XStream xstream) {
        xstream.useAttributeFor(SubProcess.class, "triggeredByEvent");
        xstream.useAttributeFor(SubProcess.class, "isForCompensation");

        xstream.aliasField("subProcess", Process.class, "parsedSubProcesses");
        xstream.addImplicitCollection(Process.class, "parsedSubProcesses", SubProcess.class);
        xstream.useAttributeFor(SubProcess.class, "id");
//        xstream.registerConverter(new SubProcessConverter());
    }

	private void configureTransaction(XStream xstream) {
        xstream.useAttributeFor(Transaction.class, "triggeredByEvent");
        xstream.useAttributeFor(Transaction.class, "isForCompensation");

        xstream.aliasField("transaction", Process.class, "parsedTransactions");
        xstream.useAttributeFor(Transaction.class, "id");
		xstream.addImplicitCollection(Process.class, "parsedTransactions", Transaction.class);
    }

	private void configureAdHocSubProcess(XStream xstream) {

		xstream.aliasField("adHocSubProcess", Process.class, "parsedAdHocSubProcesses");
		xstream.useAttributeFor(AdHocSubProcess.class, "id");
		xstream.addImplicitCollection(Process.class, "parsedAdHocSubProcesses", AdHocSubProcess.class);
	}

	private void configureAssociation(XStream xstream) {
		xstream.aliasField("association", Process.class, "parsedAssociations");
		xstream.addImplicitCollection(Process.class, "parsedAssociations", Association.class);
		xstream.useAttributeFor(Association.class, "targetRef");
		xstream.useAttributeFor(Association.class, "sourceRef");
		xstream.registerLocalConverter(Association.class, "sourceRef", new ElementConverter());
		xstream.registerLocalConverter(Association.class, "targetRef", new ElementConverter());
	}

	private void configureSequenceFlow(XStream xstream) {
		xstream.aliasField("sequenceFlow", Process.class, "parsedSequenceFlows");
		xstream.addImplicitCollection(Process.class, "parsedSequenceFlows", SequenceFlow.class);
		xstream.registerConverter(new SequenceFlowConverter());
	}

	private void configureExclusiveGateway(XStream xstream) {
		xstream.aliasField("exclusiveGateway", Process.class, "parsedExclusiveGateways");
		xstream.addImplicitCollection(Process.class, "parsedExclusiveGateways", ExclusiveGateway.class);
		xstream.registerConverter(new GatewayConverter());
	}

}