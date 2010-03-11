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
 * @since 0.3
 * @version 0.3.1
 *
 * @see FourStoreSail
 * @see FourStoreRepositoryConnection
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
	 * Create a new FourStoreSailRepository
	 * @param theURL the URL of the 4Store instance
	 * @param theUseGet whether or not to use GET requests for SPARQL queries.
	 */
	public FourStoreSailRepository(URL theURL, boolean theUseGet) {
		super(new FourStoreSail(theURL, theUseGet));
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
