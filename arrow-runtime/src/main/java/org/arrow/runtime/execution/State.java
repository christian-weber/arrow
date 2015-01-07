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

package org.arrow.runtime.execution;

/**
 * Enumeration class which defines the various BPMN entity states.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public enum State {

	SUCCESS, SUSPEND, FAILURE, WAITING, RUNNING, JOINING, IGNORED;

	/**
	 * Indicates if the state is successful.
	 * 
	 * @return boolean
	 */
	public boolean isSuccess() {
		return SUCCESS.compareTo(this) == 0;
	}

	/**
	 * Indicates if the state is suspend.
	 *
	 * @return boolean
	 */
	public boolean isSuspend() {
		return SUSPEND.compareTo(this) == 0;
	}

    /**
     * Indicates if the state is failure.
     *
     * @return boolean
     */
    public boolean isFailure() {
        return FAILURE.compareTo(this) == 0;
    }

    /**
     * Indicates if the state is wait.
     *
     * @return boolean
     */
    public boolean isWait() {
        return WAITING.compareTo(this) == 0;
    }

    /**
     * Indicates if the state is running.
     *
     * @return boolean
     */
    public boolean isRunning() {
        return RUNNING.compareTo(this) == 0;
    }

}
