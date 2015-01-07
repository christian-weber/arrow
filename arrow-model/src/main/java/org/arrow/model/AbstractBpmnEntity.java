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

package org.arrow.model;

import org.apache.log4j.Logger;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.support.index.IndexType;

import java.util.Collections;
import java.util.Set;

/**
 * Abstract {@link BpmnEntity} implementation.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public abstract class AbstractBpmnEntity implements BpmnEntity {

    /**
     * The node id.
     */
    @GraphId
    private Long nodeId;

    /**
     * The id.
     */
    @Indexed(indexName = "entityId", indexType = IndexType.FULLTEXT)
    private String id;

    /**
     * The name.
     */
    private String name;

    /**
     * The version.
     */
    private Long version;

    /**
     * {@inheritDoc}
     */
    public Long getNodeId() {
        return nodeId;
    }

    /**
     * Sets the node id.
     *
     * @param nodeId the new node id
     */
    @Deprecated
    @SuppressWarnings("unused")
    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
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
     * {@inheritDoc}
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version the new version
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Convenience method which returns either an empty set if the given set is null or the set itself.
     *
     * @param set the set instance
     * @param <T> the generic type
     * @return Set
     */
    protected static <T> Set<T> emptyIfNull(Set<T> set) {
        return set == null ? Collections.<T>emptySet() : set;
    }

    /**
     * Logs the given message at info level.
     *
     * @param msg the msg
     */
    protected void info(String msg) {
        Logger.getLogger(getClass()).info(msg);
    }

    /**
     * Logs the given message at warn level.
     *
     * @param msg the msg
     */
    protected void warn(String msg) {
        Logger.getLogger(getClass()).warn(msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + id + "]";
    }
}
