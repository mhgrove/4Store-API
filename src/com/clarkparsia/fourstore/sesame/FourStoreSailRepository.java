package com.clarkparsia.fourstore.sesame;

import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.sail.SailException;

import java.net.URL;

/**
 * <p>Extends the normal Sesame SailRepository to provide a Repository implementation for 4store.</p>
 *
 * @author Michael Grove
 */
public class FourStoreSailRepository extends SailRepository {

	/**
	 * Create a new FourStoreSailRepository
	 * @param theSail the FourStoreSail to use
	 */
	public FourStoreSailRepository(FourStoreSail theSail) {
		super(theSail);
	}

	/**
	 * Create a new FourStoreSailRepository
	 * @param theURL the URL of the 4Store instance
	 */
	public FourStoreSailRepository(URL theURL) {
		super(new FourStoreSail(theURL));
	}

	/**
	 * Return the underlying FourStoreSail of this repository
	 * @return the FourStoreSail
	 */
	protected FourStoreSail getFourStoreSail() {
		return (FourStoreSail) getSail();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public SailRepositoryConnection getConnection() throws RepositoryException {
		try {
			return new FourStoreRepositoryConnection(this);
		}
		catch (SailException e) {
			throw new RepositoryException(e);
		}
	}
}
