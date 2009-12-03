package web;

import java.util.HashSet;
import java.util.Collection;
import java.util.Arrays;

/**
 * Title: Header<br/>
 * Description: Represents an HTTP header, either from a request or a response.<br/>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com> <br/>
 * Created: Dec 1, 2009 7:55:14 PM <br/>
 *
 * @author Michael Grove <mike@clarkparsia.com>
 * @see Request
 * @see Response
 */
public class Header {

	/**
	 * The name of the header. {@link HttpHeaders} is an enumeration of common header names.
	 */
	private String mName;

	/**
	 * The list of values for the header
	 */
	private Collection<String> mValues = new HashSet<String>();

	/**
	 * Create a new HTTP header
	 * @param theName the name of the header attribute
	 * @param theValue the singleton value of the header
	 */
	public Header(final String theName, String theValue) {
		this(theName, new HashSet<String>(Arrays.asList(theValue)));
	}

	/**
	 * Create a new HTTP header
	 * @param theName the name of the header attribute
	 * @param theValues the values of the HTTP header
	 */
	public Header(final String theName, final String... theValues) {
		this(theName, new HashSet<String>(Arrays.asList(theValues)));
	}

	/**
	 * Create a new HTTP header
	 * @param theName the name of the header attribute
	 * @param theValues the values of the HTTP header
	 */
	public Header(final String theName, final Collection<String> theValues) {
		mName = theName;
		mValues = theValues;
	}

	/**
	 * Add a value to the header
	 * @param theValue the value to add
	 */
	void addValue(String theValue) {
		mValues.add(theValue);
	}

	/**
	 * Add all the values to the header
	 * @param theValues the values to add
	 */
	void addValues(Collection<String> theValues) {
		mValues.addAll(theValues);
	}

	/**
	 * The name of the HTTP header.  Common HTTP header names can be found in {@link HttpHeaders}
	 * @return the header name
	 */
	public String getName() {
		return mName;
	}

	/**
	 * Returns the values of the HTTP header
	 * @return the header values
	 */
	public Collection<String> getValues() {
		return mValues;
	}

	/**
	 * Return the value(s) of the header as a semi-colon separated string.  For example, if your values are "foo", "bar"
	 * and "baz" this will return the string "foo; bar; baz"
	 * @return the string encoded reprsentation of the header values suitable for insertion into a HTTP request
	 */
	public String getHeaderValue() {
		StringBuffer aBuffer = new StringBuffer();

		boolean aFirst = true;
		for (String aValue : getValues()) {
			if (!aFirst) {
				aBuffer.append("; ");
			}

			aFirst = false;

			aBuffer.append(aValue);
		}

		return aBuffer.toString();
	}
}
