/*
 * Copyright 2014 Christian Weber
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

package org.arrow.parser.xml.bpmn.element

import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.converters.MarshallingContext
import com.thoughtworks.xstream.converters.UnmarshallingContext
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import org.arrow.model.definition.message.MessageEventDefinition
import org.arrow.model.task.AbstractTask
import org.arrow.model.task.Task
import org.arrow.model.task.impl.*
import org.arrow.model.task.multi.LoopCardinality
import org.arrow.model.task.multi.MultiInstanceLoopCharacteristics
import org.arrow.parser.xml.bpmn.util.ConverterUtils
import org.arrow.runtime.rule.RuleData

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
    public boolean canConvert(Class type) {
        return Task.isAssignableFrom(type)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        throw new UnsupportedOperationException()
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

        final String id = reader.getAttribute("id")
        final String name = reader.getAttribute("name")
        final boolean isForCompensation = ConverterUtils.toBoolean(reader.getAttribute("isForCompensation"))

        AbstractTask task = parseTask(reader)

        task.setId(id)
        task.setName(name)
        task.setForCompensation(isForCompensation)

        return task
    }

    /**
     * Parses the task implementation.
     *
     * @param reader
     * @return AbstractTask
     */
    private static AbstractTask parseTask(HierarchicalStreamReader reader) {
        // @formatter:off
        switch (reader.nodeName) {
            case "serviceTask":      return parseServiceTask(reader)
            case "manualTask":       return new ManualTask()
            case "userTask":         return new UserTask()
            case "scriptTask":       return parseScriptTask(reader)
            case "sendTask":         return parseSendTask(reader)
            case "receiveTask":      return parseReceiveTask(reader)
            case "businessRuleTask": return parseBusinessRuleTask(reader)
            default: throw new RuntimeException("can not determine task with name $reader.nodeName")
        }
        // @formatter:on
    }

    /**
     * Parses a script task implementation.
     *
     * @param reader
     * @return AbstractTask
     */
    private static ScriptTask parseScriptTask(HierarchicalStreamReader reader) {
        def task = new ScriptTask(scriptLanguage: reader.getAttribute("scriptFormat"))

        while (reader.hasMoreChildren()) {
            reader.nextElement {
                switch (truncateNamespace(reader.nodeName)) {
                    case "script": task.setScript(reader.getValue()); break
                    case "multiInstanceLoopCharacteristics": task.setMultiInstanceLoopCharacteristics(parseLoop(reader)); break
                }
            }
        }

        return task
    }

    /**
     * Parses a multi instance loop characteristics instance.
     *
     * @param reader
     * @return AbstractTask
     */
    private static MultiInstanceLoopCharacteristics parseLoop(HierarchicalStreamReader reader) {
        def characteristics = new MultiInstanceLoopCharacteristics(sequential: true)

        def id = System.&nanoTime
        def cardinality = Integer.&parseInt(reader.value)

        while (reader.hasMoreChildren()) {
            reader.nextElement {
                switch (reader.getNodeName()) {
                    case "loopCardinality": characteristics.setLoopCardinality(new LoopCardinality(id: id, cardinality: cardinality))
                }
            }
        }
        return characteristics
    }

    /**
     * Parses a send task implementation.
     *
     * @param reader
     * @return AbstractTask
     */
    private static SendTask parseSendTask(HierarchicalStreamReader reader) {
        new SendTask(messageRef: reader.getAttribute("messageRef"))
    }

    /**
     * Parses a receive task implementation.
     *
     * @param reader
     * @return AbstractTask
     */
    private static ReceiveTask parseReceiveTask(HierarchicalStreamReader reader) {
        ReceiveTask task = new ReceiveTask()

        MessageEventDefinition definition = new MessageEventDefinition()
        definition.setMessageRef(reader.getAttribute("messageRef"))

        task.setMessageEventDefinition(definition)
        task.setInstantiate(ConverterUtils.toBoolean(reader.getAttribute("instantiate")))

        return task
    }

    /**
     * Parses a business rule task implementation.
     *
     * @param reader
     * @return AbstractTask
     */
    private static BusinessRuleTask parseBusinessRuleTask(HierarchicalStreamReader reader) {
        def task = new BusinessRuleTask()

        reader.nextElement {
            while (reader.hasMoreChildren()) {
                reader.nextElement {
                    switch (truncateNamespace(reader.nodeName)) {
                        case "ruleFormat": task.setRuleFormat(reader.getAttribute("format")); break
                        case "ruleSource": task.setRuleSource(reader.getAttribute("source")); break
                        case "ruleData":   task.getDataList().add(new RuleData(reader.getAttribute("type"), reader.getAttribute("name"))); break
                    }
                }
            }
        }
        return task
    }

    /**
     * Parses a service task implementation.
     *
     * @param reader
     * @return AbstractTask
     */
    private static ServiceTask parseServiceTask(HierarchicalStreamReader reader) {
        def task = new ServiceTask()

        reader.nextElement {
            while (reader.hasMoreChildren()) {
                reader.nextElement {
                    switch (truncateNamespace(reader.nodeName)) {
                        case "serviceClass": task.setServiceClass(reader.value.trim()); break
                        case "beanName":     task.setBeanName(reader.value.trim());     break
                        case "expression":   task.setExpression(reader.value.trim());   break
                    }
                }
            }
        }
        return task
    }

    /**
     * Truncates the namespace from the given node name.
     *
     * @param nodeName
     * @return String
     */
    private static String truncateNamespace(String nodeName) {
        return nodeName.substring(nodeName.indexOf(":") + 1)
    }

}