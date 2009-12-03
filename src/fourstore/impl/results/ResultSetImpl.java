package fourstore.impl.results;

import fourstore.api.results.Binding;
import fourstore.impl.results.BindingImpl;
import fourstore.api.results.ResultSet;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Title: ResultSetImpl<br/>
 * Description: The implementation of the ResultSet interface<br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 1, 2009 2:54:52 PM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 * @see fourstore.api.results.Binding
 * @see fourstore.api.results.ResultSet
 * @see BindingImpl
 */
public class ResultSetImpl implements ResultSet {
	/**
	 * The bindings that make up the result set
	 */
	private Collection<Binding> mBindings = new HashSet<Binding>();

	/**
	 * Create a new ResultSetImpl.  Package Protected, use {@link fourstore.impl.results.ResultSetBuilder} to create ResultSets.
	 * @param theBindings the list of bindings for the result set.
	 */
	ResultSetImpl(Collection<Binding> theBindings) {
		mBindings = theBindings;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * @inheritDoc
	 */
	public int size() {
		return mBindings.size();
	}

	/**
	 * @inheritDoc
	 */
	public Iterator<Binding> iterator() {
		return Collections.unmodifiableCollection(mBindings).iterator();
	}
}
