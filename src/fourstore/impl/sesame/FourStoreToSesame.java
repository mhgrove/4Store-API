package fourstore.impl.sesame;

import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.Literal;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.model.impl.StatementImpl;

import com.clarkparsia.sesame.utils.SesameValueFactory;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com> <br/>
 * Created: Jan 6, 2010 9:23:06 AM <br/>
 *
 * @author Michael Grove <mike@clarkparsia.com>
 */
public class FourStoreToSesame {
	
	public static Graph toSesameGraph(fourstore.api.rdf.Graph theFourStoreGraph) {
		Graph aGraph = new GraphImpl();
		
		for (fourstore.api.rdf.Statement aStmt : theFourStoreGraph) {
			aGraph.add(toSesameStatement(aStmt));
		}
		
		return aGraph;
	}

	public static Statement toSesameStatement(final fourstore.api.rdf.Statement theStmt) {
		return new StatementImpl(toSesameResource(theStmt.getSubject()), 
								 toSesameURI(theStmt.getPredicate()),
								 toSesameValue(theStmt.getObject()));
	}

	public static URI toSesameURI(final fourstore.api.rdf.URI theURI) {
		return SesameValueFactory.instance().createURI(theURI.getURI());
	}

	public static BNode toSesameBNode(final fourstore.api.rdf.BNode theBNode) {
		return SesameValueFactory.instance().createBNode(theBNode.getId());
	}

	public static Resource toSesameResource(final fourstore.api.rdf.Resource theResource) {
		if (theResource instanceof fourstore.api.rdf.URI) {
			return toSesameURI( (fourstore.api.rdf.URI) theResource);
		}
		else {
			return toSesameBNode( (fourstore.api.rdf.BNode) theResource);
		}
	}

	public static Value toSesameValue(final fourstore.api.rdf.Value theValue) {
		if (theValue instanceof fourstore.api.rdf.Resource) {
			return toSesameResource( (fourstore.api.rdf.Resource) theValue);
		}
		else {
			return toSesameLiteral( (fourstore.api.rdf.Literal) theValue);
		}
	}

	public static Literal toSesameLiteral(final fourstore.api.rdf.Literal theLiteral) {
		if (theLiteral.getLanguage() != null) {
			return SesameValueFactory.instance().createLiteral(theLiteral.getValue(),
															   theLiteral.getLanguage());
		}
		else if (theLiteral.getDatatype() != null) {
			return SesameValueFactory.instance().createLiteral(theLiteral.getValue(),
															   toSesameURI(theLiteral.getDatatype()));
		}
		else {
			return SesameValueFactory.instance().createLiteral(theLiteral.getValue());
		}
	}

}
