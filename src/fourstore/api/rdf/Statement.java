package fourstore.api.rdf;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 2, 2009 9:01:09 AM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public interface Statement {
	public Resource getSubject();
	public URI getPredicate();
	public Value getObject();
}
