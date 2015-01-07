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

package org.arrow.runtime.message.impl;

import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.message.AbstractFinishEventMessage;

/**
 * Continuing {@link org.arrow.runtime.message.FinishEventMessage} implementation for
 * {@link org.arrow.runtime.message.EventMessage} instances.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class ContinuingFinishEventMessage extends AbstractFinishEventMessage {

    public ContinuingFinishEventMessage(Execution execution) {
        super(execution);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean continueNode() {
        return true;
    }
}
