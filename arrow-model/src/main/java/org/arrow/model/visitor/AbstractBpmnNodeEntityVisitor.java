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

package org.arrow.model.visitor;

import org.arrow.model.event.boundary.BoundaryEvent;
import org.arrow.model.event.endevent.EndEvent;
import org.arrow.model.event.intermediate.catching.IntermediateCatchEvent;
import org.arrow.model.event.intermediate.throwing.IntermediateThrowEvent;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.model.gateway.Gateway;
import org.arrow.model.process.SubProcess;
import org.arrow.model.task.Task;

/**
 * Abstract {@link org.arrow.model.visitor.BpmnNodeEntityVisitor} class.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public abstract class AbstractBpmnNodeEntityVisitor implements BpmnNodeEntityVisitor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStartEvent(StartEvent startEvent) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitEndEvent(EndEvent endEvent) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitIntermediateThrowEvent(IntermediateThrowEvent ite) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitIntermediateCatchEvent(IntermediateCatchEvent ice) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitBoundaryEvent(BoundaryEvent be) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitTask(Task task) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitSubProcess(SubProcess subProcess) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitGateway(Gateway gateway) {
        // do nothing
    }

}
