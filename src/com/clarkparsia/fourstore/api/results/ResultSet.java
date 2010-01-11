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

/**
 * <p>The results of a query as an Iterable set of {@link Binding Bindings}</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
public interface ResultSet extends Iterable<Binding> {
	/**
	 * Returns the number of results in the result set
	 * @return the size of the result set
	 */
	public int size();

	/**
	 * Returns whether or not the result set is empty
	 * @return true if the result set is empty, false otherwise.
	 */
	public boolean isEmpty();
}
