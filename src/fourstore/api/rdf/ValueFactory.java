package fourstore.api.rdf;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 1, 2009 3:52:08 PM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public interface ValueFactory {
	public URI createURI(String theURI);

	public BNode createBNode();
	public BNode createBNode(String theId);

	public Literal createLiteral(String theValue);
	public Literal createLiteral(String theValue, URI theDatatype);
	public Literal createLiteral(String theValue, String theLang);

	// TODO: typed literals, eg createLiteral(boolean) creates a boolean-typed literal
}
