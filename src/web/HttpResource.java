package web;

import java.io.IOException;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 1, 2009 2:50:43 PM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public interface HttpResource {
	public HttpResource resource(String theName);

	public Response get() throws IOException;
	public Response delete() throws IOException;
//	public Response head() throws IOException;

	public Request initGet();
	public Request initPost();
	public Request initPut();
	public Request initDelete();

	public Request initRequest(Method theMethod);
}
