package fourstore.api.results;

/**
 * Title: ResultSet<br/>
 * Description: The results of a query.<br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 2, 2009 9:50:18 AM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public interface ResultSet extends Iterable<Binding> {
	/**
	 * Returns the number of results in the result set
	 * @return the size of the result set
	 */
	public int size();

	/**
	 * Returns whether or not the result set is empty
	 * @return true if the result set is empty, false otherwise.
	 */
	public boolean isEmpty();
}
