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

package org.arrow.runtime.logger;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This facade implementation hides the underlying logging mechanism in order to exchange them easily.
 *
 * @since 1.0.0
 * @author christian.weber
 */
public class LoggerFacade {

    private final transient Logger LOGGER;

    public LoggerFacade(Class<?> cls) {
        LOGGER = Logger.getLogger(cls);
    }

    /**
     * Logs the given message at error level.
     *
     * @param message the message
     * @param args the message arguments
     */
    public void error(Object message, Object... args) {
        LOGGER.error(String.format(message.toString(), args));
    }

    /**
     * Logs the given message at error level.
     *
     * @param throwable the throwable instance to log
     * @param message the message
     * @param args the message arguments
     */
    public void error(Throwable throwable, Object message, Object... args) {
        LOGGER.error(String.format(message.toString(), args), throwable);
    }

    /**
     * Logs the given message at error level.
     *
     * @param throwable the throwable instance to log
     */
    public void error(Throwable throwable) {
        LOGGER.error(throwable);
    }

    /**
     * Logs the given message at debug level.
     *
     * @param message the message
     * @param args the message arguments
     */
    public void debug(Object message, Object... args) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format(message.toString(), args));
        }
    }

    /**
     * Logs the given message at info level.
     *
     * @param message the message
     * @param args the message arguments
     */
    public void info(Object message, Object... args) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format(message.toString(), args));
        }
    }

    /**
     * Logs the given message at warn level.
     *
     * @param message the message
     * @param args the message arguments
     */
    public void warn(Object message, Object... args) {
        if (LOGGER.isEnabledFor(Level.WARN)) {
            LOGGER.warn(String.format(message.toString(), args));
        }
    }

    /**
     * Indicates if the info log level is enabled.
     *
     * @return boolean
     */
    public boolean isInfoEnabled() {
        return LOGGER.isInfoEnabled();
    }

    /**
     * Indicates if the debug log level is enabled.
     *
     * @return boolean
     */
    public boolean isDebugEnabled() {
        return LOGGER.isDebugEnabled();
    }

    /**
     * Indicates if the warn log level is enabled.
     *
     * @return boolean
     */
    public boolean isWarnEnabled() {
        return LOGGER.isDebugEnabled();
    }

}
