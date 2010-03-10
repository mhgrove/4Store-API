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
	 */
	public void commit() throws SailException;

	/**
	 * Rollback the changes in the current transaction
	 */
	public void rollback() throws SailException;

	/**
	 * Start a transaction
	 */
	public void begin() throws SailException;

	/**
	 * Return whether or not a transaction is current active
	 * @return true if a transaction is active, false otherwise
	 */
	public boolean isActive();
}
