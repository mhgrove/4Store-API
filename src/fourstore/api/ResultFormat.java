package fourstore.api;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 2, 2009 9:39:08 AM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public enum ResultFormat {
	TabSeparated("text/tab-separated-values"),
	JSON("application/sparql-results+json"),
	XML("application/sparql-results+xml");

	private final String mMimeType;

	ResultFormat(final String theMimeType) {
		mMimeType = theMimeType;
	}

	public String getMimeType() {
		return mMimeType;
	}
}
