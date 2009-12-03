package fourstore.api.results;

import fourstore.api.results.ResultSet;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 1, 2009 4:03:42 PM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public interface ResultSetFormatter {
	public String format(ResultSet theResultSet);
}
