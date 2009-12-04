package fourstore.impl;

import web.HttpResource;
import web.Method;
import web.Request;
import web.Response;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 3, 2009 8:50:24 AM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public class HttpResourceImpl implements HttpResource {
	private URL mURL;

	public HttpResourceImpl(final URL theBaseURL) {
		mURL = theBaseURL;
	}

	public HttpResource resource(final String theName) {
		try {
			return new HttpResourceImpl(new URL(mURL.toString() + (mURL.toString().endsWith("/") ? "" : "/") + theName + (theName.endsWith("/") ? "" : "/")));
		}
		catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public Response delete() throws IOException {
		return initDelete().execute();
	}

	public Response get() throws IOException {
		return initGet().execute();
	}

//	public Response head() throws IOException {
//		Request aReq = new Request(Method.HEAD, mURL);
//
//		return aReq.execute();
//	}

	public Request initGet() {
		return new Request(Method.GET, mURL);
	}

	public Request initPost() {
		return new Request(Method.POST, mURL);
	}

	public Request initPut() {
		return new Request(Method.PUT, mURL);
	}

	public Request initDelete() {
		return new Request(Method.DELETE, mURL);
	}

	public Request initRequest(Method theMethod) {
		return new Request(theMethod, mURL);
	}
}
