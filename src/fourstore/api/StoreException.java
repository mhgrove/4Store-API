package fourstore.api;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 1, 2009 3:08:56 PM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public class StoreException extends Exception {
	public StoreException() {
	}

	public StoreException(final String theMessage) {
		super(theMessage);
	}

	public StoreException(final String theMessage, final Throwable theCause) {
		super(theMessage, theCause);
	}

	public StoreException(final Throwable theCause) {
		super(theCause);
	}
}
