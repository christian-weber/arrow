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

package org.arrow.parser.xml.bpmn.element.flow;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.arrow.model.PlaceholderBpmnEntity;
import org.arrow.model.transition.impl.ConditionExpression;
import org.arrow.model.transition.impl.SequenceFlow;

/**
 * {@link Converter} implementation used to convert BPMN {@link SequenceFlow} instances.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class SequenceFlowConverter implements Converter {

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public boolean canConvert(Class type) {
        return SequenceFlow.class.isAssignableFrom(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

        SequenceFlow flow = new SequenceFlow();
        flow.setId(firstNonNull(reader.getAttribute("id"), "flow_" + System.nanoTime()));
        flow.setName(reader.getAttribute("name"));

        String sourceRef = reader.getAttribute("sourceRef");
        PlaceholderBpmnEntity element1 = new PlaceholderBpmnEntity();
        element1.setId(sourceRef);
        element1.setName(sourceRef);

        String targetRef = reader.getAttribute("targetRef");
        PlaceholderBpmnEntity element2 = new PlaceholderBpmnEntity();
        element2.setId(targetRef);
        element2.setName(targetRef);

        flow.setSourceRef(element1);
        flow.setTargetRef(element2);

        while (reader.hasMoreChildren()) {
            reader.moveDown();

            switch (reader.getNodeName()) {
                case "conditionExpression":
                    flow.setConditionExpression((ConditionExpression) context.convertAnother(context.currentObject(), ConditionExpression.class));
            }

            reader.moveUp();
        }

        return flow;
    }

    @SuppressWarnings("Contract")
    private String firstNonNull(String... strings) {
        if (strings == null) {
            return null;
        }

        for (String str : strings) {
            if (str != null) {
                return str;
            }
        }
        return null;
    }

}