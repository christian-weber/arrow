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

/**
 * Classes which implements this interface are able to post process
 * {@code GraphRepository} query results.
 * 
 * @author christian.weber
 * @since 1.0.0
 * 
 * @param <TYPE> the type
 */
public interface QueryPostProcessor<TYPE> {

	/**
	 * Post processes the annotated {code GraphRepository} query result.
	 * 
	 * @param result the result value
	 * @return TYPE
	 */
	TYPE postProcess(TYPE result);

}