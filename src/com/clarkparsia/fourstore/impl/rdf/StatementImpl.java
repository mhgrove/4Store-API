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

import com.clarkparsia.fourstore.api.rdf.Resource;
import com.clarkparsia.fourstore.api.rdf.Statement;
import com.clarkparsia.fourstore.api.rdf.URI;
import com.clarkparsia.fourstore.api.rdf.Value;

/**
 * <p>Implementation of the {@link Statement} interface.</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
public class StatementImpl implements Statement {
	private Resource mSubject;
	private URI mPredicate;
	private Value mObject;

	private int mHashCode;

	public StatementImpl(final Resource theSubject, final URI thePredicate, final Value theObject) {
		if (theSubject == null || thePredicate == null || theObject == null) {
			throw new IllegalArgumentException("Cannot have null arguments to a statement");
		}

		mSubject = theSubject;
		mPredicate = thePredicate;
		mObject = theObject;

		mHashCode = mSubject.hashCode();
		mHashCode = 31 * mHashCode + mPredicate.hashCode();
		mHashCode = 37 * mHashCode + mObject.hashCode();
	}

	/**
	 * @inheritDoc
	 */
	public Resource getSubject() {
		return mSubject;
	}

	/**
	 * @inheritDoc
	 */
	public URI getPredicate() {
		return mPredicate;
	}

	/**
	 * @inheritDoc
	 */
	public Value getObject() {
		return mObject;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean equals(final Object theObject) {
		if (this == theObject) {
			return true;
		}

		if (theObject == null || !(theObject instanceof Statement)) {
			return false;
		}

		final StatementImpl aStmt = (StatementImpl) theObject;

		return getSubject().equals(aStmt.getSubject())
			   && getPredicate().equals(aStmt.getPredicate())
			   && getObject().equals(aStmt.getObject());
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public int hashCode() {
		return mHashCode;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String toString() {
		return "(" + getSubject() + ", " + getPredicate() + ", " + getObject() + ")";
	}
}
