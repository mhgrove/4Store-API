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

/**
 * <p>A stub implementation of the TransactionSupport interface which does not actually provide any transactional
 * support.  Intended for use in cases where a base sail does not natively support transactions and the client
 * does not need them or does not want the client side performance hit of emulating them.</p>
 *
 * @author Michael Grove
 * @version 0.3
 * @since 0.3
 */
public class NoOpTransactionSupport implements TransactionSupport {

	/**
	 * Whether or not a transaction is active
	 */
	private boolean mActive = false;

	/**
	 * @inheritDoc
	 */
	public void commit() {
		mActive = false;
	}

	/**
	 * @inheritDoc
	 */
	public void rollback() {
		mActive = false;
	}

	/**
	 * @inheritDoc
	 */
	public void begin() {
		mActive = true;
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
		// no-op
	}

	/**
	 * @inheritDoc
	 */
	public void statementRemoved(final Statement theStatement) {
		// no-op
	}
}
