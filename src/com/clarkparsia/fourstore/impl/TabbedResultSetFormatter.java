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

import com.clarkparsia.utils.collections.CollectionUtil;
import com.clarkparsia.fourstore.api.results.Binding;
import com.clarkparsia.fourstore.api.results.ResultSet;
import com.clarkparsia.fourstore.api.results.ResultSetFormatter;

import java.util.List;

/**
 * <p>Simple result set formatter for displaying a result set in a tab separated table.</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
public class TabbedResultSetFormatter implements ResultSetFormatter {
	public String format(final ResultSet theResultSet) {
		StringBuffer aBuffer = new StringBuffer();

		if (theResultSet.isEmpty()) {
			aBuffer.append("*** No Results ***");
		}
		else {
			// TODO: use some of the text formatting stuff to get the columns to properly align

			Binding aVarsBinding = theResultSet.iterator().next();
			List<String> aVars = CollectionUtil.list(aVarsBinding.variables());
			for (String aVar : aVars) {
				aBuffer.append(aVar);
				aBuffer.append("\t");
			}
			aBuffer.append("\n---\n");

			for (Binding aBinding : theResultSet) {
				for (String aVar : aVars) {
					aBuffer.append(aBinding.get(aVar));
					aBuffer.append("\t");
				}
				aBuffer.append("\n");
			}
		}

		return aBuffer.toString();
	}
}
