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

package com.clarkparsia.fourstore.api.rdf;

/**
 * <p>Factory interface for creating instances of the 4Store API</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
public interface ValueFactory {

	/**
	 * Create a new URI concept
	 * @param theURI the URI
	 * @return a new URI object with the given URI
	 */
	public URI createURI(String theURI);

	/**
	 * Create a new BNode
	 * @return the new BNode
	 */
	public BNode createBNode();

	/**
	 * Create a new BNode
	 * @param theId the id of the BNode
	 * @return a BNode with the given id
	 */
	public BNode createBNode(String theId);

	/**
	 * Create a new Literal
	 * @param theValue the value of the literal
	 * @return a Literal
	 */
	public Literal createLiteral(String theValue);

	/**
	 * Create a new typed Literal
	 * @param theValue the value of the literal
	 * @param theDatatype the datatype of the Literal
	 * @return a Literal
	 */
	public Literal createLiteral(String theValue, URI theDatatype);

	/**
	 * Create a new Literal with a language attribute.
	 * @param theValue the value of the Literal
	 * @param theLang the language of the literal
	 * @return a Literal
	 */
	public Literal createLiteral(String theValue, String theLang);

	// TODO: typed literals, eg createLiteral(boolean) creates a boolean-typed literal
}
