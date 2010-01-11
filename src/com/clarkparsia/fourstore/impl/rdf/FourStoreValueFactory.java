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

package com.clarkparsia.fourstore.impl.rdf;

import com.clarkparsia.fourstore.api.rdf.BNode;
import com.clarkparsia.fourstore.api.rdf.Literal;
import com.clarkparsia.fourstore.api.rdf.URI;
import com.clarkparsia.fourstore.api.rdf.ValueFactory;

/**
 * <p>Implementation of the {@link ValueFactory} interface</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
public class FourStoreValueFactory implements ValueFactory {
	private static int mBNodeId = 0;

	public URI createURI(final String theURI) {
		return new URIImpl(theURI);
	}

	public BNode createBNode() {
		return new BNodeImpl(String.valueOf(mBNodeId++));
	}

	public BNode createBNode(final String theId) {
		return new BNodeImpl(theId);
	}

	public Literal createLiteral(final String theValue) {
		return new LiteralImpl(theValue, null, null);
	}

	public Literal createLiteral(final String theValue, final URI theDatatype) {
		return new LiteralImpl(theValue, null, theDatatype);
	}

	public Literal createLiteral(final String theValue, final String theLang) {
		return new LiteralImpl(theValue, theLang, null);
	}
}
