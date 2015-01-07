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

package org.arrow.runtime.support;

import akka.actor.ActorRef;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * This callable decorator implementation is used to export all thread local values
 * from one thread to an asynchronous thread by setting the value in the #call method.
 *
 * @param <T>
 * @author christian.weber
 * @since 1.0.0
 */
public class EngineSynchronizationManagerCallableDecorator<T> implements Callable<T> {

    private final Callable<T> callable;
    private final String piId;

    private final Map<String, Object> scopeMap;
    private final ActorRef actorRef;

    public EngineSynchronizationManagerCallableDecorator(Map<String, Object> scopeMap, Callable<T> callable, ActorRef actorRef) {
        this.callable = callable;
        this.scopeMap = scopeMap;
        this.actorRef = actorRef;
        this.piId = EngineSynchronizationManager.getProcessInstanceId();
    }

    public EngineSynchronizationManagerCallableDecorator(Callable<T> callable) {
        this(Collections.<String, Object>emptyMap(), callable, EngineSynchronizationManager.getCurrentActor());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T call() throws Exception {
        try {
            // set the current process scope
            EngineSynchronizationManager.setProcessScope(scopeMap);
            // set the current actor reference
            EngineSynchronizationManager.setCurrentActor(actorRef);
            // set the current process instance id
            EngineSynchronizationManager.setProcessInstanceId(piId);

            return callable.call();
        } finally {
            // unset the current process instance id
            EngineSynchronizationManager.setProcessInstanceId(null);
            // unset the current process scope
            EngineSynchronizationManager.setProcessScope(null);
            // unset the current actor reference
            EngineSynchronizationManager.setCurrentActor(null);
        }
    }
}
