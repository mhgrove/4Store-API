package fourstore.impl.rdf;

import fourstore.api.rdf.BNode;
import fourstore.api.rdf.Literal;
import fourstore.api.rdf.URI;
import fourstore.api.rdf.ValueFactory;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 2, 2009 9:56:31 AM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public class FourStoreValueFactory implements ValueFactory {
	private static int mBNodeId = 0;

	public URI createURI(final String theURI) {
		return new URIImpl(theURI);
	}

	public BNode createBNode() {
		return new BNodeImpl(String.valueOf(mBNodeId++));
	}

	public BNode createBNode(final String theId) {
		return new BNodeImpl(theId);
	}

	public Literal createLiteral(final String theValue) {
		return new LiteralImpl(theValue, null, null);
	}

	public Literal createLiteral(final String theValue, final URI theDatatype) {
		return new LiteralImpl(theValue, null, theDatatype);
	}

	public Literal createLiteral(final String theValue, final String theLang) {
		return new LiteralImpl(theValue, theLang, null);
	}
}
