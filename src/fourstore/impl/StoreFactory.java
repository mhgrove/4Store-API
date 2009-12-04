package fourstore.impl;

import fourstore.api.Store;
import fourstore.impl.StoreImpl;
import web.Method;

import java.net.URL;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 1, 2009 2:45:46 PM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public class StoreFactory {
	public static Store create(URL theURL) {
		return new StoreImpl(theURL);
	}

	public static Store create(URL theURL, Method theQueryMethod) {
		if (theQueryMethod != Method.POST && theQueryMethod != Method.GET) {
			throw new IllegalArgumentException("Queries can only be made as POST or GET");
		}

		return new StoreImpl(theURL, theQueryMethod == Method.GET);
	}
}
