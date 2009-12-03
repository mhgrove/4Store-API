package fourstore.impl.results;

import fourstore.api.rdf.Value;
import fourstore.api.results.Binding;
import fourstore.impl.results.BindingImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 1, 2009 3:35:41 PM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
class BindingBuilder {
	private Map<String, Value> mValues = new HashMap<String, Value>();

	public void reset() {
		mValues.clear();
	}

	public Binding binding() {
		return new BindingImpl(new HashMap<String, Value>(mValues));
	}

	public BindingBuilder append(String theKey, Value theValue) {
		mValues.put(theKey, theValue);
		return this;
	}
}
