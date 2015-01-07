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
import org.arrow.model.process.Process;
import org.arrow.model.process.SubProcess;
import org.arrow.parser.xml.bpmn.util.ConverterUtils;

/**
 * {@link com.thoughtworks.xstream.converters.Converter} implementation used to convert BPMN
 * {@link org.arrow.model.gateway.impl.ComplexGateway} instances.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class SubProcessConverter implements Converter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class type) {
		return SubProcess.class.isAssignableFrom(type);
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
        final boolean triggeredByEvent = ConverterUtils.toBoolean(reader.getAttribute("triggeredByEvent"));
        final boolean isForCompensation = ConverterUtils.toBoolean(reader.getAttribute("isForCompensation"));
        final boolean executable = ConverterUtils.toBoolean(reader.getAttribute("executable"));

        SubProcess subProcess;
        if (triggeredByEvent) {
            subProcess = new SubProcess();
        } else if (isForCompensation) {
            subProcess = new SubProcess();
        } else {
            subProcess = new SubProcess();
        }

        subProcess.setId(id);
        subProcess.setName(name);
        subProcess.setTriggeredByEvent(triggeredByEvent);
        subProcess.setForCompensation(isForCompensation);
//        subProcess.setExecutable(executable);

        return context.convertAnother(subProcess, Process.class);
	}

}