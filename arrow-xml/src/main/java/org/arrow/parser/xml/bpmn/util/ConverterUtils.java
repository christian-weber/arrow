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

package org.arrow.parser.xml.bpmn.util;

/**
 * Converter utility class.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public final class ConverterUtils {

	private ConverterUtils() {
		super();
	}

	/**
	 * Converts the given string to a boolean.
	 * 
	 * @param str the boolean string to convert
	 * @return boolean
	 */
	public static boolean toBoolean(String str) {
        return str != null && "true".equalsIgnoreCase(str);
    }
	
	/**
	 * Converts the given string to a integer.
	 * 
	 * @param str the integer string to convert
	 * @return int
	 */
	public static int toInteger(String str) {
		return Integer.parseInt(str);
	}

}
