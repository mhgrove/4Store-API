package com.clarkparsia.fourstore.sesame;

import org.openrdf.sail.helpers.NotifyingSailBase;
import org.openrdf.sail.NotifyingSailConnection;
import org.openrdf.sail.SailException;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import java.net.URL;
import java.net.ConnectException;

import com.clarkparsia.fourstore.api.Store;
import com.clarkparsia.fourstore.impl.StoreFactory;

/**
 * <p>Implementation of a Sesame Sail which is backed by an instance of 4Store.</p>
 *
 * @author Michael Grove
 * @since 0.3
 * @version 0.3
 */
public class FourStoreSail extends NotifyingSailBase {

	/**
	 * The underlying 4Store instance.
	 */
	private Store mStore;

	/**
	 * The value factory for this Sail.
	 */
	private ValueFactory mValueFactory = new ValueFactoryImpl();

	/**
	 * Create a new FourStoreSail
	 * @param theURL the URL of the 4Store instance
	 */
	public FourStoreSail(URL theURL) {
		mStore = StoreFactory.create(theURL);
	}

	/**
	 * @inheritDoc
	 */
	protected void shutDownInternal() throws SailException {
		try {
			getFourStore().disconnect();
		}
		catch (ConnectException e) {
			throw new SailException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	protected NotifyingSailConnection getConnectionInternal() throws SailException {
		return new FourStoreSailConnection(this);
	}

	/**
	 * @inheritDoc
	 */
	public boolean isWritable() throws SailException {
		return true;
	}

	/**
	 * @inheritDoc
	 */
	public ValueFactory getValueFactory() {
		return mValueFactory;
	}

	/**
	 * Return the 4Store instance backing this Sail.
	 * @return the underlying 4Store repository
	 */
	Store getFourStore() {
		return mStore;
	}
}
