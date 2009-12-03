package web;

/**
 * Title: MimeTypes<br/>
 * Description: Enumeration of common mime-types.<br/>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com> <br/>
 * Created: Dec 1, 2009 7:52:16 PM <br/>
 *
 * @author Michael Grove <mike@clarkparsia.com>
 */
public enum MimeTypes {
	FormUrlEncoded("application/x-www-form-urlencoded"),
	TextPlain("text/plain");

	/**
	 * The mime-type string
	 */
	private String mType;

	/**
	 * Create the new MimeType
	 * @param theType the mime type
	 */
	MimeTypes(final String theType) {
		mType = theType;
	}

	/**
	 * Return the mime-type
	 * @return the mime-type string
	 */
	public String getMimeType() {
		return mType;
	}
}
