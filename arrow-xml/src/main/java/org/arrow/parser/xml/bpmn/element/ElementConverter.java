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

import org.arrow.model.BpmnEntity;
import org.arrow.model.PlaceholderBpmnEntity;

import com.thoughtworks.xstream.converters.SingleValueConverter;

public class ElementConverter implements SingleValueConverter {

	@Override
	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class type) {
		return BpmnEntity.class.isAssignableFrom(type);
	}

	@Override
	public String toString(Object obj) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object fromString(String str) {
		PlaceholderBpmnEntity element = new PlaceholderBpmnEntity();
		element.setId(str);
		element.setName(str);

		return element;
	}

}
