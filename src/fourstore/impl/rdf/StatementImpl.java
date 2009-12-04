package fourstore.impl.rdf;

import com.clarkparsia.utils.BasicUtils;
import fourstore.api.rdf.Resource;
import fourstore.api.rdf.Statement;
import fourstore.api.rdf.URI;
import fourstore.api.rdf.Value;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 3, 2009 3:57:13 PM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public class StatementImpl implements Statement {
	private Resource mSubject;
	private URI mPredicate;
	private Value mObject;

	private int mHashCode;

	public StatementImpl(final Resource theSubject, final URI thePredicate, final Value theObject) {
		if (theSubject == null || thePredicate == null || theObject == null) {
			throw new IllegalArgumentException("Cannot have null arguments to a statement");
		}

		mSubject = theSubject;
		mPredicate = thePredicate;
		mObject = theObject;

		mHashCode = mSubject.hashCode();
		mHashCode = 31 * mHashCode + mPredicate.hashCode();
		mHashCode = 37 * mHashCode + mObject.hashCode();
	}

	/**
	 * @inheritDoc
	 */
	public Resource getSubject() {
		return mSubject;
	}

	/**
	 * @inheritDoc
	 */
	public URI getPredicate() {
		return mPredicate;
	}

	/**
	 * @inheritDoc
	 */
	public Value getObject() {
		return mObject;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean equals(final Object theObject) {
		if (this == theObject) {
			return true;
		}

		if (theObject == null || !(theObject instanceof Statement)) {
			return false;
		}

		final StatementImpl aStmt = (StatementImpl) theObject;

		return getSubject().equals(aStmt.getSubject())
			   && getPredicate().equals(aStmt.getPredicate())
			   && getObject().equals(aStmt.getObject());
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public int hashCode() {
		return mHashCode;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String toString() {
		return "(" + getSubject() + ", " + getPredicate() + ", " + getObject() + ")";
	}
}
