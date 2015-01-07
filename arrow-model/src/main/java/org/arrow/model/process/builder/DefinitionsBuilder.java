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

import org.arrow.model.process.Definitions;
import org.arrow.model.process.Process;
import org.arrow.model.process.event.Message;
import org.arrow.model.process.event.Signal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by christian.weber on 27.07.2014.
 */
public final class DefinitionsBuilder {

    private final Definitions definitions;

    private DefinitionsBuilder() {
        this.definitions = new Definitions();
        this.definitions.setId(definitions.hashCode() + "");
    }

    /**
     * Returns a new {@link DefinitionsBuilder} instance.
     *
     * @return SignalStartEventBuilder
     */
    public static DefinitionsBuilder builder() {
        return new DefinitionsBuilder();
    }

    /**
     * Sets the processes property
     *
     * @param processes the processes property value
     * @return DefinitionsBuilder
     */
    public DefinitionsBuilder processes(Process... processes) {
        Set<Process> set = new HashSet<>(Arrays.asList(processes));
        this.definitions.setProcesses(set);
        return this;
    }

    /**
     * Adds a signal definition.
     *
     * @param signalRef the signal reference value
     * @return DefinitionsBuilder
     */
    public DefinitionsBuilder signal(String signalRef) {
        Signal signal = new Signal();
        signal.setId(signalRef);
        signal.setName(signalRef);

        if (definitions.getSignals() == null) {
            this.definitions.setSignals(new HashSet<Signal>());
        }
        this.definitions.getSignals().add(signal);

        return this;
    }

    /**
     * Adds a message definition.
     *
     * @param messageRef the signal reference value
     * @return DefinitionsBuilder
     */
    public DefinitionsBuilder message(String messageRef) {
        Message message = new Message();
        message.setId(messageRef);
        message.setName(messageRef);

        if (definitions.getSignals() == null) {
            this.definitions.setMessages(new HashSet<Message>());
        }
        this.definitions.getMessages().add(message);

        return this;
    }

    /**
     * Returns the {@link Definitions} instance.
     *
     * @return Process
     */
    public Definitions build() {
        return this.definitions;
    }

}
