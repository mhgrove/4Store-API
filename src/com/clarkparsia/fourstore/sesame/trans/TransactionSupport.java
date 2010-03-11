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

import org.openrdf.sail.SailConnectionListener;
import org.openrdf.sail.SailException;

/**
 * <p>Interface to support transactions on a Sesame Sail.  It's integration into a Sail can be at a level sufficient
 * to implement Read-Committed or Read-Uncommitted isolation levels.</p>
 *
 * @author Michael Grove
 */
public interface TransactionSupport extends SailConnectionListener {

	/**
	 * Commit the current transaction
	 * @throws org.openrdf.sail.SailException if there is an error while committing
	 */
	public void commit() throws SailException;

	/**
	 * Rollback the changes in the current transaction
	 * @throws org.openrdf.sail.SailException if there is an error rolling back operations
	 */
	public void rollback() throws SailException;

	/**
	 * Start a transaction
	 * @throws org.openrdf.sail.SailException if there is an error starting the transaction
	 */
	public void begin() throws SailException;

	/**
	 * Return whether or not a transaction is current active
	 * @return true if a transaction is active, false otherwise
	 */
	public boolean isActive();
}
