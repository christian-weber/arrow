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

package org.arrow.model.process.builder;

import org.arrow.model.event.startevent.StartEvent;
import org.arrow.model.process.Process;
import org.arrow.model.process.SubProcess;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by christian.weber on 27.07.2014.
 */
public final class SubProcessBuilder {

    private final SubProcess process;

    private SubProcessBuilder() {
        this.process = new SubProcess();
        this.process.setId(process.hashCode() + "");
        this.process.setTimestamp(new Date());
    }

    /**
     * Returns a new {@link SubProcessBuilder} instance.
     *
     * @return SubProcessBuilder
     */
    public static SubProcessBuilder builder() {
        return new SubProcessBuilder();
    }

    /**
     * Sets the id property.
     *
     * @param id the id property value
     * @return SubProcessBuilder
     */
    public SubProcessBuilder id(String id) {
        this.process.setId(id);
        return this;
    }

    /**
     * Sets the startEvents property.
     *
     * @param startEvents the startEvents property value
     * @return SubProcessBuilder
     */
    public SubProcessBuilder startEvents(Set<StartEvent> startEvents) {
        this.process.setStartEvents(startEvents);
        return this;
    }

    /**
     * Sets the startEvents property.
     *
     * @param startEvents the startEvents property value
     * @return SubProcessBuilder
     */
    public SubProcessBuilder startEvents(StartEvent... startEvents) {
        Set<StartEvent> set = new HashSet<>(Arrays.asList(startEvents));
        this.process.setStartEvents(set);
        return this;
    }

    /**
     * Sets the triggeredByEvent property.
     *
     * @param triggeredByEvent the property to set
     * @return SubProcessBuilder
     */
    public SubProcessBuilder triggeredByEvent(boolean triggeredByEvent) {
        this.process.setTriggeredByEvent(triggeredByEvent);
        return this;
    }

    /**
     * Returns the {@link SubProcess} instance.
     *
     * @return SubProcess
     */
    public SubProcess build() {
        return this.process;
    }

}
