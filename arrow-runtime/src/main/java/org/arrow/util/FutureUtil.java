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

package org.arrow.util;

import akka.dispatch.Futures;
import org.arrow.runtime.mapper.IterableOfIterable2IterableMessageMapper;
import org.arrow.runtime.message.EventMessage;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.Future;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Utility class for Akka future handling. Offers convenient methods
 * for building future compositions easier.
 *
 * @since 1.0.0
 * @author christian.weber
 */
public final class FutureUtil {

    private FutureUtil() {
        super();
    }

    public static Iterable<EventMessage> iterableOf(EventMessage...messages) {
        return Arrays.asList(messages);
    }

    @SafeVarargs
    public static Future<Iterable<EventMessage>> sequenceResult(ExecutionContextExecutor ec, Future<Iterable<EventMessage>>... futures) {
        Iterable<Future<Iterable<EventMessage>>> iterable = Arrays.asList(futures);
        Future<Iterable<Iterable<EventMessage>>> result = Futures.sequence(iterable, ec);
        return result.map(IterableOfIterable2IterableMessageMapper.INSTANCE, ec);
    }

    public static Future<Iterable<EventMessage>> result(EventMessage...messages) {
        return Futures.successful(iterableOf(messages));
    }

    public static Future<Iterable<EventMessage>> result(Collection<EventMessage> messages) {
        return Futures.successful(messages);
    }

}
