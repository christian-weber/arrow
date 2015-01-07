/*
 * Copyright 2014 Christian Weber
 *
 * This file is build on Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.arrow.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.arrow.runtime.RuntimeService;
import org.arrow.runtime.execution.Execution;
import org.arrow.runtime.execution.service.ExecutionUserService;
import org.arrow.runtime.message.EventMessage;
import org.arrow.runtime.message.impl.DefaultFinishEventMessage;

/**
 * Neo4j {@link ExecutionUserService} implementation.
 *
 * @since 1.0.0
 * @author christian.weber
 */
@Service
@Transactional
public class Neo4jExecutionUserService implements ExecutionUserService {

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
