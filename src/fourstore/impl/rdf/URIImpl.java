package fourstore.impl.rdf;

import fourstore.api.rdf.URI;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 3, 2009 8:55:39 AM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public class URIImpl extends ResourceImpl implements URI {
	private String mURI;

	URIImpl(final String theURI) {
		mURI = theURI;
	}

	/**
	 * @inheritDoc
	 */
	public String getURI() {
		return mURI;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String toString() {
		return getURI();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public int hashCode() {
		return getURI().hashCode();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean equals(Object theObj) {
		return (theObj instanceof URI) && ((URI)theObj).getURI().equals(getURI());
	}
}
