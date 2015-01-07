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

import org.arrow.runtime.execution.ProcessInstance;
import scala.concurrent.Future;

/**
 * Created by christian.weber on 14.11.2014.
 */
public class FutureEventMessage implements InfrastructureEventMessage {

    private final Future<?> future;
    private final ProcessInstance pi;

    public FutureEventMessage(Future<?> future, ProcessInstance pi) {
        this.future = future;
        this.pi = pi;
    }

    @Override
    public ProcessInstance getProcessInstance() {
        return pi;
    }

    public Future<?> getFuture() {
        return future;
    }

}
