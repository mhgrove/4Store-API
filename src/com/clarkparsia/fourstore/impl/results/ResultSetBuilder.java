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

import com.clarkparsia.fourstore.api.rdf.Value;
import com.clarkparsia.fourstore.api.rdf.ValueFactory;

import com.clarkparsia.fourstore.api.results.Binding;
import com.clarkparsia.fourstore.api.results.ResultSet;

import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>Builder class for creating a 4Store ResultSet</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
public class ResultSetBuilder {
	private Collection<Binding> mValues = new ArrayList<Binding>();
	private BindingBuilder mCurrBinding = new BindingBuilder();
	private ValueFactory mFactory;

	public ResultSetBuilder(final ValueFactory theFactory) {
		mFactory = theFactory;
	}

	public void reset() {
		mValues.clear();
	}

	public void startResultSet() {
		reset();
	}

	public ResultSet endResultSet() {
		return resultSet();
	}

	public void startBinding() {
		mCurrBinding.reset();
	}

	public ValueFactory getValueFactory() {
		return mFactory;
	}

	public void endBinding() {
		mValues.add(mCurrBinding.binding());
	}

	public BindingBuilder addToBinding(String theKey, Value theValue) {
		return mCurrBinding.append(theKey, theValue);
	}

	public ResultSet resultSet() {
		return new ResultSetImpl(mValues);
	}
}
