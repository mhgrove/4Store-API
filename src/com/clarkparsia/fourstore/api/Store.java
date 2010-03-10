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

package com.clarkparsia.fourstore.api;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import org.openrdf.query.TupleQueryResult;

import org.openrdf.rio.RDFFormat;
import org.openrdf.repository.RepositoryResult;

import java.net.ConnectException;
import java.io.InputStream;
import java.io.Reader;

/**
 * <p>Interface to interact with a 4Store instance.  Instances of this interface can be obtained from
 * {@link com.clarkparsia.fourstore.impl.StoreFactory}.</p>
 *
 * @author Michael Grove
 * @since 0.1
 * @version 0.3
 */
public interface Store {
	/**
	 * Open a connection to the database.  You must open a connection before using the database.
	 * @throws ConnectException thrown if a connection cannot be established.
	 */
	public void connect() throws ConnectException;

	/**
	 * Close the current connection, if open, and free up any associated resources.  Once disconnected, you cannot
	 * @throws ConnectException thrown if there was an error freeing the connection
	 */
	public void disconnect() throws ConnectException;

	/**
	 * Globally sets the soft limit for all queries dispatched to the database.  The soft limit keeps hard to evaluate
	 * queries from overwhelming the database.  The higher the soft limit, the harder it will work to answer a query.
	 * You can get more information about this at the <a href="http://4store.org/trac/wiki/Query">4store site</a>
	 * @param theLimit the new soft limit, or -1 to not specify one and use the store default.
	 */
	public void setSoftLimit(int theLimit);

	/**
	 * Return the soft limit used by queries sent to the store.  A value of -1 indicates that the default value for the
	 * store is being used.
	 * @return the soft limit, or -1 if not specified.
	 * @see #setSoftLimit
	 * @see &lt;http://4store.org/trac/wiki/Query&gt;
	 */
	public int getSoftLimit();

	/**
	 * Return whether or not the statement is in the database.
	 * @param theStmt the statement to look for
	 * @return true if the statement is in the database, false otherwise
	 * @throws StoreException thrown if there is an error while looking for the statement.
	 */
	public boolean hasStatement(Statement theStmt) throws StoreException;

	/**
	 * Return whether or not there is a statement, or statements, which match the given SPO pattern.  If all values are
	 * bound, this searches for a specified statement.  Otherwise nulls are treated as wild cards in the search.  Some
	 * implementations may not allow all three values to be unbound.
	 * @param theSubj the subject of the statement, or null for any subject
	 * @param thePred the predicate of the statement, or null for any predicate
	 * @param theObj the object of the statement, or null for any predicate
	 * @return true if there are statements matching the given SPO pattern, false otherwise.
	 * @throws StoreException thrown if there is an error while looking for the statement(s)
	 */
	public boolean hasStatement(Resource theSubj, URI thePred, Value theObj) throws StoreException;

	/**
	 * Returns an iterator result over the set of statements which match the specified SPO pattern.
	 * @param theSubj the subject of the statement, or null for any subject
	 * @param thePred the predicate of the statement, or null for any predicate
	 * @param theObj the object of the statement, or null for any predicate
	 * @return an iterator over all matching statements
	 * @throws StoreException if there is an error while querying for the statements
	 */
	public RepositoryResult<Statement> getStatements(final Resource theSubj, final URI thePred, final Value theObj) throws StoreException;

	/**
	 * Send a select query to the endpoint.
	 * @param theQuery the query to execute
	 * @return the results of the select query
	 * @throws QueryException if there is an error while evaluating the query
	 */
	public TupleQueryResult query(String theQuery) throws QueryException;
	public Graph constructQuery(String theQuery) throws QueryException;
	public Graph describe(String theQuery) throws QueryException;


	public boolean add(Graph theGraph, URI theGraphURI) throws StoreException;
	public boolean add(String theGraph, RDFFormat theFormat, URI theGraphURI) throws StoreException;
	public boolean add(InputStream theGraph, RDFFormat theFormat, URI theGraphURI) throws StoreException;

	public boolean delete(Graph theGraph, URI theGraphURI) throws StoreException;

	/**
	 * Delete's the entire named subgraph
	 * @param theGraphURI the URI of the named graph to remove
	 * @return true if the graph was removed, false otherwise
	 * @throws StoreException if there is an error while removing
	 */
	public boolean delete(URI theGraphURI) throws StoreException;

	public boolean append(Graph theGraph, URI theGraphURI) throws StoreException;
	public boolean append(String theGraph, RDFFormat theFormat, URI theGraphURI) throws StoreException;

	/**
	 * Adds the RDF data to the store in an existing context.
	 * @param theGraph an input stream containing the data to add
	 * @param theFormat the format the rdf data is in
	 * @param theGraphURI the graph context to add the data to
	 * @return true if data was successfully added, false otherwise
	 * @throws StoreException if there is an error appending the data to an existing context
	 */
	public boolean append(InputStream theGraph, RDFFormat theFormat, URI theGraphURI) throws StoreException;

	/**
	 * Return the number of triples in this Store
	 * @return the size of the store
	 * @throws StoreException thrown if there was an error while trying to retrieve the triple count
	 */
	public long size() throws StoreException;

	/**
	 * Returns the result of the status page, as an HTML-formatted string.
	 * @return the status page
	 * @throws StoreException thrown if the status cannot be retrieved from the server.
	 */
	public String status() throws StoreException;
}
