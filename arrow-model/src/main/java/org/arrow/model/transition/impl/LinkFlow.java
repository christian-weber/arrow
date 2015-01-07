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

package org.arrow.model.transition.impl;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;
import org.arrow.model.AbstractBpmnRelationshipEntity;
import org.arrow.model.BpmnNodeEntity;
import org.arrow.model.transition.Flow;
import org.arrow.model.visitor.BpmnRelationshipEntityVisitor;

@RelationshipEntity(type = "LINK_FLOW")
public class LinkFlow extends AbstractBpmnRelationshipEntity implements Flow {

    /** The source ref. */
    @Fetch
    @StartNode
    private BpmnNodeEntity sourceRef;

    /** The target ref. */
    @Fetch
    @EndNode
    private BpmnNodeEntity targetRef;

    /**
     * {@inheritDoc}
     */
    @Override
    public BpmnNodeEntity getSourceRef() {
        return sourceRef;
    }

    /**
     * {@inheritDoc}
     */
    public void setSourceRef(BpmnNodeEntity sourceRef) {
        this.sourceRef = sourceRef;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BpmnNodeEntity getTargetRef() {
        return targetRef;
    }

    /**
     * {@inheritDoc}
     */
    public void setTargetRef(BpmnNodeEntity targetRef) {
        this.targetRef = targetRef;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFinished() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(BpmnRelationshipEntityVisitor visitor) {
        visitor.visitLinkFlow(this);
    }

    @Override
    public void enable() {
        // do nothing
    }
}
