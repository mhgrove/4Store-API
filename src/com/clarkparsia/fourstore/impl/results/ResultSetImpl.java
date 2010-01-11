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

import com.clarkparsia.fourstore.api.results.Binding;
import com.clarkparsia.fourstore.impl.results.BindingImpl;
import com.clarkparsia.fourstore.api.results.ResultSet;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

/**
 * <p>The implementation of the ResultSet interface</p>
 *
 * @author Michael Grove
 * @see com.clarkparsia.fourstore.api.results.Binding
 * @see com.clarkparsia.fourstore.api.results.ResultSet
 * @see BindingImpl
 * @since 0.1
 */
public class ResultSetImpl implements ResultSet {
	/**
	 * The bindings that make up the result set
	 */
	private Collection<Binding> mBindings = new HashSet<Binding>();

	/**
	 * Create a new ResultSetImpl.  Package Protected, use {@link com.clarkparsia.fourstore.impl.results.ResultSetBuilder} to create ResultSets.
	 * @param theBindings the list of bindings for the result set.
	 */
	ResultSetImpl(Collection<Binding> theBindings) {
		mBindings = theBindings;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * @inheritDoc
	 */
	public int size() {
		return mBindings.size();
	}

	/**
	 * @inheritDoc
	 */
	public Iterator<Binding> iterator() {
		return Collections.unmodifiableCollection(mBindings).iterator();
	}
}
