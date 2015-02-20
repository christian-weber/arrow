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

package org.arrow.service.engine.concurrent.dispatch.recover;

import akka.dispatch.Recover;
import org.arrow.runtime.logger.LoggerFacade;
import org.arrow.runtime.message.EventMessage;

import java.util.ArrayList;

/**
 * Definition class for Recover implementations.
 *
 * @since 1.0.0
 * @author christian.weber
 */
public final class Recovers {

    private final static transient LoggerFacade LOGGER = new LoggerFacade(Recovers.class);

    /**
     * Returns a recover instance which logs the error and returns an empty iterable instance.
     *
     * @return Recover
     */
    public static Recover<Iterable<EventMessage>> logAndReturnEmptyIterable() {
        return new Recover<Iterable<EventMessage>>() {
            @Override
            public Iterable<EventMessage> recover(Throwable throwable) throws Throwable {
                throwable.printStackTrace();
                LOGGER.error(throwable);
                return new ArrayList<>();
            }
        };
    }

    /**
     * Returns a recover instance which logs the error and returns an empty iterable instance.
     *
     * @return Recover
     */
    public static Recover<Iterable<EventMessage>> logAndReturnNull() {
        return new Recover<Iterable<EventMessage>>() {
            @Override
            public Iterable<EventMessage> recover(Throwable throwable) throws Throwable {
                LOGGER.error(throwable);
                return null;
            }
        };
    }


    /**
     * Returns a recover instance which logs and throws the error.
     *
     * @return Recover
     */
    public static Recover<Iterable<EventMessage>> logAndThrow() {
        return new Recover<Iterable<EventMessage>>() {
            @Override
            public Iterable<EventMessage> recover(Throwable throwable) throws Throwable {
                throwable.printStackTrace();
                LOGGER.error(throwable);
                throw throwable;
            }
        };
    }

}
