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

package org.arrow.data.neo4j.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.data.neo4j.lifecycle.BeforeSaveEvent;
import org.springframework.stereotype.Component;
import org.arrow.runtime.TimestampAware;

import java.util.Date;

/**
 * {@link ApplicationListener} implementation used to set the timestamp
 * information before save.
 *
 * @author christian.weber
 * @since 1.0.0
 */
@Component
public class SetTimestampBeforeSaveApplicationListener implements ApplicationListener<BeforeSaveEvent<TimestampAware>> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onApplicationEvent(BeforeSaveEvent<TimestampAware> event) {

        if (!isTimestampAware(event.getEntity())) {
            return;
        }

        TimestampAware aware = event.getEntity();
        aware.setTimestamp(new Date());

    }

    /**
     * Indicates if the given object is a instance of TimestampAware.
     *
     * @param obj the object to verify
     * @return boolean
     */
    private boolean isTimestampAware(Object obj) {
        return obj instanceof TimestampAware;
    }

}
