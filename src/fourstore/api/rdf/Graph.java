package fourstore.api.rdf;

import java.util.Iterator;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 2, 2009 9:00:51 AM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public interface Graph extends Iterable<Statement> {
	public Iterator<Statement> getStatements();
	public Iterator<Statement> getStatements(Resource theSubj, URI thePred, Value theObj);

	public void addStatement(Statement theStatement);
	public void addStatement(Resource theSubj, URI thePred, Value theObj);

	public void removeStatement(Statement theStatement);
	public void removeStatement(Resource theSubj, URI thePred, Value theObj);

	public boolean hasStatement(Statement theStatement);
	public boolean hasStatement(Resource theSubj, URI thePred, Value theObj);

	public int size();

	public void clear();
}
