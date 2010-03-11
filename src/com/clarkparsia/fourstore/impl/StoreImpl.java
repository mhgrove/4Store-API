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

package com.clarkparsia.fourstore.impl;

import com.clarkparsia.fourstore.api.QueryException;
import com.clarkparsia.fourstore.api.Store;
import com.clarkparsia.fourstore.api.StoreException;

import com.clarkparsia.fourstore.impl.results.SparqlXmlResultSetParser;
import com.clarkparsia.fourstore.impl.results.ResultSetBuilder;

import com.clarkparsia.openrdf.OpenRdfIO;
import com.clarkparsia.openrdf.query.SesameQueryUtils;

import org.openrdf.model.BNode;
import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.impl.StatementImpl;

import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.QueryResult;

import org.openrdf.query.resultio.TupleQueryResultFormat;

import org.openrdf.query.impl.TupleQueryResultImpl;

import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.RepositoryException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.clarkparsia.utils.web.HttpHeaders;
import com.clarkparsia.utils.web.HttpResource;
import com.clarkparsia.utils.web.Method;
import com.clarkparsia.utils.web.MimeTypes;
import com.clarkparsia.utils.web.ParameterList;
import com.clarkparsia.utils.web.Request;
import com.clarkparsia.utils.web.Response;
import com.clarkparsia.utils.web.HttpResourceImpl;
import com.clarkparsia.utils.io.Encoder;
import com.clarkparsia.utils.io.IOUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.StringWriter;
import java.io.File;

import java.net.ConnectException;
import java.net.URL;
import java.util.Collections;

import info.aduna.iteration.CloseableIteratorIteration;

/**
 * <p>Implementation of Store interface which interacts with a 4Store database over their RESTful HTTP
 * protocol as <a href="http://4store.org/trac/wiki/SparqlServer">documented here.</p>
 *
 * @author Michael Grove
 * @since 0.1
 * @version 0.3
 */
public class StoreImpl implements Store {
	private static final String DEFAULT_SUBGRAPH = "http://clarkparsia.com/4store/repository";

	public static final String PARAM_QUERY = "query";
	public static final String PARAM_SOFT_LIMIT = "soft-limit";

	private int mSoftLimit;

	/**
	 * Whether or not this data source is connected
	 */
	private boolean mIsConnected;

	/**
	 * The HTTP resource representing the remote 4store database
	 */
	private HttpResource mFourStoreResource;

	/**
	 * Whether or not to use GET requests for queries to the servers.  Use POST by default just in the case that
	 * queries in GET requests can be too long for servers to handle.
	 * @see StoreFactory
	 */
	private boolean mUseGetForQueries = false;

	StoreImpl(final URL theBaseURL, final boolean theUseGetForQueries) {
		mSoftLimit = -1;

		mUseGetForQueries = theUseGetForQueries;

		mFourStoreResource = new HttpResourceImpl(theBaseURL);

		mIsConnected = false;

		// TODO: don't allow operations until you are connected
	}

	/**
	 * Create a new StoreImpl
	 * @param theURL the URL of the 4Store instance
	 */
	StoreImpl(URL theURL) {
		this(theURL, false);
	}

	/**
	 * @inheritDoc
	 */
	public URL getURL() {
		return mFourStoreResource.url();
	}

	/**
	 * @inheritDoc
	 */
	public int getSoftLimit() {
		return mSoftLimit;
	}

	/**
	 * @inheritDoc
	 */
	public boolean hasStatement(final Statement theStmt) throws StoreException {
		return hasStatement(theStmt.getSubject(), theStmt.getPredicate(), theStmt.getObject());
	}

	/**
	 * @inheritDoc
	 */
	public boolean hasStatement(final Resource theSubj, final URI thePred, final Value theObj) throws StoreException {
		if (theSubj == null && thePred == null && theObj == null) {
			throw new StoreException("You must bind at least one value to this function.");
		}

		String aQuery = "select ?s ?p ?o where { ";

		if (theSubj != null) {
			aQuery += "filter(?s = " + SesameQueryUtils.getQueryString(theSubj) + "). ";
		}

		if (thePred != null) {
			aQuery += "filter(?p = " + SesameQueryUtils.getQueryString(thePred) + "). ";
		}

		if (theObj != null) {
			aQuery += "filter(?o = " + SesameQueryUtils.getQueryString(theObj) + "). ";
		}

		aQuery += " ?s ?p ?o.} limit 1";

        try {
			TupleQueryResult aResult = query(aQuery);

            boolean aAnswer = aResult.hasNext();

			aResult.close();

			return aAnswer;
        }
        catch (QueryEvaluationException e) {
            throw new StoreException(e);
        }
    }

	/**
	 * @inheritDoc
	 */
	public RepositoryResult<Statement> getStatements(final Resource theSubj, final URI thePred, final Value theObj) throws StoreException {
		if (theSubj == null && thePred == null && theObj == null) {
			throw new StoreException("You must bind at least one value to this function.");
		}
		else if (theSubj != null && thePred != null && theObj != null) {
			return new RepositoryResult<Statement>(
					new CloseableIteratorIteration<Statement, RepositoryException>(
							Collections.singleton(new StatementImpl(theSubj, thePred, theObj)).iterator()));
		}

		String aQuery = "construct { ?s ?p ?o } where { ";

		if (theSubj != null) {
			aQuery += "filter(?s = " + SesameQueryUtils.getQueryString(theSubj) + "). ";
		}

		if (thePred != null) {
			aQuery += "filter(?p = " + SesameQueryUtils.getQueryString(thePred) + "). ";
		}

		if (theObj != null) {
			aQuery += "filter(?o = " + SesameQueryUtils.getQueryString(theObj) + "). ";
		}

		aQuery += " ?s ?p ?o.}";

		Graph aResult = constructQuery(aQuery);

		return new RepositoryResult<Statement>(new CloseableIteratorIteration<Statement, RepositoryException>(aResult.iterator()));
    }

	/**
	 * @inheritDoc
	 */
	public void connect() throws ConnectException {
		try {
			Response aResp = mFourStoreResource.resource("status/").get();

			if (aResp.hasErrorCode()) {
				throw new ConnectException("There was an error connecting to the store: " +
										   aResp.getMessage() + "\n" + aResp.getContent());
			}

			mIsConnected = true;
		}
		catch (IOException e) {
			throw new ConnectException(e.getMessage());
		}
	}

	/**
	 * Enforce an "open" connection to use the database
	 * @throws IllegalArgumentException if there is no open connection.
	 */
	private void assertConnected() {
		if (!mIsConnected) {
			throw new IllegalArgumentException("You must be connected in order to query the database");
		}
	}

	/**
	 * @inheritDoc
	 */
	public void disconnect() throws ConnectException {
		mIsConnected = false;
		// no other clean-up needed
	}

	/**
	 * @inheritDoc
	 */
	public void setSoftLimit(final int theSoftLimit) {
		mSoftLimit = theSoftLimit;
	}

	/**
	 * @inheritDoc
	 */
	public TupleQueryResult query(String theQuery) throws QueryException {
		SparqlXmlResultSetParser aHandler = internalQuery(theQuery);

		return new TupleQueryResultImpl(aHandler.bindingNames(), aHandler.bindingSet());
	}

//	public void update(String theQuery) throws QueryException {
//		HttpResource aRes = mFourStoreResource.resource("update");
//
//		String aQuery = theQuery;
//
//		// auto prefix queries w/ rdf and rdfs namespaces
//		aQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
//				 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
//				 aQuery;
//
//		ParameterList aParams = new ParameterList()
//				.add("update", aQuery);
//
//		try {
//			Request aQueryRequest = aRes.initPost()
//					.addHeader(HttpHeaders.ContentType.getName(), MimeTypes.FormUrlEncoded.getMimeType())
//					.addHeader(HttpHeaders.ContentLength.getName(), Integer.toString(aParams.getURLEncoded().getBytes(Encoder.UTF8.name()).length))
//					.setBody(aParams.getURLEncoded());
//
//			Response aResponse = aQueryRequest.execute();
//
//			if (aResponse.hasErrorCode()) {
//				throw new QueryException(responseToStoreException(aResponse));
//			}
//			else {
//				checkResultsForError(aResponse);
//			}
//		}
//		catch (IOException e) {
//			throw new QueryException(e);
//		}
//	}

	/**
	 * Dispatch a query to the sparql endpoing of the store
	 * @param theQuery the query to send
	 * @return the sparql result handler which parsed the results
	 * @throws QueryException if there was an error while querying
	 */
	private SparqlXmlResultSetParser internalQuery(String theQuery) throws QueryException {
		return internalQuery(theQuery, TupleQueryResultFormat.SPARQL);
	}

	/**
	 * Dispatch a query to the sparql endpoint of the store
	 * @param theQuery the query to send
	 * @param theAccept the result format to send back
	 * @return the sparql result set handler which parsed the results
	 * @throws QueryException if there is an error while querying
	 */
	private SparqlXmlResultSetParser internalQuery(String theQuery, TupleQueryResultFormat theAccept) throws QueryException {
		// TODO: this really only works for sparql/xml results, generalize it to work for any sparql results format.

		HttpResource aRes = mFourStoreResource.resource("sparql");

		String aQuery = theQuery;

		// auto prefix queries w/ rdf and rdfs namespaces
		aQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
				 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
				 aQuery;

		ParameterList aParams = new ParameterList()
				.add(PARAM_QUERY, aQuery)
				.add(PARAM_SOFT_LIMIT, String.valueOf(getSoftLimit()));

		try {
			Request aQueryRequest = null;
			if (mUseGetForQueries) {
				aQueryRequest = aRes.initGet()
						.addHeader(HttpHeaders.Accept.getName(), theAccept.getDefaultMIMEType())
						.setParameters(aParams);
			}
			else {
				aQueryRequest = aRes.initPost()
						.addHeader(HttpHeaders.ContentType.getName(), MimeTypes.FormUrlEncoded.getMimeType())
						.addHeader(HttpHeaders.Accept.getName(), theAccept.getDefaultMIMEType())
						.setBody(aParams.getURLEncoded());
			}

			Response aResponse = aQueryRequest.execute();

			if (aResponse.hasErrorCode()) {
				throw new QueryException(responseToStoreException(aResponse));
			}
			else {
				checkResultsForError(aResponse);

				// TODO: pull out and do something w/ information about hitting the soft limit.  this is how
				// it looks in the results xml.
				// <!-- warning: hit complexity limit 2 times, increasing soft limit may give more results -->

				try {
					SparqlXmlResultSetParser aHandler = new SparqlXmlResultSetParser(new ResultSetBuilder(new ValueFactoryImpl()));

					XMLReader aParser = org.xml.sax.helpers.XMLReaderFactory.createXMLReader();

					aParser.setContentHandler(aHandler);
					aParser.setFeature("http://xml.org/sax/features/validation", false);

					aParser.parse(new InputSource(new ByteArrayInputStream(aResponse.getContent().getBytes(Encoder.UTF8.name()))));

                    return aHandler;
				}
				catch (SAXException e) {
					throw new QueryException("Could not parse SPARQL-XML results", e);
				}
			}
		}
		catch (IOException e) {
			throw new QueryException(e);
		}
	}

	/**
	 * Return whether or not the response contains any error messages
	 * @param theResponse the response to check
	 * @throws QueryException true if it contains error messages, false otherwise
	 */
	private void checkResultsForError(Response theResponse) throws QueryException {
		String aContent = theResponse.getContent();

		String aToken = "parser error:";

		// TODO: could stand for more robust error checking.
		if (aContent.indexOf(aToken) != -1) {
			int aStart = aContent.indexOf(aToken) + aToken.length();
			throw new QueryException("Parse Error:" + aContent.substring(aStart,
																		 aContent.indexOf("-->", aStart)));
		}
	}

	/**
	 * @inheritDoc
	 */
	public Graph constructQuery(final String theQuery) throws QueryException {
		HttpResource aRes = mFourStoreResource.resource("sparql");

		String aQuery = theQuery;

		// auto prefix queries w/ rdf and rdfs namespaces
		aQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
				 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
				 aQuery;

		ParameterList aParams = new ParameterList()
				.add(PARAM_QUERY, aQuery)
				.add(PARAM_SOFT_LIMIT, String.valueOf(getSoftLimit()));

		try {
			Request aQueryRequest = null;

			if (mUseGetForQueries) {
				aQueryRequest = aRes.initGet()
						// TODO: why doesn't setting the accept header work here?
						.addHeader(HttpHeaders.Accept.getName(), RDFFormat.TURTLE.getDefaultMIMEType())
						.setParameters(aParams);
			}
			else {
				aQueryRequest = aRes.initPost()
						.addHeader(HttpHeaders.ContentType.getName(), MimeTypes.FormUrlEncoded.getMimeType())
						// TODO: why doesn't setting the accept header work here?
						.addHeader(HttpHeaders.Accept.getName(), RDFFormat.TURTLE.getDefaultMIMEType())
						.setBody(aParams.getURLEncoded());
			}

			Response aResponse = aQueryRequest.execute();

			if (aResponse.hasErrorCode()) {
				throw new QueryException(responseToStoreException(aResponse));
			}
			else {
				checkResultsForError(aResponse);

				// TODO: pull out and do something w/ information about hitting the soft limit.  this is how
				// it looks in the results xml.
				// <!-- warning: hit complexity limit 2 times, increasing soft limit may give more results -->

				try {
					return OpenRdfIO.readGraph(new StringReader(aResponse.getContent()), RDFFormat.RDFXML);
				}
				catch (RDFParseException e) {
					throw new QueryException("Error while parsing rdf/xml-formatted query results", e);
				}
			}
		}
		catch (IOException e) {
			throw new QueryException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	public Graph describe(final String theQuery) throws QueryException {
		// this should be sufficient.  describe's return an RDF graph as the result, and we don't do anything in
		// the constructQuery method specific to constructs other than parsing the result as an RDF graph, which
		// is something we want for describe's as well.
		return constructQuery(theQuery);
	}

	/**
	 * @inheritDoc
	 */
	public boolean add(final String theGraph, final RDFFormat theFormat, final URI theGraphURI) throws StoreException {
		return dataOperation(Method.PUT, theGraph, theFormat, theGraphURI);
	}

	/**
	 * @inheritDoc
	 */
	public boolean add(final Graph theGraph, final URI theGraphURI) throws StoreException {
		try {
			StringWriter aWriter = new StringWriter();

			OpenRdfIO.writeGraph(theGraph, aWriter, RDFFormat.NTRIPLES);

			return add(aWriter.toString(), RDFFormat.NTRIPLES, theGraphURI);
		}
		catch (IOException e) {
			throw new StoreException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	public boolean add(final InputStream theGraph, final RDFFormat theFormat, final URI theGraphURI) throws StoreException {
		return dataOperation(Method.PUT, theGraph, theFormat, theGraphURI);
	}

	/**
	 * @inheritDoc
	 */
    public boolean delete(final Graph theGraph, final URI theGraphURI) throws StoreException {
		HttpResource aRes = mFourStoreResource.resource("update");

		StringBuffer aQuery = new StringBuffer();

		for (Statement aStmt : theGraph) {
			aQuery.append(SesameQueryUtils.getQueryString(aStmt.getSubject())).append(" ")
					.append(SesameQueryUtils.getQueryString(aStmt.getPredicate())).append(" ")
					.append(SesameQueryUtils.getQueryString(aStmt.getObject())).append(".\n");
		}

		if (theGraphURI != null) {
			aQuery.insert(0, " graph <" + theGraphURI + "> {\n").append(" }");
		}

		aQuery.insert(0, "delete { ").append(" }");
		
		ParameterList aParams = new ParameterList()
				.add("update", aQuery.toString());

		try {
			Request aQueryRequest = aRes.initPost()
					.addHeader(HttpHeaders.ContentType.getName(), MimeTypes.FormUrlEncoded.getMimeType())
					.addHeader(HttpHeaders.ContentLength.getName(), Integer.toString(aParams.getURLEncoded().getBytes(Encoder.UTF8.name()).length))
					.setBody(aParams.getURLEncoded());

			Response aResponse = aQueryRequest.execute();

			if (aResponse.hasErrorCode()) {
				throw new QueryException(responseToStoreException(aResponse));
			}
			else {
				checkResultsForError(aResponse);

				return true;
			}
		}
		catch (IOException e) {
			throw new QueryException(e);
		}
    }

	/**
	 * @inheritDoc
	 */
	public boolean delete(final URI theGraphURI) throws StoreException {
		HttpResource aRes = mFourStoreResource.resource("data");

		if (theGraphURI != null) {
			aRes = aRes.resource(theGraphURI.toString());
		}
		else {
			throw new StoreException("No graph specified to delete");
		}

		try {
			Response aResponse = aRes.delete();

			if (aResponse.hasErrorCode()) {
				throw responseToStoreException(aResponse);
			}
			else {
				return isSuccess(aResponse);
			}
		}
		catch (IOException e) {
			throw new StoreException(e);
		}
	}

	/**
	 * Returns whether or not the response indicates the operation was successful
	 * @param theResponse the response to check
	 * @return true if it indicates a successful response, false otherwise.
	 */
	private boolean isSuccess(Response theResponse) {
		// TODO: is there a better indication of success?
		return theResponse.getResponseCode() == 200;
	}

	/**
	 * @inheritDoc
	 */
	public boolean append(final String theGraph, final RDFFormat theFormat, final URI theGraphURI) throws StoreException {
		HttpResource aRes = mFourStoreResource.resource("data");

		ParameterList aParams = new ParameterList();
		aParams.add("mime-type", theFormat.getDefaultMIMEType())
			   .add("data", theGraph+"\n");

		if (theGraphURI != null) {
			aParams.add("graph", theGraphURI.toString());
		}
		else {
			aParams.add("graph", DEFAULT_SUBGRAPH);
		}

		try {
			Response aResponse = aRes.initPost()
					.setBody(aParams.getURLEncoded())
					.execute();

			if (aResponse.hasErrorCode()) {
				throw responseToStoreException(aResponse);
			}
			else {
				return isSuccess(aResponse);
			}
		}
		catch (IOException e) {
			throw new StoreException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	public boolean append(final InputStream theGraph, final RDFFormat theFormat, final URI theGraphURI) throws StoreException {
		//return dataOperation(Method.POST, theGraph, theFormat, theGraphURI);
		try {
			return append(IOUtil.readStringFromStream(theGraph), theFormat, theGraphURI);
		}
		catch (IOException e) {
			throw new StoreException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	public boolean append(final Graph theGraph, URI theGraphURI) throws StoreException {
		try {
			StringWriter aWriter = new StringWriter();

			OpenRdfIO.writeGraph(theGraph, aWriter, RDFFormat.NTRIPLES);

			return append(aWriter.toString(), RDFFormat.NTRIPLES, theGraphURI);
		}
		catch (IOException e) {
			throw new StoreException(e);
		}
	}

	/**
	 * Perform the specified data operation on the server
	 * @param theMethod the HTTP method to invoke
	 * @param theGraph the RDF data
	 * @param theFormat the RDF syntax format the data is in
	 * @param theGraphURI the graph URI for the operation
	 * @return true if the operation was a success, false otherwise
	 * @throws StoreException if there is an error invoking the operation
	 */
	private boolean dataOperation(final Method theMethod, final String theGraph, final RDFFormat theFormat, final URI theGraphURI) throws StoreException {
		try {
			return dataOperation(theMethod, new ByteArrayInputStream(theGraph.getBytes(Encoder.UTF8.name())), theFormat, theGraphURI);
		}
		catch (UnsupportedEncodingException e) {
			throw new StoreException(e);
		}
	}

	/**
	 * Perform the specified data operation on the server
	 * @param theMethod the HTTP method to invoke
	 * @param theGraph the input stream containing the RDF data
	 * @param theFormat the RDF syntax format the data is in
	 * @param theGraphURI the graph URI for the operation
	 * @return true if the operation was a success, false otherwise
	 * @throws StoreException if there is an error invoking the operation
	 */
	private boolean dataOperation(final Method theMethod, final InputStream theGraph, final RDFFormat theFormat, final URI theGraphURI) throws StoreException {
		HttpResource aRes = mFourStoreResource.resource("data");

		if (theGraphURI != null) {
			// does this need to be URL encoded?
			aRes = aRes.resource(theGraphURI.toString());
		}
		else {
			// i think 4store requires these operations to be in a named subgraph, i dont think there's some generic
			// global un-named blob of data that you can stick stuff into.  so if a subgraph is not specified
			// we'll just stick everything into the catch-all subgraph of our choosing...
			aRes = aRes.resource(DEFAULT_SUBGRAPH);
		}

		try {
			Response aResponse = aRes.initRequest(theMethod)
					.addHeader(HttpHeaders.ContentType.getName(), theFormat.getDefaultMIMEType())
					.setBody(theGraph)
					.execute();

			if (aResponse.hasErrorCode()) {
				throw responseToStoreException(aResponse);
			}
			else {
				return isSuccess(aResponse);
			}
		}
		catch (IOException e) {
			throw new StoreException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	public long size() throws StoreException {
		HttpResource aRes = mFourStoreResource.resource("status").resource("size");

		try {
			Response aResponse = aRes.get();

			if (aResponse.hasErrorCode()) {
				throw responseToStoreException(aResponse);
			}
			else {
				//return Long.parseLong(aResponse.getContent());
				// page source looks like:  <th>Total</th><td>3144265</td
				// so when we find Total, we want to move up 5 (Total) + 5 (</th>) + 4 (<td>)
				// and grab data until the next <
				String aStr = aResponse.getContent();
				int aStartIndex = aStr.indexOf("Total") + 14;
				return Long.parseLong(aStr.substring(aStartIndex, aStr.indexOf("<", aStartIndex)));
			}
		}
		catch (IOException e) {
			throw new StoreException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	public String status() throws StoreException {
		HttpResource aRes = mFourStoreResource.resource("status");

		try {
			Response aResponse = aRes.get();

			if (aResponse.hasErrorCode()) {
				throw responseToStoreException(aResponse);
			}
			else {
				return aResponse.getContent();
			}
		}
		catch (IOException e) {
			throw new StoreException(e);
		}
	}

	/**
	 * Given a response, return it as a StoreException by parsing out the errore message and content
	 * @param theResponse the response which indicate a server error
	 * @return the Response as an Exception
	 */
	private StoreException responseToStoreException(Response theResponse) {
		return new StoreException("(" + theResponse.getResponseCode() + ") " + theResponse.getMessage() + "\n\n" + theResponse.getContent());
	}
}
