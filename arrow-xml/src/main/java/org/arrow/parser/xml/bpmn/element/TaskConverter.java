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

package org.arrow.parser.xml.bpmn.element;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.arrow.model.definition.message.MessageEventDefinition;
import org.arrow.model.task.AbstractTask;
import org.arrow.model.task.Task;
import org.arrow.model.task.impl.*;
import org.arrow.model.task.multi.LoopCardinality;
import org.arrow.model.task.multi.MultiInstanceLoopCharacteristics;
import org.arrow.parser.xml.bpmn.util.ConverterUtils;
import org.arrow.runtime.rule.RuleData;

/**
 * {@link com.thoughtworks.xstream.converters.Converter} implementation used to convert BPMN
 * {@link org.arrow.model.gateway.impl.ComplexGateway} instances.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class TaskConverter implements Converter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class type) {
		return Task.class.isAssignableFrom(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {

        final String id = reader.getAttribute("id");
        final String name = reader.getAttribute("name");
        final boolean isForCompensation = ConverterUtils.toBoolean(reader.getAttribute("isForCompensation"));

        AbstractTask task = parseTask(reader);

		task.setId(id);
		task.setName(name);
        task.setForCompensation(isForCompensation);

        return task;
	}

    private AbstractTask parseTask(HierarchicalStreamReader reader) {

        switch (reader.getNodeName()) {
            case "serviceTask": return parseServiceTask(reader);
            case "manualTask": return new ManualTask();
            case "userTask": return new UserTask();
            case "scriptTask": return parseScriptTask(reader);
            case "sendTask": return parseSendTask(reader);
            case "receiveTask": return parseReceiveTask(reader);
            case "businessRuleTask": return parseBusinessRuleTask(reader);
        }

        throw new RuntimeException();
    }

    private ScriptTask parseScriptTask(HierarchicalStreamReader reader) {
        ScriptTask task = new ScriptTask();

        task.setScriptLanguage(reader.getAttribute("scriptFormat"));
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            switch (truncateNamespace(reader.getNodeName())) {
                case "script": task.setScript(reader.getValue()); break;
                case "multiInstanceLoopCharacteristics": task.setMultiInstanceLoopCharacteristics(parseLoop(reader));
            }
            reader.moveUp();
        }

        return task;
    }

    private MultiInstanceLoopCharacteristics parseLoop(HierarchicalStreamReader reader) {
        MultiInstanceLoopCharacteristics characteristics = new MultiInstanceLoopCharacteristics();
        characteristics.setSequential(true);
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            switch (reader.getNodeName()) {
                case "loopCardinality":
                    LoopCardinality loopCardinality = new LoopCardinality();
                    loopCardinality.setId(System.nanoTime() + "");
                    loopCardinality.setCardinality(Integer.parseInt(reader.getValue()));
                    characteristics.setLoopCardinality(loopCardinality);
            }
            reader.moveUp();
        }
        return characteristics;
    }

    private SendTask parseSendTask(HierarchicalStreamReader reader) {
        SendTask task = new SendTask();
        task.setMessageRef(reader.getAttribute("messageRef"));

        return task;
    }

    private ReceiveTask parseReceiveTask(HierarchicalStreamReader reader) {
        ReceiveTask task = new ReceiveTask();

        MessageEventDefinition definition = new MessageEventDefinition();
        definition.setMessageRef(reader.getAttribute("messageRef"));

        task.setMessageEventDefinition(definition);
        task.setInstantiate(ConverterUtils.toBoolean(reader.getAttribute("instantiate")));

        return task;
    }

    private BusinessRuleTask parseBusinessRuleTask(HierarchicalStreamReader reader) {
        BusinessRuleTask task = new BusinessRuleTask();

        reader.moveDown();

        while (reader.hasMoreChildren()) {
            reader.moveDown();
            switch (truncateNamespace(reader.getNodeName())) {
                case "ruleFormat": task.setRuleFormat(reader.getAttribute("format")); break;
                case "ruleSource": task.setRuleSource(reader.getAttribute("source")); break;
                case "ruleData": task.getDataList().add(new RuleData(reader.getAttribute("type"), reader.getAttribute("name"))); break;
            }
            reader.moveUp();
        }

        reader.moveUp();

        return task;
    }

    private ServiceTask parseServiceTask(HierarchicalStreamReader reader) {
        ServiceTask task = new ServiceTask();

        reader.moveDown();

        while (reader.hasMoreChildren()) {
            reader.moveDown();
            switch (truncateNamespace(reader.getNodeName())) {
                case "serviceClass": task.setServiceClass(reader.getValue().trim());
            }
            reader.moveUp();
        }

        reader.moveUp();

        return task;
    }

    private String truncateNamespace(String nodeName) {
        return nodeName.substring(nodeName.indexOf(":") + 1);
    }

}