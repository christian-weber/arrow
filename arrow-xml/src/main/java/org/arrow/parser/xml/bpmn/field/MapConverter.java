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

package org.arrow.parser.xml.bpmn.field;

import com.thoughtworks.xstream.XStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

public abstract class MapConverter<S, T> implements
		Converter<S, T> {

	public static class StringConverter extends
            MapConverter<Map, String> {

		@Autowired
		private XStream xstream;

		@Override
		public String convert(Map str) {
			return xstream.toXML(str);
		}

	}

	public static class ObjectConverter extends
            MapConverter<String, Map> {

		@Autowired
		private XStream xstream;

		@Override
		public Map convert(String str) {
			return (Map) xstream.fromXML(str);
		}

	}

}
