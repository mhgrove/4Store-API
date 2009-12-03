package web;

import java.util.Map;
import java.util.Collection;
import java.util.HashMap;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 1, 2009 2:51:15 PM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public class Response {
	private int mResponseCode;
	private String mContent;
	private Map<String, Header> mHeaders = new HashMap<String, Header>();
	private String mMessage;

	void setResponseCode(final int theResponseCode) {
		mResponseCode = theResponseCode;
	}

	void setContent(final String theContent) {
		mContent = theContent;
	}

	public void setHeaders(final Collection<Header> theHeaders) {
		mHeaders = new HashMap<String, Header>();

		for (Header aHeader : theHeaders) {
			mHeaders.put(aHeader.getName(), aHeader);
		}
	}

	void setMessage(final String theMessage) {
		mMessage = theMessage;
	}

	public String getMessage() {
		return mMessage;
	}

	public Collection<Header> getHeaders() {
		return mHeaders.values();
	}

	public Header getHeader(String theName) {
		return mHeaders.get(theName);
	}

	public int getResponseCode() {
		return mResponseCode;
	}

	public String getContent() {
		return mContent;
	}

	public boolean hasErrorCode() {
		// TODO: right?
		return getResponseCode() >= 400;
	}
}
