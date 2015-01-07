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

package org.arrow.model.definition.message;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.arrow.model.definition.AbstractEventDefinition;
import org.arrow.model.definition.EventDefinition;

/**
 * {@link EventDefinition} implementation which represents a message event
 * definition.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
@NodeEntity
@TypeAlias("MessageEventDefinition")
public class MessageEventDefinition extends AbstractEventDefinition {

	/** The signal ref. */
	private String messageRef;

	/**
	 * Gets the message ref.
	 * 
	 * @return the message ref
	 */
	public String getMessageRef() {
		return messageRef;
	}

	/**
	 * Sets the message ref.
	 * 
	 * @param messageRef
	 *            the new message ref
	 */
	public void setMessageRef(String messageRef) {
		this.messageRef = messageRef;
	}

}
