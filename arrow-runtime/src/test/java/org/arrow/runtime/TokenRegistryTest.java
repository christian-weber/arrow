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

package org.arrow.runtime;

import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Assert;
import org.junit.Test;

public class TokenRegistryTest {

	@Test
	public void produceTokenShouldIncrementCount() {
		TokenRegistry registry = new TokenRegistry();
		registry.produce();
		registry.produce();
		registry.produce();
		
		Assert.assertThat(4, equalTo(registry.getTokens()));
		Assert.assertThat(false, equalTo(registry.lastToken()));
	}
	
	@Test
	public void consumeShouldDecreaseCount() {
		TokenRegistry registry = new TokenRegistry();
		
		// produce
		registry.produce();
		registry.produce();
		registry.produce();
		
		// consume
		registry.consume();
		registry.consume();
		registry.consume();

		Assert.assertThat(1, equalTo(registry.getTokens()));
		Assert.assertThat(true, equalTo(registry.lastToken()));
	}
	
	@Test
	public void terminateShouldSetCountToZero() {
		TokenRegistry registry = new TokenRegistry();
		
		// produce
		registry.produce();
		registry.produce();
		registry.produce();
		
		registry.terminate();
		
		Assert.assertThat(0, equalTo(registry.getTokens()));
		Assert.assertThat(false, equalTo(registry.lastToken()));
	}
	
}
