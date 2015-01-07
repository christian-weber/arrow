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

package org.arrow.data.neo4j.postprocess;

import org.arrow.model.definition.EventDefinition;
import org.arrow.model.definition.message.MessageEventDefinition;
import org.arrow.model.event.startevent.StartEvent;
import org.arrow.model.event.startevent.impl.MultipleStartEvent;

import java.util.function.Predicate;

/**
 * {@link QueryPostProcessor} implementation used to set the started by event
 * definition of {@code MultipleStartEvent} instances.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class MultipleStartEventStartedByMessage implements QueryPostProcessor<StartEvent> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StartEvent postProcess(StartEvent result) {

		if (result instanceof MultipleStartEvent) {
			MultipleStartEvent mse = (MultipleStartEvent) result;

			Predicate<? super EventDefinition> filter = object -> object instanceof MessageEventDefinition;
			mse.getEventDefinitions().stream().filter(filter).forEach(mse::setStartedBy);
		}
		return result;
	}

}
