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

package org.arrow.runtime.message;

import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.message.impl.DefaultFinishEventMessage;
import org.arrow.runtime.message.impl.EndEventMessage;
import org.arrow.runtime.message.infrastructure.TokenEventMessage;

public final class Messages {

    private Messages() {
        super();
    }

    public static EventMessage finishAndContinue(Execution execution) {
        return new DefaultFinishEventMessage(execution) {
            @Override
            public boolean continueNode() {
                return true;
            }
        };
    }

    public static TokenEventMessage terminateToken(Execution execution) {
        return new TokenEventMessage(execution, TokenEventMessage.TokenAction.TERMINATE);
    }

    public static EndEventMessage forceEndEvent(Execution execution) {
        EndEventMessage msg = new EndEventMessage(execution);
        msg.setForce(true);

        return msg;
    }

}
