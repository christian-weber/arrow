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

package org.arrow.data.neo4j.query.cypher;

import org.arrow.data.neo4j.query.cypher.close.Close;
import org.arrow.data.neo4j.query.cypher.close.SimpleClose;
import org.arrow.data.neo4j.query.cypher.match.Match;
import org.arrow.data.neo4j.query.cypher.match.SimpleMatch;
import org.arrow.data.neo4j.query.cypher.start.SimpleStart;
import org.arrow.data.neo4j.query.cypher.start.Start;
import org.arrow.data.neo4j.query.cypher.where.SimpleWhere;
import org.arrow.data.neo4j.query.cypher.where.Where;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Cypher criteria implementation which offers various methods to manipulate the
 * cypher query.
 * 
 * @author christian.weber
 * @since 1.0.0
 */
public class CypherCriteria {

	private List<Start> startList = new ArrayList<>();
	private List<Match> matchList = new ArrayList<>();
	private List<Where> whereList = new ArrayList<>();
	private List<Close> closeList = new ArrayList<>();

	/**
	 * Adds a simple {@link Start} cypher fragment to the list of fragments.
	 * 
	 * @param str the cypher string
	 */
	public void start(String str) {
		add(new SimpleStart(str));
	}

	/**
	 * Adds a simple {@link Match} cypher fragment to the list of fragments.
	 * 
	 * @param str the cypher string
	 */
	public void match(String str) {
		add(new SimpleMatch(str));
	}

	/**
	 * Adds a simple {@link Where} cypher fragment to the list of fragments.
	 * 
	 * @param str the cypher string
	 */
	public void where(String str) {
		add(new SimpleWhere(str));
	}

	/**
	 * Adds a simple {@link Close} cypher fragment to the list of fragments.
	 * 
	 * @param str the cypher string
	 */
	public void close(String str) {
		add(new SimpleClose(str));
	}

	/**
	 * Adds a {@link Start} cypher fragment to the list of fragments.
	 * 
	 * @param start the start segment
	 */
	public void add(Start start) {
		startList.add(start);
	}

	/**
	 * Adds a {@link Match} cypher fragment to the list of fragments.
	 * 
	 * @param match the match segment
	 */
	public void add(Match match) {
		matchList.add(match);
	}

	/**
	 * Adds a {@link Where} cypher fragment to the list of fragments.
	 * 
	 * @param where the where segment
	 */
	public void add(Where where) {
		whereList.add(where);
	}

	/**
	 * Adds a {@link Close} cypher fragment to the list of fragments.
	 * 
	 * @param close the close segment
	 */
	public void add(Close close) {
		closeList.add(close);
	}

	/**
	 * Returns a cypher string by concatenating all cypher fragments.
	 * 
	 * @return String
	 */
	public String toCypher() {
		StringBuilder builder;
        builder = new StringBuilder();

        builder.append(join("START ", startList));
		builder.append(join("MATCH ", matchList));
		builder.append(join("WHERE ", whereList, " and "));
		builder.append(join("RETURN ", closeList));

		return builder.toString();
	}

	private String join(String prefix, Iterable<? extends CypherText> iterable) {
		return join(prefix, iterable, ", ");
	}

	private String join(String prefix, Iterable<? extends CypherText> iterable,
			String joinCharacter) {

		StringBuilder builder = new StringBuilder();
		Iterator<? extends CypherText> iterator = iterable.iterator();

		if (iterator.hasNext()) {
			builder.append(prefix);
		}
		while (iterator.hasNext()) {
			CypherText segment = iterator.next();
			builder.append(segment.toCypher());
			if (iterator.hasNext()) {
				builder.append(joinCharacter);
			}
		}

		return builder.toString();
	}

}
