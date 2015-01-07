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

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.arrow.runtime.AbstractRuntimeEntity;

/**
 * The execution group runtime entity.
 *
 * @since 1.0.0
 * @author christian.weber
 */
@NodeEntity
@TypeAlias("ExecutionGroup")
public class ExecutionGroup extends AbstractRuntimeEntity {

    private boolean finished;

    /**
     * Indicates if the execution group is finished.
     *
     * @return boolean
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Sets the finished property.
     *
     * @param finished the finished value
     */
    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
