package web;

/**
 * Title: Parameter<br/>
 * Description: A representation of a parameter, simply a key-value pair.<br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 1, 2009 2:52:32 PM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public class Parameter {
	/**
	 * The parameter name
	 */
	private String mName;

	/**
	 * The parameter value
	 */
	private String mValue;

	/**
	 * Create a new Parameter
	 * @param theName the name of the parameter
	 * @param theValue the value of the parameter
	 */
	public Parameter(final String theName, final String theValue) {
		mName = theName;
		mValue = theValue;
	}

	/**
	 * Return the name of the parameter
	 * @return the name
	 */
	public String getName() {
		return mName;
	}

	/**
	 * Return the value of the parameter
	 * @return the value
	 */
	public String getValue() {
		return mValue;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String toString() {
		return getName() + " = " + getValue();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public int hashCode() {
		return getName().hashCode() + getValue().hashCode();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean equals(Object theObject) {
		return theObject instanceof Parameter
			   && ((Parameter)theObject).getName().equals(getName())
			   && ((Parameter)theObject).getValue().equals(getValue());
	}
}
