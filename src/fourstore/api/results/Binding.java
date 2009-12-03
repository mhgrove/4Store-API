package fourstore.api.results;

import fourstore.api.rdf.BNode;
import fourstore.api.rdf.Literal;
import fourstore.api.rdf.URI;
import fourstore.api.rdf.Value;

/**
 * Title: Binding<br/>
 * Description: <p>A single result binding for a query.  Associates variable names in the project with their values in
 * the binding.</p>
 * <p>
 * <p>Usage:</p>
 * <code>
 * </code>
 * </p><br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 2, 2009 9:54:05 AM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public interface Binding {
	public Value get(String theKey);

	public URI uri(String theKey);
	public BNode bnode(String theKey);
	public Literal literal(String theKey);

	public Iterable<String> variables();
}
