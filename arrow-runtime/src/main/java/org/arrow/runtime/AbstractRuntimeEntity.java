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

import org.apache.log4j.Logger;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.support.index.IndexType;

/**
 * Abstract {@link RuntimeEntity} implementation.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public abstract class AbstractRuntimeEntity implements RuntimeEntity {

	/** The node id. */
	@GraphId
    @SuppressWarnings("unused")
	private Long nodeId;

	/** The id. */
//    @Indexed(indexName = "ENTITY_ID", unique = true, indexType = IndexType.FULLTEXT)
	private String id;

	/** The name. */
	private String name;

	/**
	 * {@inheritDoc}
	 */
	public Long getNodeId() {
		return nodeId;
	}

    /**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Logs the given message at info level.
	 *
	 * @param msg the msg
	 */
	protected void info(String msg) {
		Logger.getLogger(getClass()).info(msg);
	}

}
