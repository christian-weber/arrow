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

import org.arrow.runtime.logger.LoggerFacade;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This token registry class is used to track the amount of active tokens during
 * the execution of an BPMN process. A process could only be finished if all
 * active tokens are consumed.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class TokenRegistry {

    private final transient static LoggerFacade LOGGER = new LoggerFacade(TokenRegistry.class);

	private AtomicInteger tokens = new AtomicInteger(1);

	/**
	 * Consumes an active token. Decrements the token count by one.
	 */
	public void consume() {
        LOGGER.info("consume: " + tokens.decrementAndGet() + " " + this.hashCode());
	}

	/**
	 * Produces a new token. Increments the token count by one.
	 */
	public void produce() {
		LOGGER.info("produce: " + tokens.incrementAndGet() + " " + this.hashCode());
	}

	/**
	 * Returns the active token count.
	 * 
	 * @return int
	 */
	public int getTokens() {
		return tokens.get();
	}

	/**
	 * Indicates that only one token is present.
	 * 
	 * @return boolean
	 */
	public boolean lastToken() {
		return tokens.get() == 1;
	}
	
	/**
	 * Indicates whether a token is present.
	 * 
	 * @return boolean
	 */
	public boolean hasToken() {
		LOGGER.info("HAS TOKEN: %s", tokens.get());
        return tokens.get() > 0;
	}

	/**
	 * Terminates the tokens, sets the counter to 1.
	 */
	public void terminate() {
		this.tokens.set(0);
	}

}
