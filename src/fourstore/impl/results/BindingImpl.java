package fourstore.impl.results;

import fourstore.api.rdf.BNode;
import fourstore.api.rdf.Literal;
import fourstore.api.rdf.URI;
import fourstore.api.rdf.Value;
import fourstore.api.results.Binding;

import java.util.Collections;
import java.util.Map;

/**
 * Title: BindingImpl<br/>
 * Description: Implementation of the Binding interface<br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 1, 2009 2:55:10 PM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 * @see URI
 * @see Literal
 * @see BNode
 * @see Value
 */
public class BindingImpl implements Binding {
	private Map<String, Value> mValues;

	BindingImpl(Map<String, Value> theValues) {
		mValues = theValues;
	}

	public Value get(String theKey) {
		return mValues.get(theKey);
	}

	public URI uri(String theKey) {
		return safeGet(theKey, URI.class);
	}

	public BNode bnode(String theKey) {
		return safeGet(theKey, BNode.class);
	}

	public Literal literal(String theKey) {
		return safeGet(theKey, Literal.class);
	}

	public Iterable<String> variables() {
		return Collections.unmodifiableCollection(mValues.keySet());
	}

	private <T> T safeGet(String theKey, Class<T> theClass) {
		try {
			if (mValues.containsKey(theKey)) {
				return theClass.cast(mValues.get(theKey));
			}
			else {
				return null;
			}
		}
		catch (ClassCastException ex) {
			// this is ok, it's the point of safe get, we'll just return null
			// we're treating get me var "foo" as a URI the same as get me var "foo" when "foo" does not exist
			return null;
		}
	}
}
