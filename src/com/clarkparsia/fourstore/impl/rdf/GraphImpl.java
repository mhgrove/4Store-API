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

import com.clarkparsia.utils.BasicUtils;
import com.clarkparsia.utils.Predicate;
import com.clarkparsia.utils.collections.CollectionUtil;
import com.clarkparsia.fourstore.api.rdf.Graph;
import com.clarkparsia.fourstore.api.rdf.Resource;
import com.clarkparsia.fourstore.api.rdf.Statement;
import com.clarkparsia.fourstore.api.rdf.URI;
import com.clarkparsia.fourstore.api.rdf.Value;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p>Simple implemtentation of the Graph interface.  This uses a Set of Statements and is only suitable for small,
 * in-memory graphs.</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
public class GraphImpl implements Graph {
	private Set<Statement> mStatements = new LinkedHashSet<Statement>();

	/**
	 * @inheritDoc
	 */
	public Iterator<Statement> iterator() {
		return Collections.unmodifiableSet(mStatements).iterator();
	}

	/**
	 * @inheritDoc
	 */
	public Iterator<Statement> getStatements() {
		return iterator();
	}

	/**
	 * @inheritDoc
	 */
	public Iterator<Statement> getStatements(final Resource theSubj, final URI thePred, final Value theObj) {
		return CollectionUtil.filter(mStatements, new Predicate<Statement>() {
			public boolean accept(final Statement theValue) {
				return BasicUtils.equalsOrNull(theValue.getSubject(), theSubj)
						&& BasicUtils.equalsOrNull(theValue.getPredicate(), thePred)
						&& BasicUtils.equalsOrNull(theValue.getObject(), theObj);
			}
		}).iterator();
	}

	/**
	 * @inheritDoc
	 */
	public void addStatement(final Statement theStatement) {
		mStatements.add(theStatement);
	}

	/**
	 * @inheritDoc
	 */
	public void addStatement(final Resource theSubj, final URI thePred, final Value theObj) {
		mStatements.add(new StatementImpl(theSubj, thePred, theObj));
	}

	/**
	 * @inheritDoc
	 */
	public void removeStatement(final Statement theStatement) {
		mStatements.remove(theStatement);
	}

	/**
	 * @inheritDoc
	 */
	public void removeStatement(final Resource theSubj, final URI thePred, final Value theObj) {
		Collection<Statement> toRemove = null;

		if (theSubj != null && thePred != null && theObj != null) {
			// TODO: fix this generics issue
			toRemove = CollectionUtil.set( (Statement) new StatementImpl(theSubj, thePred, theObj));
		}
		else {
			CollectionUtil.filter(mStatements, new StatementMatcher(theSubj, thePred, theObj));
		}

		mStatements.removeAll(toRemove);
	}

	/**
	 * @inheritDoc
	 */
	public boolean hasStatement(final Statement theStatement) {
		return mStatements.contains(theStatement);
	}

	/**
	 * @inheritDoc
	 */
	public boolean hasStatement(final Resource theSubj, final URI thePred, final Value theObj) {
		if (theSubj != null && thePred != null && theObj != null) {
			return mStatements.contains(new StatementImpl(theSubj, thePred, theObj));
		}
		else {
			return CollectionUtil.find(mStatements, new StatementMatcher(theSubj, thePred, theObj));
		}
	}

	/**
	 * @inheritDoc
	 */
	public int size() {
		return mStatements.size();
	}

	/**
	 * @inheritDoc
	 */
	public void clear() {
		mStatements.clear();
	}

	private static class StatementMatcher implements Predicate<Statement> {
		private final Resource mSubj;
		private final URI mPred;
		private final Value mObj;

		public StatementMatcher(final Resource theSubj, final URI thePred, final Value theObj) {
			mSubj = theSubj;
			mPred = thePred;
			mObj = theObj;
		}

		public boolean accept(final Statement theValue) {
			return BasicUtils.equalsOrNull(theValue.getSubject(), mSubj)
				   && BasicUtils.equalsOrNull(theValue.getPredicate(), mPred)
				   && BasicUtils.equalsOrNull(theValue.getObject(), mObj);
		}
	}
}
