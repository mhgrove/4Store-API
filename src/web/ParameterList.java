package web;

import com.clarkparsia.utils.io.Encoder;

import java.util.ArrayList;

/**
 * Title: ParameterList<br/>
 * Description: A list of {@link Parameter} objects.<br/>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com> <br/>
 * Created: Dec 1, 2009 7:41:14 PM <br/>
 *
 * @author Michael Grove <mike@clarkparsia.com>
 */
public class ParameterList extends ArrayList<Parameter> {

	/**
	 * Adds a new parameter to the list
	 * @param theName the name of the parameter
	 * @param theValue the value of the parameter
	 * @return the list itself
	 */
	public ParameterList add(String theName, String theValue) {
		add(new Parameter(theName, theValue));
		return this;
	}

	/**
	 * Create a string representation of the list of parameters
	 * @param theEncode true if the values of the parameters should be URL encoded, false otherwise
	 * @return the params as a string
	 */
	private String str(boolean theEncode) {
		StringBuffer aBuffer = new StringBuffer();

		boolean aFirst = true;
		for (Parameter aParam : this) {
			if (!aFirst) {
				aBuffer.append("&");
			}

			aBuffer.append(aParam.getName());
			aBuffer.append("=");
			aBuffer.append(theEncode
						   ? Encoder.urlEncode(aParam.getValue())
						   : aParam.getValue());

			aFirst = false;
		}

		return aBuffer.toString();
	}

	/**
	 * Return the Parameters in the list in URL (encoded) form.  They will be & delimited and the values of each parameter
	 * will be encoded.  If you have two parameters a:b and c:"d d", the result is a=b&c=d+d
	 * @return the URL encoded parameter list in key-value pairs
	 */
	public String getURLEncoded() {
		return str(true /* encode */);
	}


	/**
	 * Functionally similar to {@link #getURLEncoded} but the values of the parameters are not URL encoded.
	 * @inheritDoc
	 */
	@Override
	public String toString() {
		return str(false /* don't encode */);
	}
}
