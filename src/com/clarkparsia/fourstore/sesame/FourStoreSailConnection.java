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

import com.clarkparsia.fourstore.api.Store;
import com.clarkparsia.fourstore.api.StoreException;
import com.clarkparsia.fourstore.api.QueryException;
import com.clarkparsia.fourstore.sesame.trans.TransactionSupport;
import com.clarkparsia.fourstore.sesame.trans.NoOpTransactionSupport;
import com.clarkparsia.fourstore.sesame.trans.NaiveTransactionSupport;
import com.clarkparsia.openrdf.query.SesameQueryUtils;
import com.clarkparsia.openrdf.query.sparql.SPARQLQueryRenderer;
import com.clarkparsia.openrdf.query.sparql.SparqlTupleExprRenderer;
import com.clarkparsia.openrdf.OpenRdfIO;
import static com.clarkparsia.openrdf.OpenRdfUtil.iterable;
import com.clarkparsia.utils.Function;
import com.clarkparsia.utils.AbstractDataCommand;
import static com.clarkparsia.utils.collections.CollectionUtil.transform;
import static com.clarkparsia.utils.collections.CollectionUtil.each;

import org.openrdf.sail.helpers.NotifyingSailConnectionBase;

import org.openrdf.sail.SailException;
import org.openrdf.sail.SailConnectionListener;

import org.openrdf.query.BindingSet;
import org.openrdf.query.Dataset;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.evaluation.impl.BindingAssigner;
import org.openrdf.query.algebra.evaluation.impl.ConstantOptimizer;
import org.openrdf.query.algebra.evaluation.impl.CompareOptimizer;
import org.openrdf.query.algebra.evaluation.impl.ConjunctiveConstraintSplitter;
import org.openrdf.query.algebra.evaluation.impl.DisjunctiveConstraintOptimizer;
import org.openrdf.query.algebra.evaluation.impl.SameTermFilterOptimizer;
import org.openrdf.query.algebra.evaluation.impl.QueryModelNormalizer;
import org.openrdf.query.algebra.evaluation.impl.QueryJoinOptimizer;
import org.openrdf.query.algebra.evaluation.impl.IterativeEvaluationOptimizer;
import org.openrdf.query.algebra.evaluation.impl.FilterOptimizer;
import org.openrdf.query.algebra.evaluation.impl.OrderLimitOptimizer;
import org.openrdf.query.algebra.evaluation.impl.EvaluationStrategyImpl;
import org.openrdf.query.algebra.evaluation.util.QueryOptimizerList;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.Statement;
import org.openrdf.model.Namespace;
import org.openrdf.model.Graph;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.helpers.RDFHandlerBase;

import info.aduna.iteration.CloseableIteration;
import info.aduna.iteration.CloseableIteratorIteration;

import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;
import java.io.StringWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * <p>Implementation of a SailConnection which operates on a 4Store instance.</p>
 *
 * @author Michael Grove
 * @version 0.3
 * @since 0.3
 *
 * @see FourStoreSail
 */
public class FourStoreSailConnection extends NotifyingSailConnectionBase {

	/**
	 * The logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger("4store");

	/**
	 * The 4Store instance to perform operations on
	 */
	private Store mStore;

	/**
	 * Namespace mappings.  Keys are prefixes, values are the URI's associated with the prefix.
	 */
	private Map<String, String> mNamespaces = new HashMap<String, String>();

	/**
	 * Utility class for providing transactional support for this 4Store Sail
	 */
	private TransactionSupport mTransactionSupport;

	private QueryOptimizerList mOptimizers;

	/**
	 * Create a new FourStoreSailConnection
	 * @param theSail the store to operations on the connection will mutate
	 */
	FourStoreSailConnection(final FourStoreSail theSail) {
		super(theSail);

		mStore = theSail.getFourStore();

		mTransactionSupport = new NoOpTransactionSupport();

		addConnectionListener(mTransactionSupport);

        mOptimizers = new QueryOptimizerList();

        mOptimizers.add(new BindingAssigner());
        mOptimizers.add(new CompareOptimizer());
		// we're not currently using these since they don't seem to make sense from a client's perspective.  for example,
		// they split OR value expressions into unioned queries.  this might make sense for the eval of the sesame query
		// algebra internally, but the point of this method is to apply general purpose optimizations that make sense
		// for everyone.  i dont think unioned queries is a way to achieve that.  so we'll disable them here and
		// use the other "standard" optimizers.
//        aList.add(new ConjunctiveConstraintSplitter());
//        aList.add(new DisjunctiveConstraintOptimizer());
        mOptimizers.add(new SameTermFilterOptimizer());
        mOptimizers.add(new QueryModelNormalizer());
        mOptimizers.add(new IterativeEvaluationOptimizer());
        mOptimizers.add(new FilterOptimizer());
        mOptimizers.add(new OrderLimitOptimizer());
	}

	/**
	 * @inheritDoc
	 */
	protected void closeInternal() throws SailException {
		// no-op, this connection does not keep any resources open
	}

	/**
	 * @inheritDoc
	 */
	protected CloseableIteration<? extends BindingSet, QueryEvaluationException> evaluateInternal(final TupleExpr theTupleExpr, final Dataset theDataset, final BindingSet theBindingSet, final boolean theInfer) throws SailException {
		// 4store doesn't have a distinction b/w inferred or not, so that param is completely ignored.

		mOptimizers.optimize(theTupleExpr, theDataset, theBindingSet);

		try {
			String aSPARQL = new SparqlTupleExprRenderer().render(theTupleExpr);

			return query(aSPARQL);
		}
		catch (Exception e) {
			throw new SailException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	protected CloseableIteration<? extends Resource, SailException> getContextIDsInternal() throws SailException {
		// this is probably an expensive query, but i dont know of a better way to do this.
		String aQuery = "select distinct ?g where { graph ?g {?s ?p ?o} }";

		Collection<Resource> aURIs = new HashSet<Resource>();

		try {
			TupleQueryResult aResult = mStore.query(aQuery);
			while (aResult.hasNext()) {
				aURIs.add( (Resource) aResult.next().getValue("g"));
			}
			aResult.close();

			return new CloseableIteratorIteration<Resource, SailException>(aURIs.iterator());
		}
		catch (QueryException e) {
			throw new SailException(e);
		}
		catch (QueryEvaluationException e) {
			throw new SailException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	protected CloseableIteration<? extends Statement, SailException> getStatementsInternal(final Resource theSubj, final URI thePred, final Value theObj, final boolean theInfer, final Resource... theResources) throws SailException {
		StringBuffer aQuery = new StringBuffer("construct { ?s ?p ?o } where { ");

		String aFilters = "";
		String aWhere = " ?s ?p ?o.";

		if (theSubj != null) {
			aFilters += "filter(?s = " + SesameQueryUtils.getQueryString(theSubj) + "). ";
		}

		if (thePred != null) {
			aFilters += "filter(?p = " + SesameQueryUtils.getQueryString(thePred) + "). ";
		}

		if (theObj != null) {
			aFilters += "filter(?o = " + SesameQueryUtils.getQueryString(theObj) + "). ";
		}

		if (theResources.length == 0) {
			aQuery.append(aWhere).append("\n").append(aFilters);
		}
		else {
			for (Resource aContext : theResources) {
				aQuery.append("graph ").append(SesameQueryUtils.getQueryString(aContext)).append(" { ").append(aWhere).append(" ").append(aFilters).append("}.\n");
			}
		}

		aQuery.append("}");

		try {
			Graph aGraph = mStore.constructQuery(aQuery.toString());

			return new CloseableIteratorIteration<Statement, SailException>(aGraph.iterator());
		}
		catch (QueryException e) {
			throw new SailException(e);
		}
	}
	
	/**
	 * @inheritDoc
	 */
	protected long sizeInternal(final Resource... theContexts) throws SailException {
		try {
			if (theContexts.length == 0) {
				return mStore.size();
			}
			else {
				int aSize = 0;

				for (Resource aContext : theContexts) {

					// since non-distinct, s should be repeated for every triple its a part of, effectively giving us
					// the size of the graph w/o doing a construct and parsing the RDF.
					String aQuery = "select ?s where { graph " + SesameQueryUtils.getQueryString(aContext) + " { ?s ?p ?o } }";

					int aCount = 0;
					TupleQueryResult aResults = mStore.query(aQuery);
					while (aResults.hasNext()) {
						aResults.next();
						aCount++;
					}

					aSize += aCount;
				}

				return aSize;
			}
		}
		catch (StoreException e) {
			throw new SailException(e);
		}
		catch (QueryEvaluationException e) {
			throw new SailException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	protected void startTransactionInternal() throws SailException {
		mTransactionSupport.begin();
	}

	/**
	 * @inheritDoc
	 */
	protected void commitInternal() throws SailException {
		mTransactionSupport.commit();
	}

	/**
	 * @inheritDoc
	 */
	protected void rollbackInternal() throws SailException {
		mTransactionSupport.rollback();
	}

	/**
	 * @inheritDoc
	 */
	protected void addStatementInternal(final Resource theSubject, final URI thePred, final Value theObject, final Resource... theContexts) throws SailException {
		try {
			Graph aGraph = new GraphImpl();

			aGraph.add(theSubject, thePred, theObject);

			if (theContexts.length == 0) {
				mStore.append(aGraph, null);

				notifyStatementAdded(new StatementImpl(theSubject, thePred, theObject));
			}
			else {
				for (Resource aContext : theContexts) {
					if (aContext instanceof URI) {
						mStore.append(aGraph, (URI) aContext);

						notifyStatementAdded(new ContextStatementImpl(theSubject, thePred, theObject, aContext));
					}
					else {
						LOGGER.warn("Ignoring add to context, not a URI: " + aContext);
					}
				}
			}
		}
		catch (StoreException e) {
			throw new SailException(e);
		}
	}

	/**
	 * Add the RDF data directly to 4store
	 * @param theGraph the graph to add
	 * @param theContexts the contexts to add the data to
	 * @throws SailException if there is an error while adding
	 */
	public void add(Graph theGraph, Resource... theContexts) throws SailException {
		autoStartTransaction();

		try {
			if (theContexts == null || theContexts.length == 0) {
				mStore.append(theGraph, null);
			}
			else {
				for (Resource aContext : theContexts) {
					if (aContext instanceof URI) {
						mStore.append(theGraph, (URI) aContext);
					}
					else {
						LOGGER.warn("Ignoring add to context, not a URI: " + aContext);
					}
				}
			}

			for (Statement aStmt : theGraph) {
				if (theContexts == null || theContexts.length == 0) {
					notifyStatementAdded(aStmt);
				}
				else {
					for (Resource aContext : theContexts) {
						if (aContext instanceof URI) {
							notifyStatementAdded(new ContextStatementImpl(aStmt.getSubject(), aStmt.getPredicate(), aStmt.getObject(), aContext));
						}
					}
				}
			}

		}
		catch (StoreException e) {
			throw new SailException(e);
		}
	}

	/**
	 * Add the RDF data in the stream directly to 4store
	 * @param theStream the data to add
	 * @param theFormat the format of the data
	 * @param theContexts the context to add the data to
	 * @throws SailException if there is an error while adding
	 */
	public void add(final InputStream theStream, RDFFormat theFormat, final Resource... theContexts) throws SailException {
		autoStartTransaction();

		try {
			// this will pipe the data being added to a temporary file on disk, we'll read from that file
			// to dispatch add events to interested listeners
			FileBufferedInputStream aStream = new FileBufferedInputStream(theStream);

			boolean aSuccess = false;
			if (theContexts == null || theContexts.length == 0) {
				aSuccess = mStore.append(aStream, theFormat, null);
			}
			else {
				for (Resource aContext : theContexts) {
					if (aContext instanceof URI) {
						aSuccess = mStore.append(aStream, theFormat, (URI) aContext);
					}
					else {
						LOGGER.warn("Ignoring add to context, not a URI: " + aContext);
					}
				}
			}

			aStream.close();

			if (aSuccess) {
				OpenRdfIO.iterateGraph(new RDFHandlerBase() {
					public void handleStatement(Statement theStmt) {
						if (theContexts == null || theContexts.length == 0) {
							notifyStatementAdded(theStmt);
						}
						else {
							for (Resource aContext : theContexts) {
								if (aContext instanceof URI) {
									notifyStatementAdded(new ContextStatementImpl(theStmt.getSubject(), theStmt.getPredicate(), theStmt.getObject(), aContext));
								}
							}
						}
					}
				}, aStream.buffer(), theFormat);
			}
		}
		catch (Exception e) {
			throw new SailException(e);
		}
	}

	/**
	 * Remove the given RDF data directly from 4store
	 * @param theGraph the graph to remove
	 * @param theContexts the contexts to remove from
	 * @throws SailException if there is an error while removing
	 */
	public void remove(final Graph theGraph, final Resource... theContexts) throws SailException {
		autoStartTransaction();
		
		try {
			if (theContexts == null || theContexts.length == 0) {
				mStore.delete(theGraph, null);
			}
			else {
				for (Resource aContext : theContexts) {
					if (aContext instanceof URI) {
						mStore.delete(theGraph, (URI) aContext);
					}
					else {
						LOGGER.warn("Ignoring remove from context, not a URI: " + aContext);
					}
				}
			}

			for (Statement aStmt : theGraph) {
				if (theContexts == null || theContexts.length == 0) {
					notifyStatementRemoved(aStmt);
				}
				else {
					for (Resource aContext : theContexts) {
						if (aContext instanceof URI) {
							notifyStatementRemoved(new ContextStatementImpl(aStmt.getSubject(), aStmt.getPredicate(), aStmt.getObject(), aContext));
						}
					}
				}
			}
		}
		catch (StoreException e) {
			throw new SailException(e);
		}
	}

	/**
	 * Remove the given RDF data directly from 4store
	 * @param theStream the rdf data to remove
	 * @param theFormat the syntax format the RDF is in
	 * @param theContexts the contexts to remove the data from
	 * @throws SailException if there is an error while removing
	 */
	public void remove(final InputStream theStream, final RDFFormat theFormat, final Resource... theContexts) throws SailException {
		autoStartTransaction();

		try {
			Graph aGraph = OpenRdfIO.readGraph(theStream, theFormat);

			if (theContexts == null || theContexts.length == 0) {
				mStore.delete(aGraph, null);
			}
			else {
				for (Resource aContext : theContexts) {
					if (aContext instanceof URI) {
						mStore.delete(aGraph, (URI) aContext);
					}
					else {
						LOGGER.warn("Ignoring remove from context, not a URI: " + aContext);
					}
				}
			}

			for (Statement aStmt : aGraph) {
				if (theContexts == null || theContexts.length == 0) {
					notifyStatementRemoved(aStmt);
				}
				else {
					for (Resource aContext : theContexts) {
						if (aContext instanceof URI) {
							notifyStatementRemoved(new ContextStatementImpl(aStmt.getSubject(), aStmt.getPredicate(), aStmt.getObject(), aContext));
						}
					}
				}
			}
		}
		catch (Exception e) {
			throw new SailException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	protected void removeStatementsInternal(final Resource theSubject, final URI thePred, final Value theObject, final Resource... theContexts) throws SailException {
		try {
			StringWriter aWriter = new StringWriter();
			Graph aGraph = new GraphImpl();

			for (Statement aStmt : iterable(mStore.getStatements(theSubject, thePred, theObject))) {
				aGraph.add(aStmt);
			}

			OpenRdfIO.writeGraph(aGraph, aWriter, RDFFormat.NTRIPLES);

			if (theContexts.length == 0) {
				mStore.delete(aGraph, null);
			}
			else {
				for (Resource aContext : theContexts) {
					if (aContext instanceof URI) {
						mStore.delete(aGraph, (URI) aContext);
					}
					else {
						LOGGER.warn("Ignoring delete from context, not a URI: " + aContext);
					}
				}
			}

			for (Statement aStmt : aGraph) {
				notifyStatementRemoved(aStmt);
			}
		}
		catch (IOException e) {
			throw new SailException(e);
		}
		catch (StoreException e) {
			throw new SailException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	protected void clearInternal(final Resource... theContexts) throws SailException {
		try {
			// TODO: should this stuff be tracked inside the transaction?

			for (Resource aContext : theContexts) {
				if (aContext instanceof URI) {
					mStore.delete( (URI) aContext);
				}
				else {
					LOGGER.warn("Ignoring delete context, not a URI: " + aContext);
				}
			}
		}
		catch (StoreException e) {
			throw new SailException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	protected CloseableIteration<? extends Namespace, SailException> getNamespacesInternal() throws SailException {
		return new CloseableIteratorIteration<Namespace, SailException>(transform(mNamespaces.entrySet(), new Function<Map.Entry<String, String>, Namespace>() {
			public Namespace apply(final Map.Entry<String, String> theIn) {
				return new NamespaceImpl(theIn.getKey(), theIn.getValue());
		}}).iterator());
	}

	/**
	 * @inheritDoc
	 */
	protected String getNamespaceInternal(final String thePrefix) throws SailException {
		return mNamespaces.get(thePrefix);
	}

	/**
	 * @inheritDoc
	 */
	protected void setNamespaceInternal(final String thePrefix, final String theNamespaceURI) throws SailException {
		mNamespaces.put(thePrefix, theNamespaceURI);
	}

	/**
	 * @inheritDoc
	 */
	protected void removeNamespaceInternal(final String thePrefix) throws SailException {
		mNamespaces.remove(thePrefix);
	}

	/**
	 * @inheritDoc
	 */
	protected void clearNamespacesInternal() throws SailException {
		mNamespaces.clear();
	}

	/**
	 * Perform a graph query on the 4store instance
	 * @param theQuery the query to perform
	 * @return the results of the graph query
	 * @throws SailException if there is an error while querying
	 */
	public Graph graphQuery(final String theQuery) throws SailException {
		try {
			return mStore.constructQuery(theQuery);
		}
		catch (QueryException e) {
			throw new SailException(e);
		}
	}

	/**
	 * Perform a select query on the 4store instance
	 * @param theQuery the query to perform
	 * @return the results of the query
	 * @throws SailException if there is an error while querying
	 */
	public TupleQueryResult query(String theQuery) throws SailException {
		try {
			return mStore.query(theQuery);
		}
		catch (QueryException e) {
			throw new SailException(e);
		}
	}

	private static class FileBufferedInputStream extends InputStream {
		private InputStream mStream;
		private File mFile;
		private OutputStream mOut;
		private boolean mOpen;

		private FileBufferedInputStream(final InputStream theStream) throws IOException {
			mStream = theStream;

			mFile = File.createTempFile("data", "");
			mFile.deleteOnExit();

			mOut = new FileOutputStream(mFile);

			mOpen = true;
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public int read() throws IOException {
			int aResult = mStream.read();

			if (aResult != -1) {
				mOut.write(aResult);
			}

			return aResult;
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public void close() throws IOException {
			mOpen = false;
			super.close();

			mOut.flush();
			mOut.close();
		}

		private boolean isOpen() {
			return mOpen;
		}

		public InputStream buffer() throws IOException {
			if (this.isOpen()) {
				throw new IOException("Cannot return buffer");
			}

			return new FileInputStream(mFile);
		}
	}
}
