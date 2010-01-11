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
import com.clarkparsia.fourstore.api.results.Binding;
import com.clarkparsia.fourstore.impl.results.BindingImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Simple builder class for creating a Binding instance</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
class BindingBuilder {
	private Map<String, Value> mValues = new HashMap<String, Value>();

	public void reset() {
		mValues.clear();
	}

	public Binding binding() {
		return new BindingImpl(new HashMap<String, Value>(mValues));
	}

	public BindingBuilder append(String theKey, Value theValue) {
		mValues.put(theKey, theValue);
		return this;
	}
}
