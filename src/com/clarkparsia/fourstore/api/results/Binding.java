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

package com.clarkparsia.fourstore.api.results;

import com.clarkparsia.fourstore.api.rdf.BNode;
import com.clarkparsia.fourstore.api.rdf.Literal;
import com.clarkparsia.fourstore.api.rdf.URI;
import com.clarkparsia.fourstore.api.rdf.Value;

/**
 * Description: <p>A single result binding for a query.  Associates variable names in the project with their values in
 * the binding.</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
public interface Binding {

	/**
	 * Return the value bound to the specified variable in the results.
	 * @param theKey the variable
	 * @return the value of the variable for this binding, or null if the variable does not exist or is not bound
	 */
	public Value get(String theKey);

	/**
	 * Return the {@link URI} value bound to the variable name
	 * @param theKey the variable name
	 * @return the URI value of the variable, or null if it is not bound, does not exist, or is not a URI.
	 */
	public URI uri(String theKey);

	/**
	 * Return the {@link BNode} value bound to the variable name.
	 * @param theKey the variable name
	 * @return the BNode value of the variable, or null if it is not bound, does not exist, or is not a BNode.
	 */
	public BNode bnode(String theKey);

	/**
	 * Return the {@link Literal} value bound to the variable name.
	 * @param theKey the variable name
	 * @return the Literal value of the variable, or null if it is not bound, does not exist, or is not a Literal
	 */
	public Literal literal(String theKey);

	/**
	 * Return an Iterable of the variable names in this Binding
	 * @return the variable names
	 */
	public Iterable<String> variables();
}
