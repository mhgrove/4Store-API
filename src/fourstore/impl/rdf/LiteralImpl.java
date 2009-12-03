package fourstore.impl.rdf;

import com.clarkparsia.utils.BasicUtils;
import fourstore.api.rdf.Literal;
import fourstore.api.rdf.URI;

import static com.clarkparsia.utils.BasicUtils.equalsOrNull;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 3, 2009 8:55:26 AM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public class LiteralImpl extends ValueImpl implements Literal {
	private String mLanguage;
	private String mValue;
	private URI mDatatype;

	LiteralImpl(String theValue, String theLang, URI theDatatype) {
		mValue = theValue;
		mLanguage = theLang;
		mDatatype = theDatatype;
	}

	/**
	 * @inheritDoc
	 */
	public String getLanguage() {
		return mLanguage;
	}

	/**
	 * @inheritDoc
	 */
	public String getValue() {
		return mValue;
	}

	/**
	 * @inheritDoc
	 */
	public URI getDatatype() {
		return mDatatype;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String toString() {
		return mValue;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public int hashCode() {
		return mValue.hashCode() + (getLanguage() == null ? 0 : getLanguage().hashCode()) + (getDatatype() == null ? 0 : getDatatype().hashCode());
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean equals(Object theObj) {
		if (theObj instanceof Literal) {
			Literal aLit = (Literal) theObj;

			return getValue().equals(aLit.getValue()) &&
				   equalsOrNull(getLanguage(), aLit.getLanguage()) &&
				   equalsOrNull(getDatatype(), aLit.getDatatype());
		}
		else {
			return false;
		}
	}
}
