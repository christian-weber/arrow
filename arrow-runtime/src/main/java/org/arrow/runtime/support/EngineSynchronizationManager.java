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
import org.springframework.core.NamedThreadLocal;

import java.util.Map;

/**
 * This class is used to synchronize thread local variables of the current thread.
 *
 * @since 1.0.0
 * @author christian.weber
 */
public class EngineSynchronizationManager {

    private static final ThreadLocal<String> processInstanceId =
            new NamedThreadLocal<>("Process Instance Id");

    private static final ThreadLocal<ActorRef> currentActor =
            new NamedThreadLocal<>("Current Actor");

    private static final ThreadLocal<Map<String, Object>> processScope =
            new NamedThreadLocal<>("Process Scope");

    /**
     * Returns the process instance id from the current thread.
     *
     * @return String
     */
    public static String getProcessInstanceId() {
        return processInstanceId.get();
    }

    /**
     * Sets the process instance id for the current thread.
     *
     * @param processInstanceId the process instance id
     */
    public static void setProcessInstanceId(String processInstanceId) {
        EngineSynchronizationManager.processInstanceId.set(processInstanceId);
    }

    /**
     * Returns the current actor instance from the current thread.
     *
     * @return ActorRef
     */
    public static ActorRef getCurrentActor() {
        return currentActor.get();
    }

    /**
     * Sets the current actor instance id for the current thread.
     *
     * @param currentActor the current actor reference instance
     */
    public static void setCurrentActor(ActorRef currentActor) {
        EngineSynchronizationManager.currentActor.set(currentActor);
    }

    /**
     * Returns the current process scope map from the current thread.
     *
     * @return Map
     */
    public static Map<String, Object> getProcessScope() {
        return processScope.get();
    }

    /**
     * Sets the current process scope map for the current thread.
     *
     * @param processScope the current process scope
     */
    public static void setProcessScope(Map<String, Object> processScope) {
        EngineSynchronizationManager.processScope.set(processScope);
    }

}
