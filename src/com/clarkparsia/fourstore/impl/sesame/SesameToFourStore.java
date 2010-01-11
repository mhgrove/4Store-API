/*
 * Copyright (c) 2005-2010 Clark & Parsia, LLC. <http://www.clarkparsia.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.clarkparsia.fourstore.impl.sesame;

import com.clarkparsia.fourstore.api.rdf.BNode;
import com.clarkparsia.fourstore.api.rdf.Graph;
import com.clarkparsia.fourstore.api.rdf.Resource;
import com.clarkparsia.fourstore.api.rdf.Statement;
import com.clarkparsia.fourstore.impl.rdf.FourStoreValueFactory;
import com.clarkparsia.fourstore.impl.rdf.GraphImpl;
import com.clarkparsia.fourstore.impl.rdf.StatementImpl;
import com.clarkparsia.fourstore.api.rdf.URI;
import org.openrdf.sesame.sail.StatementIterator;

/**
 * <p>Utility functions to convert from Sesame API to 4Store.</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
public class SesameToFourStore {
	private static FourStoreValueFactory FACTORY = new FourStoreValueFactory();

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

	private static com.clarkparsia.fourstore.api.rdf.Value toValue(final org.openrdf.model.Value theValue) {
		if (theValue instanceof org.openrdf.model.Resource) {
			return toResource((org.openrdf.model.Resource) theValue);
		}

		org.openrdf.model.Literal aLit = (org.openrdf.model.Literal) theValue;

		if (aLit.getDatatype() != null) {
			return FACTORY.createLiteral(aLit.getLabel(), toURI(aLit.getDatatype()));
		}
		else if (aLit.getLanguage() != null) {
			return FACTORY.createLiteral(aLit.getLabel(), aLit.getLanguage());
		}
		else {
			return FACTORY.createLiteral(aLit.getLabel());
		}
	}

	private static Resource toResource(final org.openrdf.model.Resource theResource) {
		return theResource instanceof org.openrdf.model.URI ? toURI((org.openrdf.model.URI) theResource) : toBNode( (org.openrdf.model.BNode) theResource);
	}

	private static BNode toBNode(final org.openrdf.model.BNode theBNode) {
		return FACTORY.createBNode(theBNode.getID());
	}

	private static URI toURI(final org.openrdf.model.URI theURI) {
		return FACTORY.createURI(theURI.getURI());
	}
}
