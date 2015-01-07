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

package org.arrow.service.engine.concurrent.dispatch.onsuccess;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.arrow.runtime.execution.ProcessInstance;

import akka.dispatch.OnSuccess;

/**
 * {@link OnSuccess} implementation which is used to release the lock and to
 * notify all waiting threads in order to release the blocking state.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class NotifyOnSuccess extends OnSuccess<Iterable<Object>> {

    private final static Logger LOGGER = Logger.getLogger(NotifyOnSuccess.class);

	private final ProcessInstance processInstance;

	public NotifyOnSuccess(ProcessInstance processInstance) {
		Assert.notNull(processInstance);
		this.processInstance = processInstance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSuccess(Iterable<Object> nodes) {
        this.processInstance.setFinished(true);
        synchronized (processInstance) {
            this.processInstance.setFinished(true);
            this.processInstance.notifyAll();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("NOTIFY process instance " + processInstance.getId());
            }
		}
	}

}