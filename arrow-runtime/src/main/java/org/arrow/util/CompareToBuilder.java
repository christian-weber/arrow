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

package org.arrow.util;

/**
 * Utility class used to build a comparison number for bean properties by
 * calling the append method for each compare characteristic.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class CompareToBuilder {

	private int comparison = 0;

	/**
	 * Compares the given string argument 1 with the given string argument 2.
	 * 
	 * @param str1 the first string
	 * @param str2 the second string
	 */
	public void append(String str1, String str2) {

		if (comparison != 0) {
			return;
		}

		if (str1 == null && str2 == null) {
			return;
		}

		if (str1 == null) {
			comparison = 1;
		} else if (str2 == null) {
			comparison = -1;
		} else {
            comparison = str1.compareTo(str2);
        }
	}

	/**
	 * Compares the given {@link Comparable} object argument 1 with the given
	 * {@link Comparable} object argument 2.
	 * 
	 * @param t1 the first comparable instance
	 * @param t2 the second comparable instance
	 */
	public <T extends Comparable<T>> void append(T t1, T t2) {

		if (comparison != 0) {
			return;
		}

		if (t1 == null && t2 == null) {
			return;
		}

		if (t1 == null) {
			comparison = 1;
		} else if (t2 == null) {
			comparison = -1;
		} else {
            comparison = t1.compareTo(t2);
        }
	}

	/**
	 * Returns the comparison number.
	 * 
	 * @return int
	 */
	public int toComparison() {
		return comparison;
	}

}
