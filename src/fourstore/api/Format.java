package fourstore.api;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 1, 2009 3:12:09 PM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public enum Format {
	Turtle("application/x-turtle"),
	RdfXml("application/rdf+xml"),
	N3("application/rdf+n3"),
	NTriples("application/rdf+nt"),
	Trig("application/x-trig");

	private final String mMimeType;

	Format(final String theMimeType) {
		mMimeType = theMimeType;
	}

	public String getMimeType() {
		return mMimeType;
	}
}
