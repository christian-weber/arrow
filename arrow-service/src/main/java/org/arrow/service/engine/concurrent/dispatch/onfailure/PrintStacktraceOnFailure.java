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

package org.arrow.service.engine.concurrent.dispatch.onfailure;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.arrow.runtime.execution.ProcessInstance;

import akka.dispatch.OnFailure;

/**
 * {@link OnFailure} implementation used to print the stacktrace in case of an error.
 *
 * @since 1.0.0
 * @author christian.weber
 */
public class PrintStacktraceOnFailure extends OnFailure {

	private final ProcessInstance processInstance;

	public PrintStacktraceOnFailure() {
		this.processInstance = null;
	}

	public PrintStacktraceOnFailure(ProcessInstance processInstance) {
		Assert.notNull(processInstance);
		this.processInstance = processInstance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onFailure(Throwable ex) throws Throwable {
        Logger.getLogger(getClass()).error(ex);
		ex.printStackTrace();

        if (processInstance != null) {
			synchronized (processInstance) {
				this.processInstance.setFinished(true);
				this.processInstance.notifyAll();
			}
		}
	}

}
