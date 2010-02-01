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

import com.clarkparsia.utils.collections.CollectionUtil;

import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;

import org.openrdf.query.BindingSet;
import org.openrdf.query.impl.MapBindingSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <p>Builder class for creating a 4Store ResultSet</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
public class ResultSetBuilder {
	private Collection<BindingSet> mValues = new ArrayList<BindingSet>();
	private MapBindingSet mCurrBinding = new MapBindingSet();
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

	public void endResultSet() {
	}

	public void startBinding() {
		mCurrBinding = new MapBindingSet();
	}

	public ValueFactory getValueFactory() {
		return mFactory;
	}

	public void endBinding() {
		mValues.add(mCurrBinding);
	}

	public ResultSetBuilder addToBinding(String theKey, Value theValue) {
		mCurrBinding.addBinding(theKey, theValue);

        return this;
	}

    public Collection<BindingSet> bindingSet() {
        return mValues;
    }

    public List<String> bindingNames() {
        if (mValues.isEmpty()) {
            return Collections.emptyList();
        }
        else {
            return CollectionUtil.list(mValues.iterator().next().getBindingNames());
        }
    }
}
