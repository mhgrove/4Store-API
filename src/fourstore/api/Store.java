package fourstore.api;

import fourstore.api.rdf.Graph;
import fourstore.api.rdf.Resource;
import fourstore.api.rdf.Statement;
import fourstore.api.rdf.URI;
import fourstore.api.rdf.Value;

import fourstore.api.results.ResultSet;

import java.net.ConnectException;

/**
 * Title: Store<br/>
 * Description: Interface to interact with a 4Store instance.  Instances of this interface can be obtained from
 * {@link fourstore.impl.StoreFactory}.<br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 1, 2009 2:45:19 PM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
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
	 * @see <http://4store.org/trac/wiki/Query>
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


	public ResultSet query(String theQuery) throws QueryException;
	public Graph constructQuery(String theQuery) throws QueryException;
	public Graph describe(URI theConcept) throws QueryException;
	public boolean ask(URI theConcept) throws QueryException;

	// TODO: for these operations that modify the data, add methods which take an InputStream rather than
	// a string.  this way the inputstream can be written directly to the HTTP connection rather than being in a
	// string which requires all of it to be in memory

	public boolean add(String theGraph, Format theFormat, java.net.URI theGraphURI) throws StoreException;

	// TODO: why is this not supported??
//	public boolean delete(String theGraph, Format theFormat, java.net.URI theGraphURI) throws StoreException;
	public boolean delete(java.net.URI theGraphURI) throws StoreException;

	public boolean append(String theGraph, Format theFormat, java.net.URI theGraphURI) throws StoreException;

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
