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

/**
 * {@link JavaDelegate} implementation used to pass through.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class PassThroughJavaDelegate implements JavaDelegate {
	
	@Override
	public void execute(Execution execution) {
		// do nothing
	}
	
}