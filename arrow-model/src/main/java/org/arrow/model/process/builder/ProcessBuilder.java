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
public final class ProcessBuilder {

    private final Process process;

    private ProcessBuilder() {
        this.process = new Process();
        this.process.setId(process.hashCode() + "");
        this.process.setTimestamp(new Date());
    }

    /**
     * Returns a new {@link ProcessBuilder} instance.
     *
     * @return ProcessBuilder
     */
    public static ProcessBuilder builder() {
        return new ProcessBuilder();
    }

    /**
     * Sets the id property.
     *
     * @param id the id property value
     * @return ProcessBuilder
     */
    public ProcessBuilder id(String id) {
        this.process.setId(id);
        return this;
    }

    /**
     * Sets the startEvents property.
     *
     * @param startEvents the startEvents property value
     * @return ProcessBuilder
     */
    public ProcessBuilder startEvents(Set<StartEvent> startEvents) {
        this.process.setStartEvents(startEvents);
        return this;
    }

    /**
     * Sets the startEvents property.
     *
     * @param startEvents the startEvents property value
     * @return ProcessBuilder
     */
    public ProcessBuilder startEvents(StartEvent... startEvents) {
        Set<StartEvent> set = new HashSet<>(Arrays.asList(startEvents));
        this.process.setStartEvents(set);
        return this;
    }

    /**
     * Sets the subProcesses property value.
     *
     * @param subProcesses the subProcesses to set
     * @return ProcessBuilder
     */
    public ProcessBuilder subProcesses(SubProcess... subProcesses) {
        Set<SubProcess> set = new HashSet<>(Arrays.asList(subProcesses));
        this.process.setSubProcesses(set);
        return this;
    }

    /**
     * Returns the {@link Process} instance.
     *
     * @return Process
     */
    public Process build() {
        return this.process;
    }

}
