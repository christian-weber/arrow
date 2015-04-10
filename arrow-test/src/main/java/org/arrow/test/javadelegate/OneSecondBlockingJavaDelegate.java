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

package org.arrow.test.javadelegate;

import org.arrow.runtime.api.task.JavaDelegate;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.message.EventMessage;
import org.arrow.util.FutureUtil;
import scala.concurrent.Future;

/**
 * {@link org.arrow.runtime.api.task.JavaDelegate} implementation used to simulate a 3 second blocking behavior.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class OneSecondBlockingJavaDelegate implements JavaDelegate {
	
	@Override
	public Future<Iterable<EventMessage>> execute(Execution execution) {
		try {
			Thread.sleep(3000);
			return FutureUtil.result();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
}