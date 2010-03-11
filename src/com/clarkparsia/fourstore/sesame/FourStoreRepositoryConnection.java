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

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryConnection;

import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.repository.sail.SailTupleQuery;
import org.openrdf.repository.sail.SailGraphQuery;

import org.openrdf.sail.SailException;
import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

import org.openrdf.query.QueryLanguage;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.impl.GraphQueryResultImpl;
import org.openrdf.query.impl.TupleQueryResultBuilder;
import org.openrdf.query.parser.sparql.SPARQLParserFactory;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.QueryParserFactory;
import org.openrdf.query.parser.QueryParserUtil;
import org.openrdf.query.parser.ParsedGraphQuery;
import org.openrdf.query.parser.ParsedTupleQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.File;

import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;

import info.aduna.iteration.Iteration;

import com.clarkparsia.utils.io.IOUtil;
import com.clarkparsia.utils.io.Encoder;

import static com.clarkparsia.utils.collections.CollectionUtil.set;
import com.clarkparsia.openrdf.util.GraphBuildingRDFHandler;
import com.clarkparsia.openrdf.query.sparql.SPARQLQueryRenderer;

/**
 * <p>Extends the Sesame SailRepositoryConnection class to provide a connection interface to a remote 4store instance.
 * Overrides the default behavior for adds, removes and queries so they operate directly on the 4store instance instead
 * of going through the Sail/Repository API which just unnecessarily slows down the performance of the repository.</p>
 *
 * @author Michael Grove
 */
public class FourStoreRepositoryConnection extends SailRepositoryConnection {
	/**
	 * The logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger("4store");

	/**
	 * Create a new FourStoreRepositoryConnection
	 * @param theFourStoreSail the instance of four store to operate on
	 * @throws SailException thrown if a connection cannot be established.
	 */
	protected FourStoreRepositoryConnection(final FourStoreSailRepository theFourStoreSail) throws SailException {
		super(theFourStoreSail, theFourStoreSail.getFourStoreSail().getConnection());
	}

	/**
	 * Return the underlying SailConnection casted as a {@link FourStoreSailConnection}.
	 * @return the SailConnection as a FourStoreSailConnection
	 */
	private FourStoreSailConnection getFourStoreSailConnection() {
		// this is a safe cast as the underlying sail can only ever be a FourStoreSail
		return (FourStoreSailConnection) getSailConnection();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public SailTupleQuery prepareTupleQuery(final QueryLanguage theQueryLanguage, final String theQuery) throws MalformedQueryException {
		return prepareTupleQuery(theQueryLanguage, theQuery, "http://4store.clarkparsia.com");
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public SailTupleQuery prepareTupleQuery(final QueryLanguage theQueryLanguage, final String theQuery, final String theBaseURI) throws MalformedQueryException {
		ParsedTupleQuery aQuery = QueryParserUtil.parseTupleQuery(theQueryLanguage, theQuery, theBaseURI);

		return new FourStoreSailTupleQuery(aQuery, this);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public SailGraphQuery prepareGraphQuery(final QueryLanguage theQueryLanguage, final String theQuery) throws MalformedQueryException {
		return prepareGraphQuery(theQueryLanguage, theQuery, "http://4store.clarkparsia.com/");
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public SailGraphQuery prepareGraphQuery(final QueryLanguage theQueryLanguage, final String theQuery, final String theBaseURI) throws MalformedQueryException {
		ParsedGraphQuery aQuery = QueryParserUtil.parseGraphQuery(theQueryLanguage,  theQuery, theBaseURI);

		return new FourStoreSailGraphQuery(aQuery, this);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void add(final InputStream theInputStream, final String theBaseURI, final RDFFormat theRDFFormat, final Resource... theContexts) throws IOException, RDFParseException, RepositoryException {
		if (theBaseURI == null || theBaseURI.length() > 0) {
			LOGGER.warn("Ignoring specified base URI");
		}
		
		try {
			getFourStoreSailConnection().add(theInputStream, theRDFFormat, theContexts);
		}
		catch (SailException e) {
			throw new RepositoryException(e);
		}
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	public void add(final Reader theReader, final String theBaseURI, final RDFFormat theRDFFormat, final Resource... theContexts) throws IOException, RDFParseException, RepositoryException {
		// this is not terribly efficient, reading in the contents of the reader like this.  but our 4store api doesn't
		// expose something to handle Reader's since you cannot pass a Reader to an HTTPConnection.
		// could always do something like this:  http://www.koders.com/java/fid0A51E45C950B2B8BD9365C19F2626DE35EC09090.aspx

		add(new ByteArrayInputStream(IOUtil.readStringFromReader(theReader).getBytes(Encoder.UTF8.name())), theBaseURI, theRDFFormat, theContexts);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void add(final URL theURL, final String theBaseURI, final RDFFormat theRDFFormat, final Resource... theContexts) throws IOException, RDFParseException, RepositoryException {
		add(theURL.openStream(), theBaseURI, theRDFFormat, theContexts);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void add(final File theFile, final String theBaseURI, final RDFFormat theRDFFormat, final Resource... theContexts) throws IOException, RDFParseException, RepositoryException {
		add(new FileInputStream(theFile), theBaseURI, theRDFFormat, theContexts);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void add(final Resource theResource, final URI theURI, final Value theValue, final Resource... theContexts) throws RepositoryException {
		try {
			getSailConnection().addStatement(theResource, theURI, theValue, theContexts);
		}
		catch (SailException e) {
			throw new RepositoryException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void add(final Statement theStatement, final Resource... theContexts) throws RepositoryException {
		try {
			getSailConnection().addStatement(theStatement.getSubject(), theStatement.getPredicate(), theStatement.getObject(), theContexts);
		}
		catch (SailException e) {
			throw new RepositoryException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void add(final Iterable<? extends Statement> theIterable, final Resource... theContexts) throws RepositoryException {
		Graph aGraph = new GraphImpl();
		aGraph.addAll(set(theIterable));

		try {
			getFourStoreSailConnection().add(aGraph, theContexts);
		}
		catch (SailException e) {
			throw new RepositoryException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <E extends Exception> void add(final Iteration<? extends Statement, E> theIteration, final Resource... theContexts) throws RepositoryException, E {
		Graph aGraph = new GraphImpl();

		while (theIteration.hasNext()) {
			aGraph.add(theIteration.next());
		}

		try {
			getFourStoreSailConnection().add(aGraph, theContexts);
		}
		catch (SailException e) {
			throw new RepositoryException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void remove(final Resource theSubject, final URI thePredicate, final Value theObject, final Resource... theContexts) throws RepositoryException {
		remove(new StatementImpl(theSubject,  thePredicate, theObject), theContexts);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void remove(final Statement theStatement, final Resource... theContexts) throws RepositoryException {
		remove(Collections.singleton(theStatement), theContexts);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void remove(final Iterable<? extends Statement> theIterable, final Resource... theContexts) throws RepositoryException {
		Graph aGraph = new GraphImpl();

		aGraph.addAll(set(theIterable));

		try {
			getFourStoreSailConnection().remove(aGraph, theContexts);
		}
		catch (SailException e) {
			throw new RepositoryException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <E extends Exception> void remove(final Iteration<? extends Statement, E> theIteration, final Resource... theContexts) throws RepositoryException, E {
		Graph aGraph = new GraphImpl();

		while (theIteration.hasNext()) {
			aGraph.add(theIteration.next());
		}

		try {
			getFourStoreSailConnection().remove(aGraph, theContexts);
		}
		catch (SailException e) {
			throw new RepositoryException(e);
		}
	}

	/**
	 * Extends the Sesame SailGraphQuery to circumvent their naive query implementation and operate directly on the
	 * underlying four store sail so the queries go straight to 4store instead of through the Sesame layer.
	 */
	private static class FourStoreSailGraphQuery extends SailGraphQuery {

		/**
		 * Create a new FourStoreSailGraphQuery
		 * @param theQuery the query that will be dispatched
		 * @param theFourStoreRepositoryConnection the connection to be used to answer the query
		 */
		public FourStoreSailGraphQuery(final ParsedGraphQuery theQuery, final FourStoreRepositoryConnection theFourStoreRepositoryConnection) {
			super(theQuery, theFourStoreRepositoryConnection);
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public GraphQueryResult evaluate() throws QueryEvaluationException {
			try {
				GraphBuildingRDFHandler aHandler = new GraphBuildingRDFHandler();

				evaluate(aHandler);

				return new GraphQueryResultImpl(new HashMap<String, String>(), aHandler.getGraph());
			}
			catch (RDFHandlerException e) {
				throw new QueryEvaluationException(e);
			}
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public void evaluate(RDFHandler theHandler) throws QueryEvaluationException, RDFHandlerException {
			try {
				Graph aGraph = ((FourStoreRepositoryConnection)this.getConnection()).getFourStoreSailConnection().graphQuery(new SPARQLQueryRenderer().render(getParsedQuery()));

				theHandler.startRDF();
				for (Statement aStmt : aGraph) {
					theHandler.handleStatement(aStmt);
				}
				theHandler.endRDF();
			}
			catch (Exception e) {
				throw new QueryEvaluationException(e);
			}
		}
	}

	/**
	 * Etends the Sesame SailTupleQuery to circumvent their naive query implementation to dispatch the query directly
	 * to 4store via the underlying fourstore sail.
	 */
	private static class FourStoreSailTupleQuery extends SailTupleQuery {

		/**
		 * Create a new FourStoreSailTupleQuery
		 * @param theParsedTupleQuery the query that will be dispatched
		 * @param theFourStoreRepositoryConnection the connection to be used to answer the query
		 */
		protected FourStoreSailTupleQuery(final ParsedTupleQuery theParsedTupleQuery, final FourStoreRepositoryConnection theFourStoreRepositoryConnection) {
			super(theParsedTupleQuery, theFourStoreRepositoryConnection);
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public TupleQueryResult evaluate() throws QueryEvaluationException {
			TupleQueryResultBuilder aBuilder = new TupleQueryResultBuilder();

			evaluate(aBuilder);

			return aBuilder.getQueryResult();
		}


		/**
		 * @inheritDoc
		 */
		@Override
		public void evaluate(TupleQueryResultHandler theHandler) throws QueryEvaluationException {
			try {
				TupleQueryResult aResult = ((FourStoreRepositoryConnection)this.getConnection()).getFourStoreSailConnection().query(new SPARQLQueryRenderer().render(getParsedQuery()));

				theHandler.startQueryResult(aResult.getBindingNames());
				while (aResult.hasNext()) {
					theHandler.handleSolution(aResult.next());
				}
				theHandler.endQueryResult();
			}
			catch (Exception e) {
				throw new QueryEvaluationException(e);
			}
		}
	}
}
