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

import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Assert;
import org.junit.Test;

public class CompareToBuilderTest {
	
	@Test
	public void compareOfStringArgumentsShouldReturnOne() {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append("str2", "str1");
		
		Assert.assertThat(builder.toComparison(), equalTo(1));
	}

	@Test
	public void compareOfStringArgumentsShouldReturnMinusOne() {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append("str1", "str2");
		
		Assert.assertThat(builder.toComparison(), equalTo(-1));
	}
	
	@Test
	public void compareOfStringArgumentsShouldReturnZero() {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append("str", "str");
		
		Assert.assertThat(builder.toComparison(), equalTo(0));
	}
	
	@Test
	public void compareOfComparableArgumentsShouldReturnOne() {
		CompareToBuilder builder = new CompareToBuilder();
		
		ComparableBean bean1 = new ComparableBean(2);
		ComparableBean bean2 = new ComparableBean(1);

		builder.append(bean1, bean2);

		Assert.assertThat(builder.toComparison(), equalTo(1));
	}
	
	@Test
	public void compareOfComparableArgumentsShouldReturnMinusOne() {
		CompareToBuilder builder = new CompareToBuilder();
		
		ComparableBean bean1 = new ComparableBean(1);
		ComparableBean bean2 = new ComparableBean(2);

		builder.append(bean1, bean2);
		
		Assert.assertThat(builder.toComparison(), equalTo(-1));
	}
	
	@Test
	public void compareOfComparableArgumentsShouldReturnZero() {
		CompareToBuilder builder = new CompareToBuilder();

		ComparableBean bean1 = new ComparableBean(1);
		ComparableBean bean2 = new ComparableBean(1);

		builder.append(bean1, bean2);
		
		Assert.assertThat(builder.toComparison(), equalTo(0));
	}
	
	private class ComparableBean implements Comparable<ComparableBean> {

		private final Integer value;
		
		public ComparableBean(int value) {
			this.value = value;
		}
		
		public Integer getValue() {
			return value;
		}
		
		@Override
		public int compareTo(ComparableBean o) {
			return value.compareTo(o.getValue());
		}

		
	}
	
}
