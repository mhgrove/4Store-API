package fourstore.impl;

import com.clarkparsia.utils.collections.CollectionUtil;
import fourstore.api.results.Binding;
import fourstore.api.results.ResultSet;
import fourstore.api.results.ResultSetFormatter;

import java.util.List;

/**
 * Title: TabbedResultSetFormatter<br/>
 * Description: Simple result set formatter for displaying a result set in a tab separated table<br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 1, 2009 4:04:52 PM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public class TabbedResultSetFormatter implements ResultSetFormatter {
	public String format(final ResultSet theResultSet) {
		StringBuffer aBuffer = new StringBuffer();

		if (theResultSet.isEmpty()) {
			aBuffer.append("*** No Results ***");
		}
		else {
			// TODO: use some of the text formatting stuff to get the columns to properly align

			Binding aVarsBinding = theResultSet.iterator().next();
			List<String> aVars = CollectionUtil.list(aVarsBinding.variables());
			for (String aVar : aVars) {
				aBuffer.append(aVar);
				aBuffer.append("\t");
			}
			aBuffer.append("\n---\n");

			for (Binding aBinding : theResultSet) {
				for (String aVar : aVars) {
					aBuffer.append(aBinding.get(aVar));
					aBuffer.append("\t");
				}
				aBuffer.append("\n");
			}
		}

		return aBuffer.toString();
	}
}
