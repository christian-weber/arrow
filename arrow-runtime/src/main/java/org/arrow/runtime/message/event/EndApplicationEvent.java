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

package org.arrow.runtime.message.event;

import org.springframework.context.ApplicationEvent;

/**
 * {@link org.springframework.context.ApplicationEvent} instance for
 * {@link org.arrow.runtime.message.FinishEventMessage} event messages.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class EndApplicationEvent extends ApplicationEvent {

    public EndApplicationEvent(String processInstanceId) {
        super(processInstanceId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSource() {
        return (String) super.getSource();
    }

}
