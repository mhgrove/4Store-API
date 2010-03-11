/*
 * Copyright (c) 2009-2010 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package com.clarkparsia.fourstore.sesame.trans;

import org.openrdf.model.Statement;
import org.openrdf.model.Graph;
import org.openrdf.model.impl.GraphImpl;

import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;

import java.util.HashSet;
import java.util.Collection;

/**
 * <p>Simple implementation of read-uncommitted transaction isolation level support.</p>
 *
 * @author Michael Grove
 * @version 0.3
 * @since 0.3
 */
public class NaiveTransactionSupport implements TransactionSupport {

	/**
	 * Whether or not a transaction is active
	 */
	private boolean mActive = false;

	/**
	 * The list of things that have been added during this transaction
	 */
	private Graph mAdded;

	/**
	 * The list of things that have been removed in this transaction
	 */
	private Graph mRemoved;

	/**
	 * The connection all the changes will be committed to
	 */
	private SailConnection mConnection;

	/**
	 * Create a new NaiveTransactionSupport
	 * @param theConnection the connection the changes will be committed to
	 */
	public NaiveTransactionSupport(final SailConnection theConnection) {
		mConnection = theConnection;

		mAdded = new GraphImpl();
		mRemoved = new GraphImpl();
	}

	/**
	 * @inheritDoc
	 */
	public void commit() throws SailException  {
		// nothing much to do since we allow all the operations to be performed on the underlying database,
		// they're already committed.

		mActive = false;

		mAdded.clear();
		mRemoved.clear();
	}

	/**
	 * @inheritDoc
	 */
	public void rollback() throws SailException {
		mActive = false;

		// making these temporary collections to iterate over and affect the rollback.  unfortunately, i was trying
		// to be generic with this, which might have been a mistake; we use the parent connection to add/remove
		// the statements, and this class is a listener for adds/removes, so you get called back for the adds and
		// removes during the rollback, which is not ideal.  the right thing to use is probably the transaction sail
		// or tap into native transaction support, but for now, this will have to do.
		Collection<Statement> aStmtsToAdd = new HashSet<Statement>(mRemoved);
		Collection<Statement> aStmtsToRemove = new HashSet<Statement>(mAdded);

		for (Statement aStmt : aStmtsToAdd) {
			mConnection.addStatement(aStmt.getSubject(), aStmt.getPredicate(), aStmt.getObject(), aStmt.getContext());
		}

		for (Statement aStmt : aStmtsToRemove) {
			mConnection.removeStatements(aStmt.getSubject(), aStmt.getPredicate(), aStmt.getObject(), aStmt.getContext());
		}

		mAdded.clear();
		mRemoved.clear();
	}

	/**
	 * @inheritDoc
	 */
	public void begin() throws SailException {
		mActive = true;
		mAdded.clear();
		mRemoved.clear();
	}

	/**
	 * @inheritDoc
	 */
	public boolean isActive() {
		return mActive;
	}

	/**
	 * @inheritDoc
	 */
	public void statementAdded(final Statement theStatement) {
		mAdded.add(theStatement);
		mRemoved.remove(theStatement);
	}

	/**
	 * @inheritDoc
	 */
	public void statementRemoved(final Statement theStatement) {
		mRemoved.add(theStatement);
		mAdded.remove(theStatement);
	}
}
