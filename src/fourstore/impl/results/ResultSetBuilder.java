package fourstore.impl.results;

import fourstore.api.rdf.Value;
import fourstore.api.rdf.ValueFactory;

import fourstore.api.results.Binding;
import fourstore.api.results.ResultSet;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 1, 2009 3:37:41 PM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public class ResultSetBuilder {
	private Collection<Binding> mValues = new ArrayList<Binding>();
	private BindingBuilder mCurrBinding = new BindingBuilder();
	private ValueFactory mFactory;

	public ResultSetBuilder(final ValueFactory theFactory) {
		mFactory = theFactory;
	}

	public void reset() {
		mValues.clear();
	}

	public void startResultSet() {
		reset();
	}

	public ResultSet endResultSet() {
		return resultSet();
	}

	public void startBinding() {
		mCurrBinding.reset();
	}

	public ValueFactory getValueFactory() {
		return mFactory;
	}

	public void endBinding() {
		mValues.add(mCurrBinding.binding());
	}

	public BindingBuilder addToBinding(String theKey, Value theValue) {
		return mCurrBinding.append(theKey, theValue);
	}

	public ResultSet resultSet() {
		return new ResultSetImpl(mValues);
	}
}
