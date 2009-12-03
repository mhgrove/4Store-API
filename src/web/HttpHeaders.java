package web;

/**
 * Title: HttpHeaders<br/>
 * Description: Enumeration of common http headers.<br/>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com> <br/>
 * Created: Dec 1, 2009 7:50:25 PM <br/>
 *
 * @author Michael Grove <mike@clarkparsia.com>
 */
public enum HttpHeaders {

	Accept("Accept"),
	Authentication("Authentication"),
	ContentDisposition("Content-Disposition"),
	ContentLength("Content-Length"),
	ContentType("Content-Type");

	/**
	 * The name of the HTTP header
	 */
	private String mName;

	/**
	 * Create a new HTTP header key
	 * @param theName the name of the header
	 */
	HttpHeaders(final String theName) {
		mName = theName;
	}

	/**
	 * Return the name of the header
	 * @return the header name
	 */
	public String getName() {
		return mName;
	}
}
