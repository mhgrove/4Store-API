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

import com.clarkparsia.fourstore.api.rdf.URI;

/**
 * <p>Implementation of the {@link URI} interface.</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
public class URIImpl extends ResourceImpl implements URI {
	private String mURI;

	URIImpl(final String theURI) {
		mURI = theURI;
	}

	/**
	 * @inheritDoc
	 */
	public String getURI() {
		return mURI;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String toString() {
		return getURI();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public int hashCode() {
		return getURI().hashCode();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean equals(Object theObj) {
		return (theObj instanceof URI) && ((URI)theObj).getURI().equals(getURI());
	}
}
