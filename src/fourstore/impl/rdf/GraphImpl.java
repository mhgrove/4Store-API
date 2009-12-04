package fourstore.impl.rdf;

import com.clarkparsia.utils.BasicUtils;
import com.clarkparsia.utils.Predicate;
import com.clarkparsia.utils.collections.CollectionUtil;
import fourstore.api.rdf.Graph;
import fourstore.api.rdf.Resource;
import fourstore.api.rdf.Statement;
import fourstore.api.rdf.URI;
import fourstore.api.rdf.Value;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 3, 2009 3:53:55 PM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public class GraphImpl implements Graph {
	private Set<Statement> mStatements = new LinkedHashSet<Statement>();

	/**
	 * @inheritDoc
	 */
	public Iterator<Statement> iterator() {
		return Collections.unmodifiableSet(mStatements).iterator();
	}

	/**
	 * @inheritDoc
	 */
	public Iterator<Statement> getStatements() {
		return iterator();
	}

	/**
	 * @inheritDoc
	 */
	public Iterator<Statement> getStatements(final Resource theSubj, final URI thePred, final Value theObj) {
		return CollectionUtil.filter(mStatements, new Predicate<Statement>() {
			public boolean accept(final Statement theValue) {
				return BasicUtils.equalsOrNull(theValue.getSubject(), theSubj)
						&& BasicUtils.equalsOrNull(theValue.getPredicate(), thePred)
						&& BasicUtils.equalsOrNull(theValue.getObject(), theObj);
			}
		}).iterator();
	}

	/**
	 * @inheritDoc
	 */
	public void addStatement(final Statement theStatement) {
		mStatements.add(theStatement);
	}

	/**
	 * @inheritDoc
	 */
	public void addStatement(final Resource theSubj, final URI thePred, final Value theObj) {
		mStatements.add(new StatementImpl(theSubj, thePred, theObj));
	}

	/**
	 * @inheritDoc
	 */
	public void removeStatement(final Statement theStatement) {
		mStatements.remove(theStatement);
	}

	/**
	 * @inheritDoc
	 */
	public void removeStatement(final Resource theSubj, final URI thePred, final Value theObj) {
		Collection<Statement> toRemove = null;

		if (theSubj != null && thePred != null && theObj != null) {
			// TODO: fix this generics issue
			toRemove = CollectionUtil.set( (Statement) new StatementImpl(theSubj, thePred, theObj));
		}
		else {
			CollectionUtil.filter(mStatements, new StatementMatcher(theSubj, thePred, theObj));
		}

		mStatements.removeAll(toRemove);
	}

	/**
	 * @inheritDoc
	 */
	public boolean hasStatement(final Statement theStatement) {
		return mStatements.contains(theStatement);
	}

	/**
	 * @inheritDoc
	 */
	public boolean hasStatement(final Resource theSubj, final URI thePred, final Value theObj) {
		if (theSubj != null && thePred != null && theObj != null) {
			return mStatements.contains(new StatementImpl(theSubj, thePred, theObj));
		}
		else {
			return CollectionUtil.find(mStatements, new StatementMatcher(theSubj, thePred, theObj));
		}
	}

	/**
	 * @inheritDoc
	 */
	public int size() {
		return mStatements.size();
	}

	/**
	 * @inheritDoc
	 */
	public void clear() {
		mStatements.clear();
	}

	private static class StatementMatcher implements Predicate<Statement> {
		private final Resource mSubj;
		private final URI mPred;
		private final Value mObj;

		public StatementMatcher(final Resource theSubj, final URI thePred, final Value theObj) {
			mSubj = theSubj;
			mPred = thePred;
			mObj = theObj;
		}

		public boolean accept(final Statement theValue) {
			return BasicUtils.equalsOrNull(theValue.getSubject(), mSubj)
				   && BasicUtils.equalsOrNull(theValue.getPredicate(), mPred)
				   && BasicUtils.equalsOrNull(theValue.getObject(), mObj);
		}
	}
}
