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

package org.arrow.service.microservice;

import org.arrow.runtime.message.EventMessage;
import scala.concurrent.Future;

/**
 * Event message service definition.
 *
 * @param <REQUEST> the request type
 * @author christian.weber
 * @since 1.0.0
 */
public interface EventMessageService<REQUEST> {

    /**
     * Returns the event messages as a future object.
     *
     * @param request the request instance
     * @return Future
     */
    Future<Iterable<EventMessage>> getEventMessages(REQUEST request);

}
