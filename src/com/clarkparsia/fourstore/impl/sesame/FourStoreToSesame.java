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
 * <p>Utility functions to convert from 4Store API to Sesame</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
public class FourStoreToSesame {
	
	public static Graph toSesameGraph(com.clarkparsia.fourstore.api.rdf.Graph theFourStoreGraph) {
		Graph aGraph = new GraphImpl();
		
		for (com.clarkparsia.fourstore.api.rdf.Statement aStmt : theFourStoreGraph) {
			aGraph.add(toSesameStatement(aStmt));
		}
		
		return aGraph;
	}

	public static Statement toSesameStatement(final com.clarkparsia.fourstore.api.rdf.Statement theStmt) {
		return new StatementImpl(toSesameResource(theStmt.getSubject()), 
								 toSesameURI(theStmt.getPredicate()),
								 toSesameValue(theStmt.getObject()));
	}

	public static URI toSesameURI(final com.clarkparsia.fourstore.api.rdf.URI theURI) {
		return SesameValueFactory.instance().createURI(theURI.getURI());
	}

	public static BNode toSesameBNode(final com.clarkparsia.fourstore.api.rdf.BNode theBNode) {
		return SesameValueFactory.instance().createBNode(theBNode.getId());
	}

	public static Resource toSesameResource(final com.clarkparsia.fourstore.api.rdf.Resource theResource) {
		if (theResource instanceof com.clarkparsia.fourstore.api.rdf.URI) {
			return toSesameURI( (com.clarkparsia.fourstore.api.rdf.URI) theResource);
		}
		else {
			return toSesameBNode( (com.clarkparsia.fourstore.api.rdf.BNode) theResource);
		}
	}

	public static Value toSesameValue(final com.clarkparsia.fourstore.api.rdf.Value theValue) {
		if (theValue instanceof com.clarkparsia.fourstore.api.rdf.Resource) {
			return toSesameResource( (com.clarkparsia.fourstore.api.rdf.Resource) theValue);
		}
		else {
			return toSesameLiteral( (com.clarkparsia.fourstore.api.rdf.Literal) theValue);
		}
	}

	public static Literal toSesameLiteral(final com.clarkparsia.fourstore.api.rdf.Literal theLiteral) {
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
