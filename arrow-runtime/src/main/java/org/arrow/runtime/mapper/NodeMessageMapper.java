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

package org.arrow.runtime.mapper;

import akka.dispatch.Mapper;
import org.apache.log4j.Logger;
import org.arrow.runtime.message.EventMessage;
import scala.concurrent.Future;

import java.util.HashSet;
import java.util.Set;

/**
 * {@link akka.dispatch.Mapper} implementation used to reduce a iterable of iterable of event message instance
 * to a instance of iterable of event messages.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public final class NodeMessageMapper extends Mapper<Iterable<Future<Iterable<EventMessage>>>, Future<Iterable<EventMessage>>> {

    public static final NodeMessageMapper INSTANCE = new NodeMessageMapper();

    public NodeMessageMapper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Iterable<EventMessage>> apply(Iterable<Future<Iterable<EventMessage>>> parameter) {
        Logger.getLogger(getClass()).info("MAP");

        Set<Future<Iterable<EventMessage>>> messages = new HashSet<>();
        for (Future<Iterable<EventMessage>> future : parameter) {
            messages.add(future);
        }

        return null;
    }

}