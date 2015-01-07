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

package org.arrow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.arrow.runtime.RuntimeService;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.service.ExecutionUserService;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.impl.DefaultFinishEventMessage;

/**
 * Default {@link ExecutionUserService} implementation.
 *
 * @since 1.0.0
 * @author christian.weber
 */
@Service
@Transactional
public class DefaultExecutionUserService implements ExecutionUserService {

    @Autowired
    private RuntimeService runtimeService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void finish(Execution execution) {
        EventMessage eventMessage = new DefaultFinishEventMessage(execution);
        runtimeService.publishEventMessage(eventMessage);
    }
}
