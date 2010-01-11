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

package com.clarkparsia.fourstore.impl.rdf;

import com.clarkparsia.fourstore.api.rdf.Literal;
import com.clarkparsia.fourstore.api.rdf.URI;

import static com.clarkparsia.utils.BasicUtils.equalsOrNull;

/**
 * <p>Implementation of the {@link Literal} interface.</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
public class LiteralImpl extends ValueImpl implements Literal {
	private String mLanguage;
	private String mValue;
	private URI mDatatype;

	LiteralImpl(String theValue, String theLang, URI theDatatype) {
		mValue = theValue;
		mLanguage = theLang;
		mDatatype = theDatatype;
	}

	/**
	 * @inheritDoc
	 */
	public String getLanguage() {
		return mLanguage;
	}

	/**
	 * @inheritDoc
	 */
	public String getValue() {
		return mValue;
	}

	/**
	 * @inheritDoc
	 */
	public URI getDatatype() {
		return mDatatype;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String toString() {
		return mValue;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public int hashCode() {
		return mValue.hashCode() + (getLanguage() == null ? 0 : getLanguage().hashCode()) + (getDatatype() == null ? 0 : getDatatype().hashCode());
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean equals(Object theObj) {
		if (theObj instanceof Literal) {
			Literal aLit = (Literal) theObj;

			return getValue().equals(aLit.getValue()) &&
				   equalsOrNull(getLanguage(), aLit.getLanguage()) &&
				   equalsOrNull(getDatatype(), aLit.getDatatype());
		}
		else {
			return false;
		}
	}
}
