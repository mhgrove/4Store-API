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

package com.clarkparsia.fourstore.impl.results;

import com.clarkparsia.fourstore.api.rdf.BNode;
import com.clarkparsia.fourstore.api.rdf.Literal;
import com.clarkparsia.fourstore.api.rdf.URI;
import com.clarkparsia.fourstore.api.rdf.Value;
import com.clarkparsia.fourstore.api.results.Binding;

import java.util.Collections;
import java.util.Map;

/**
 * <p>Implementation of the Binding interface</p>
 *
 * @author Michael Grove
 * @see URI
 * @see Literal
 * @see BNode
 * @see Value
 * @since 0.1
 */
public class BindingImpl implements Binding {
	private Map<String, Value> mValues;

	BindingImpl(Map<String, Value> theValues) {
		mValues = theValues;
	}

	public Value get(String theKey) {
		return mValues.get(theKey);
	}

	public URI uri(String theKey) {
		return safeGet(theKey, URI.class);
	}

	public BNode bnode(String theKey) {
		return safeGet(theKey, BNode.class);
	}

	public Literal literal(String theKey) {
		return safeGet(theKey, Literal.class);
	}

	public Iterable<String> variables() {
		return Collections.unmodifiableCollection(mValues.keySet());
	}

	private <T> T safeGet(String theKey, Class<T> theClass) {
		try {
			if (mValues.containsKey(theKey)) {
				return theClass.cast(mValues.get(theKey));
			}
			else {
				return null;
			}
		}
		catch (ClassCastException ex) {
			// this is ok, it's the point of safe get, we'll just return null
			// we're treating get me var "foo" as a URI the same as get me var "foo" when "foo" does not exist
			return null;
		}
	}
}
