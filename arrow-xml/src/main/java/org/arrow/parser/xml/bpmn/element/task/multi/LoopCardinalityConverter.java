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

package org.arrow.parser.xml.bpmn.element.task.multi;

import org.arrow.model.event.boundary.BoundaryEvent;
import org.arrow.model.task.multi.LoopCardinality;
import org.arrow.parser.xml.bpmn.util.ConverterUtils;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * {@link Converter} implementation used to convert BPMN {@link BoundaryEvent}
 * instances.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class LoopCardinalityConverter implements Converter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class type) {
		return LoopCardinality.class.isAssignableFrom(type);
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

		LoopCardinality loopCardinality = new LoopCardinality();
		loopCardinality.setId(reader.getAttribute("id"));
		loopCardinality.setCardinality(ConverterUtils.toInteger(reader.getValue()));
		
		if (loopCardinality.getId() == null) {
			loopCardinality.setId("loopCardinality_" + System.currentTimeMillis());
		}
		
		return loopCardinality;
	}

	

}