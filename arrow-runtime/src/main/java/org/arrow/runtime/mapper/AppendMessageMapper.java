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
import org.neo4j.helpers.collection.IteratorUtil;
import org.springframework.util.Assert;
import org.arrow.runtime.message.EventMessage;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Mapper implementation which is used to append messages to the retrieved messages from the future call.
 *
 * @since 1.0.0
 * @author christian.weber
 */
public class AppendMessageMapper extends Mapper<Iterable<EventMessage>, Iterable<EventMessage>> {

    private final EventMessage[] messages;

    public AppendMessageMapper(EventMessage...messages) {
        this.messages = messages;
        Assert.notNull(messages);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<EventMessage> apply(Iterable<EventMessage> parameter) {
        Iterator<EventMessage> iterator = parameter.iterator();
        List<EventMessage> list = IteratorUtil.asList(iterator);

        Collections.addAll(list, messages);
        return list;
    }

}
