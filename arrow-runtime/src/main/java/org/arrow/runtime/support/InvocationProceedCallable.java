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

import org.aopalliance.intercept.MethodInvocation;
import org.arrow.runtime.message.EventMessage;
import scala.concurrent.Future;

import java.util.concurrent.Callable;

/**
 * Callable implementation designed to execute a method invocation.
 *
 * @since 1.0.0
 * @author christian.weber
 */
public class InvocationProceedCallable implements Callable<Future<Iterable<EventMessage>>> {

    private final MethodInvocation invocation;

    public InvocationProceedCallable(MethodInvocation invocation) {
        this.invocation = invocation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Future<Iterable<EventMessage>> call() {
        try {
            return (Future<Iterable<EventMessage>>) invocation.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
