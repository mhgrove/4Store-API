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

package com.clarkparsia.fourstore.impl;

import com.clarkparsia.fourstore.api.Store;

import com.clarkparsia.utils.web.Method;

import java.net.URL;

/**
 * <p>Factory class for getting instances of the Store interface</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
public class StoreFactory {

	/**
	 * Create a new instance of a {@link Store}
	 * @param theURL the URL of the store http endpoint
	 * @return an instance of a Store
	 */
	public static Store create(URL theURL) {
		return new StoreImpl(theURL);
	}

	/**
	 * Create a new instance of a {@link Store}
	 * @param theURL the URL of the store endpoint
	 * @param theQueryMethod the HTTP method to use for queries
	 * @return an instance of a Store
	 */
	public static Store create(URL theURL, Method theQueryMethod) {
		if (theQueryMethod != Method.POST && theQueryMethod != Method.GET) {
			throw new IllegalArgumentException("Queries can only be made as POST or GET");
		}

		return new StoreImpl(theURL, theQueryMethod == Method.GET);
	}
}
