package fourstore.api.rdf;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 1, 2009 3:27:51 PM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public interface Literal extends Value {
	public String getLanguage();
	public String getValue();
	public URI getDatatype();
}
