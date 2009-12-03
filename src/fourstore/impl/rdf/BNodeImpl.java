package fourstore.impl.rdf;

import fourstore.api.rdf.BNode;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 3, 2009 8:55:44 AM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public class BNodeImpl extends ResourceImpl implements BNode {
	private String mId;

	BNodeImpl(final String theId) {
		mId = theId;
	}

	/**
	 * @inheritDoc
	 */
	public String getId() {
		return mId;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String toString() {
		return getId();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean equals(Object theObj) {
		return (theObj instanceof BNode) && ((BNode)theObj).getId().equals(getId());
	}
}
