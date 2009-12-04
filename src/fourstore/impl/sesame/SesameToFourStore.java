package fourstore.impl.sesame;

import fourstore.api.rdf.BNode;
import fourstore.api.rdf.Graph;
import fourstore.api.rdf.Resource;
import fourstore.api.rdf.Statement;
import fourstore.impl.rdf.FourStoreValueFactory;
import fourstore.impl.rdf.GraphImpl;
import fourstore.impl.rdf.StatementImpl;
import fourstore.api.rdf.URI;
import org.openrdf.model.Value;
import org.openrdf.sesame.sail.StatementIterator;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 3, 2009 4:19:27 PM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public class SesameToFourStore {
	private static FourStoreValueFactory aFactory = new FourStoreValueFactory();

	public static Graph toGraph(org.openrdf.model.Graph theSesameGraph) {

		Graph aGraph = new GraphImpl();

		StatementIterator sIter = theSesameGraph.getStatements();
		while (sIter.hasNext()) {
			org.openrdf.model.Statement aStmt = sIter.next();

			aGraph.addStatement(toStatement(aStmt));
		}

		sIter.close();

		return aGraph;
	}

	public static Statement toStatement(final org.openrdf.model.Statement theStmt) {
		return new StatementImpl(toResource(theStmt.getSubject()),
								 toURI(theStmt.getPredicate()),
								 toValue(theStmt.getObject()));
	}

	private static fourstore.api.rdf.Value toValue(final org.openrdf.model.Value theValue) {
		if (theValue instanceof org.openrdf.model.Resource) {
			return toResource((org.openrdf.model.Resource) theValue);
		}

		org.openrdf.model.Literal aLit = (org.openrdf.model.Literal) theValue;

		if (aLit.getDatatype() != null) {
			return aFactory.createLiteral(aLit.getLabel(), toURI(aLit.getDatatype()));
		}
		else if (aLit.getLanguage() != null) {
			return aFactory.createLiteral(aLit.getLabel(), aLit.getLanguage());
		}
		else {
			return aFactory.createLiteral(aLit.getLabel());
		}
	}

	private static Resource toResource(final org.openrdf.model.Resource theResource) {
		return theResource instanceof org.openrdf.model.URI ? toURI((org.openrdf.model.URI) theResource) : toBNode( (org.openrdf.model.BNode) theResource);
	}

	private static BNode toBNode(final org.openrdf.model.BNode theBNode) {
		return aFactory.createBNode(theBNode.getID());
	}

	private static URI toURI(final org.openrdf.model.URI theURI) {
		return aFactory.createURI(theURI.getURI());
	}
}
