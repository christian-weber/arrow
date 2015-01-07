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

package org.arrow.data.neo4j.query.cypher.where;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Simple {@link Where} implementation which only returns a predefined string
 * text.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class SimpleWhere implements Where {

	private final String cypher;

	public SimpleWhere(String cypher) {
		Assert.notNull(cypher);

		cypher = StringUtils.replace(cypher, "where", "");
		cypher = StringUtils.trimWhitespace(cypher);

		this.cypher = cypher;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toCypher() {
		return cypher;
	}

}
