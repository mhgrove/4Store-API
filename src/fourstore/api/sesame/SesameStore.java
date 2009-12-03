package fourstore.api.sesame;

import fourstore.api.Store;

import org.openrdf.model.Graph;

import java.net.URI;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 1, 2009 3:10:29 PM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public interface SesameStore extends Store {
	public boolean add(Graph theGraph, URI theGraphURI);
	public boolean delete(Graph theGraph, URI theGraphURI);
	public boolean append(Graph theGraph, URI theGraphURI);
}
