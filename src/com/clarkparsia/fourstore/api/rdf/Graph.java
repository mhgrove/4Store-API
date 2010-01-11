/*
 * Copyright (c) 2005-2010 Clark & Parsia, LLC. <http://www.clarkparsia.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.clarkparsia.fourstore.api.rdf;

import java.util.Iterator;

/**
 * <p>Interface for an RDF graph represented as a collection of {@link Statement Statements}</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
public interface Graph extends Iterable<Statement> {

	/**
	 * Return an Iterator over all the statements in the graph
	 * @return an Iterator over the entire graph
	 */
	public Iterator<Statement> getStatements();

	/**
	 * Return an Iterator over the set of statements that match the provided spo pattern.
	 * @param theSubj the subject to match, or null to match any
	 * @param thePred the predicate to match, or null to match any
	 * @param theObj the object to match, or null to match any.
	 * @return an Iterator over all matching statements
	 */
	public Iterator<Statement> getStatements(Resource theSubj, URI thePred, Value theObj);

	/**
	 * Add the statement to the graph
	 * @param theStatement the statement to add
	 */
	public void addStatement(Statement theStatement);

	/**
	 * Add the statement to the graph
	 * @param theSubj the subject of the statement to add
	 * @param thePred the predicate of the statement to add
	 * @param theObj the object of the statement to add
	 */
	public void addStatement(Resource theSubj, URI thePred, Value theObj);

	/**
	 * Remove the given statement from the graph
	 * @param theStatement the statement to remove
	 */
	public void removeStatement(Statement theStatement);

	/**
	 * Remove any statements matching the provided spo pattern from the graph
	 * @param theSubj the subject, or null to match any subject
	 * @param thePred the predicate or null to match any predicate
	 * @param theObj the object or null to match any object
	 */
	public void removeStatement(Resource theSubj, URI thePred, Value theObj);

	/**
	 * Return whether or not the Graph contains the given statement
	 * @param theStatement the statement to look for
	 * @return true if it contains the statement, false otherwise.
	 */
	public boolean hasStatement(Statement theStatement);

	/**
	 * Return whether or not this graph has a statement(s) which match the specified pattern.
	 * @param theSubj the statement subject, or null to match any subject
	 * @param thePred the statement predicate, or null to match any predicate
	 * @param theObj the statement object, or null to match any object
	 * @return true if the graph has at least one statement matching the provided spo pattern.
	 */
	public boolean hasStatement(Resource theSubj, URI thePred, Value theObj);

	/**
	 * Return the number of statements in this graph.
	 * @return the size of the graph
	 */
	public int size();

	/**
	 * Remove all the statements in this graph.
	 */
	public void clear();
}
