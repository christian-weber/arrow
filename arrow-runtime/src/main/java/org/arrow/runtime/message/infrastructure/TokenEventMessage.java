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

package org.arrow.runtime.message.infrastructure;

import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.ProcessInstance;

/**
 * Created by christian.weber on 05.11.2014.
 */
public class TokenEventMessage implements InfrastructureEventMessage {

    private final transient Execution execution;
    private final TokenAction tokenAction;

    public TokenEventMessage(Execution execution, TokenAction tokenAction) {
        this.execution = execution;
        this.tokenAction = tokenAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessInstance getProcessInstance() {
        return execution.getProcessInstance();
    }

    public TokenAction getTokenAction() {
        return tokenAction;
    }

    public enum TokenAction {

        PRODUCE, CONSUME, TERMINATE

    }

    @Override
    public String toString() {
        return "TokenEventMessage " +
                " tokenAction=" + tokenAction + " [" + execution.getEntity().getId() + "]";
    }
}
