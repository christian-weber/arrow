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

package org.arrow.model.definition.link;

import org.arrow.model.definition.AbstractEventDefinition;
import org.arrow.model.definition.EventDefinition;

/**
 * {@link EventDefinition} implementation which represents a link event
 * definition.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class LinkEventDefinition extends AbstractEventDefinition {

	/** The link name. */
	private String linkName;

	/**
	 * Gets the link name.
	 * 
	 * @return the link name
	 */
	public String getLinkName() {
		return linkName;
	}

	/**
	 * Sets the link name.
	 * 
	 * @param linkName
	 *            the new link name
	 */
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

}
