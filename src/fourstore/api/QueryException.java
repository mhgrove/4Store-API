package fourstore.api;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 1, 2009 3:06:29 PM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public class QueryException extends StoreException {
	public QueryException() {
	}

	public QueryException(final String theMessage) {
		super(theMessage);
	}

	public QueryException(final String theMessage, final Throwable theCause) {
		super(theMessage, theCause);
	}

	public QueryException(final Throwable theCause) {
		super(theCause);
	}
}
