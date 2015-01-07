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

package org.arrow.data.neo4j.traversal;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.arrow.runtime.logger.LoggerFacade;

/**
 * Evaluator implementation used to determine if an execution is synchronized.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class BreadthFirstSynchronisationEvaluator implements Evaluator {

    private boolean synchronised = true;

    public boolean isSynchronised() {
        return synchronised;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Evaluation evaluate(Path path) {

        // skip the inclusive gateway itself
        if (path.length() == 0) {
            return Evaluation.INCLUDE_AND_CONTINUE;
        }

        if (!synchronised) {
            return Evaluation.EXCLUDE_AND_PRUNE;
        }

        Node node = path.endNode();

        final RelationshipType type = DynamicRelationshipType.withName("EXECUTION");
        Iterable<Relationship> relationships = node.getRelationships(type, Direction.OUTGOING);

        if (!relationships.iterator().hasNext()) {
            synchronised = false;
            return Evaluation.EXCLUDE_AND_PRUNE;
        }

        Relationship relationship = relationships.iterator().next();
        Node execution = relationship.getEndNode();

        synchronised = isSuccessful(execution);

        if (synchronised) {
            return Evaluation.INCLUDE_AND_CONTINUE;
        }
        return Evaluation.EXCLUDE_AND_PRUNE;
    }

    /**
     * Indicates if the given execution node is in state SUCCESS.
     *
     * @param execution the execution node instance
     * @return boolean
     */
    private boolean isSuccessful(Node execution) {
        final String state = (String) execution.getProperty("state");
        return state != null && state.equalsIgnoreCase("SUCCESS");
    }

}
