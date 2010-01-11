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
 * <p>Interface for a class which can format a {@link ResultSet} as a String.</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
public interface ResultSetFormatter {

	/**
	 * Format the given {@link ResultSet} as a String.
	 * @param theResultSet the result set to format
	 * @return the result set as a String.
	 */
	public String format(ResultSet theResultSet);
}
