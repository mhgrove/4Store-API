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

import info.aduna.iteration.CloseableIteratorIteration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.URL;
import java.util.Collections;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.impl.TupleQueryResultBuilder;
import org.openrdf.query.resultio.QueryResultIO;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.query.resultio.TupleQueryResultParser;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import com.clarkparsia.common.web.HttpHeaders;
import com.clarkparsia.common.web.HttpResource;
import com.clarkparsia.common.web.HttpResourceImpl;
import com.clarkparsia.common.web.Method;
import com.clarkparsia.common.web.MimeTypes;
import com.clarkparsia.common.web.ParameterList;
import com.clarkparsia.common.web.Request;
import com.clarkparsia.common.web.Response;
import com.clarkparsia.fourstore.api.QueryException;
import com.clarkparsia.fourstore.api.Store;
import com.clarkparsia.fourstore.api.StoreException;
import com.clarkparsia.openrdf.OpenRdfIO;
import com.clarkparsia.openrdf.query.SesameQueryUtils;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

/**
 * <p>
 * Implementation of Store interface which interacts with a 4Store database over
 * their RESTful HTTP protocol as <a
 * href="http://4store.org/trac/wiki/SparqlServer">documented here.
 * </p>
 * 
 * @author Michael Grove
 * @since 0.1
 * @version 0.3.1
 */
public class StoreImpl implements Store {
  private static final String DEFAULT_SUBGRAPH = "http://clarkparsia.com/4store/repository";

  public static final String PARAM_QUERY = "query";
  public static final String PARAM_SOFT_LIMIT = "soft-limit";

  /**
   * The read endpoint of 4-store. Used for querying.
   */
  private static final String SPARQL_READ_ENDPOINT = "sparql/";

  /**
   * The write endpoint of 4-store. Used for updates and deletes.
   */
  private static final String SPARQL_WRITE_ENDPOINT = "update/";

  /**
   * The data endpoint of 4-store. Used for adding and removing data via files
   * and urls.
   */
  private static final String SPARQL_DATA_ENDPOINT = "data/";

  /**
   * The status endpoint of 4-store. Used for status checks.
   */
  private static final String SPARQL_STATUS_ENDPOINT = "status/";

  /**
   * The size endpoint of 4-store. Used for storage status checks.
   */
  private static final String SPARQL_SIZE_ENDPOINT = SPARQL_STATUS_ENDPOINT + "size/";

  /**
   * The default length to read from the response when checking for errors.
   */
  private static final int DEFAULT_READ_LENGTH = 20;

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
   * Whether or not to use GET requests for queries to the servers. Use POST by
   * default just in the case that queries in GET requests can be too long for
   * servers to handle.
   * 
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
   * 
   * @param theURL
   *          the URL of the 4Store instance
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
      aQuery += "filter(?s = " + SesameQueryUtils.getSPARQLQueryString(theSubj) + "). ";
    }

    if (thePred != null) {
      aQuery += "filter(?p = " + SesameQueryUtils.getSPARQLQueryString(thePred) + "). ";
    }

    if (theObj != null) {
      aQuery += "filter(?o = " + SesameQueryUtils.getSPARQLQueryString(theObj) + "). ";
    }

    aQuery += " ?s ?p ?o.} limit 1";

    try {
      TupleQueryResult aResult = query(aQuery);

      boolean aAnswer = aResult.hasNext();

      aResult.close();

      return aAnswer;
    } catch (QueryEvaluationException e) {
      throw new StoreException(e);
    }
  }

  /**
   * @inheritDoc
   */
  public RepositoryResult<Statement> getStatements(final Resource theSubj, final URI thePred, final Value theObj)
      throws StoreException {
    if (theSubj == null && thePred == null && theObj == null) {
      throw new StoreException("You must bind at least one value to this function.");
    } else if (theSubj != null && thePred != null && theObj != null) {
      return new RepositoryResult<Statement>(new CloseableIteratorIteration<Statement, RepositoryException>(Collections
          .singleton(new StatementImpl(theSubj, thePred, theObj)).iterator()));
    }

    String aQuery = "construct { ?s ?p ?o } where { ";

    if (theSubj != null) {
      aQuery += "filter(?s = " + SesameQueryUtils.getSPARQLQueryString(theSubj) + "). ";
    }

    if (thePred != null) {
      aQuery += "filter(?p = " + SesameQueryUtils.getSPARQLQueryString(thePred) + "). ";
    }

    if (theObj != null) {
      aQuery += "filter(?o = " + SesameQueryUtils.getSPARQLQueryString(theObj) + "). ";
    }

    aQuery += " ?s ?p ?o.}";

    Graph aResult = constructQuery(aQuery);

    return new RepositoryResult<Statement>(new CloseableIteratorIteration<Statement, RepositoryException>(
        aResult.iterator()));
  }

  /**
   * @inheritDoc
   */
  public void connect() throws ConnectException {
    try {
      Response aResp = mFourStoreResource.resource(SPARQL_STATUS_ENDPOINT).get();

      if (aResp.hasErrorCode()) {
        throw new ConnectException("There was an error connecting to the store: " + aResp.getMessage() + "\n"
            + aResp.getContent());
      }

      mIsConnected = true;
    } catch (IOException e) {
      throw new ConnectException(e.getMessage());
    }
  }

  /**
   * Enforce an "open" connection to use the database
   * 
   * @throws IllegalArgumentException
   *           if there is no open connection.
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
    return internalQuery(theQuery, TupleQueryResultFormat.SPARQL);
  }

  /**
   * Dispatch a query to the sparql endpoint of the store
   * 
   * @param theQuery
   *          the query to send
   * @param theAccept
   *          the result format to send back
   * @return the sparql result set handler which parsed the results
   * @throws QueryException
   *           if there is an error while querying
   */
  private TupleQueryResult internalQuery(String theQuery, TupleQueryResultFormat theAccept) throws QueryException {
    // TODO: this really only works for sparql/xml results, generalize it to
    // work for any sparql results format.

    HttpResource aRes = mFourStoreResource.resource(SPARQL_READ_ENDPOINT);

    String aQuery = theQuery;

    // auto prefix queries w/ rdf and rdfs namespaces
    aQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
        + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + aQuery;

    ParameterList aParams = new ParameterList().add(PARAM_QUERY, aQuery).add(PARAM_SOFT_LIMIT,
        String.valueOf(getSoftLimit()));

    try {
      Request aQueryRequest;

      if (mUseGetForQueries) {
        aQueryRequest = aRes.initGet().addHeader(HttpHeaders.Accept.getName(), theAccept.getDefaultMIMEType())
            .setParameters(aParams);
      } else {
        aQueryRequest = aRes.initPost()
            .addHeader(HttpHeaders.ContentType.getName(), MimeTypes.FormUrlEncoded.getMimeType())
            .addHeader(HttpHeaders.Accept.getName(), theAccept.getDefaultMIMEType()).setBody(aParams.getURLEncoded());
      }

      Response aResponse = aQueryRequest.execute();

      try {
        if (aResponse.hasErrorCode()) {
          throw new QueryException(responseToStoreException(aResponse));
        } else {
          checkResultsForError(aResponse);

          // TODO: pull out and do something w/ information about hitting the
          // soft limit. this is how
          // it looks in the results xml.
          // <!-- warning: hit complexity limit 2 times, increasing soft limit
          // may give more results -->

          TupleQueryResultBuilder aBuilder = new TupleQueryResultBuilder();
          TupleQueryResultParser aParser = QueryResultIO.createParser(theAccept);
          aParser.setTupleQueryResultHandler(aBuilder);
          aParser.parse(aResponse.getContent());

          return aBuilder.getQueryResult();
        }
      } finally {
        aResponse.close();
      }
    } catch (Exception e) {
      throw new QueryException(e);
    }
  }

  /**
   * Return whether or not the response contains any error messages
   * 
   * @param theResponse
   *          the response to check
   * @throws QueryException
   *           true if it contains error messages, false otherwise
   */
  private void checkResultsForError(Response theResponse) throws IOException, QueryException {
    String aContent = readFirstBytes(theResponse.getContent());

    String aToken = "error";

    // TODO: could stand for more robust error checking.
    if (aContent.indexOf(aToken) != -1) {
      int aStart = aContent.indexOf(aToken) + aToken.length();
      throw new QueryException("Parse Error:" + aContent.substring(aStart, aContent.indexOf("-->", aStart)));
    }

  }

  /**
   * Reads the first n bytes of the given input stream and returns their string
   * representation. The number of bytes n to read is setup via
   * {@link StoreImpl#DEFAULT_READ_LENGTH}.
   * 
   * @param content
   *          the input stream to read from.
   * @return a string of the first n bytes that were read.
   * @throws IOException
   *           if an error occurs during reading from the stream.
   */
  private String readFirstBytes(InputStream content) throws IOException {
    byte[] bytes = new byte[DEFAULT_READ_LENGTH];
    content.mark(DEFAULT_READ_LENGTH);

    for (int i = 0; i < DEFAULT_READ_LENGTH; i++) {
      int b = content.read();
      if (b == -1)
        break;

      bytes[i] = (byte) b;
    }

    content.reset();
    return new String(bytes);
  }

  /**
   * @inheritDoc
   */
  public Graph constructQuery(final String theQuery) throws QueryException {
    HttpResource aRes = mFourStoreResource.resource(SPARQL_READ_ENDPOINT);

    String aQuery = theQuery;

    // auto prefix queries w/ rdf and rdfs namespaces
    aQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
        + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + aQuery;

    ParameterList aParams = new ParameterList().add(PARAM_QUERY, aQuery).add(PARAM_SOFT_LIMIT,
        String.valueOf(getSoftLimit()));

    try {
      Request aQueryRequest;

      if (mUseGetForQueries) {
        aQueryRequest = aRes.initGet()
        // TODO: why doesn't setting the accept header work here?
            .addHeader(HttpHeaders.Accept.getName(), RDFFormat.TURTLE.getDefaultMIMEType()).setParameters(aParams);
      } else {
        aQueryRequest = aRes.initPost()
            .addHeader(HttpHeaders.ContentType.getName(), MimeTypes.FormUrlEncoded.getMimeType())
            // TODO: why doesn't setting the accept header work here?
            .addHeader(HttpHeaders.Accept.getName(), RDFFormat.TURTLE.getDefaultMIMEType())
            .setBody(aParams.getURLEncoded());
      }

      Response aResponse = aQueryRequest.execute();

      try {
        if (aResponse.hasErrorCode()) {
          throw new QueryException(responseToStoreException(aResponse));
        } else {
          checkResultsForError(aResponse);

          // TODO: pull out and do something w/ information about hitting the
          // soft limit. this is how
          // it looks in the results xml.
          // <!-- warning: hit complexity limit 2 times, increasing soft limit
          // may give more results -->

          try {
            return OpenRdfIO.readGraph(aResponse.getContent(), RDFFormat.RDFXML);
          } catch (RDFParseException e) {
            throw new QueryException("Error while parsing rdf/xml-formatted query results", e);
          }
        }
      } finally {
        aResponse.close();
      }
    } catch (IOException e) {
      throw new QueryException(e);
    }
  }

  /**
   * @inheritDoc
   */
  public Graph describe(final String theQuery) throws QueryException {
    // this should be sufficient. describe's return an RDF graph as the result,
    // and we don't do anything in
    // the constructQuery method specific to constructs other than parsing the
    // result as an RDF graph, which
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
    } catch (IOException e) {
      throw new StoreException(e);
    }
  }

  /**
   * @inheritDoc
   */
  public boolean add(final InputStream theGraph, final RDFFormat theFormat, final URI theGraphURI)
      throws StoreException {
    return dataOperation(Method.PUT, theGraph, theFormat, theGraphURI);
  }

  /**
   * @inheritDoc
   */
  public boolean delete(final Graph theGraph, final URI theGraphURI) throws StoreException {
    HttpResource aRes = mFourStoreResource.resource(SPARQL_WRITE_ENDPOINT);

    StringBuffer aQuery = new StringBuffer();

    for (Statement aStmt : theGraph) {
      aQuery.append(SesameQueryUtils.getSPARQLQueryString(aStmt.getSubject())).append(" ")
          .append(SesameQueryUtils.getSPARQLQueryString(aStmt.getPredicate())).append(" ")
          .append(SesameQueryUtils.getSPARQLQueryString(aStmt.getObject())).append(".\n");
    }

    // not sure if this works?
    if (theGraphURI != null) {
      aQuery.insert(0, " graph <" + theGraphURI + "> {\n").append(" }");
    }

    // adding a where clause here...
    aQuery.insert(0, "delete where { ").append(" }");

    ParameterList aParams = new ParameterList().add("update", aQuery.toString());

    try {
      Request aQueryRequest = aRes
          .initPost()
          .addHeader(HttpHeaders.ContentType.getName(), MimeTypes.FormUrlEncoded.getMimeType())
          .addHeader(HttpHeaders.ContentLength.getName(),
              Integer.toString(aParams.getURLEncoded().getBytes(Charsets.UTF_8).length))
          .setBody(aParams.getURLEncoded());

      Response aResponse = aQueryRequest.execute();

      if (aResponse.hasErrorCode()) {
        throw new QueryException(responseToStoreException(aResponse));
      } else {
        checkResultsForError(aResponse);

        return true;
      }
    } catch (IOException e) {
      throw new QueryException(e);
    }
  }

  /**
   * @inheritDoc
   */
  public boolean delete(final URI theGraphURI) throws StoreException {
    HttpResource aRes = mFourStoreResource.resource(SPARQL_DATA_ENDPOINT);

    if (theGraphURI != null) {
      aRes = aRes.resource(theGraphURI.toString());
    } else {
      throw new StoreException("No graph specified to delete");
    }

    try {
      Response aResponse = aRes.delete();

      if (aResponse.hasErrorCode()) {
        throw responseToStoreException(aResponse);
      } else {
        return isSuccess(aResponse);
      }
    } catch (IOException e) {
      throw new StoreException(e);
    }
  }

  /**
   * Returns whether or not the response indicates the operation was successful
   * 
   * @param theResponse
   *          the response to check
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
    HttpResource aRes = mFourStoreResource.resource(SPARQL_DATA_ENDPOINT);

    ParameterList aParams = new ParameterList();
    aParams.add("mime-type", theFormat.getDefaultMIMEType()).add("data", theGraph + "\n");

    if (theGraphURI != null) {
      aParams.add("graph", theGraphURI.toString());
    } else {
      aParams.add("graph", DEFAULT_SUBGRAPH);
    }

    try {
      Response aResponse = aRes.initPost().setBody(aParams.getURLEncoded()).execute();

      if (aResponse.hasErrorCode()) {
        throw responseToStoreException(aResponse);
      } else {
        return isSuccess(aResponse);
      }
    } catch (IOException e) {
      throw new StoreException(e);
    }
  }

  /**
   * @inheritDoc
   */
  public boolean append(final InputStream theGraph, final RDFFormat theFormat, final URI theGraphURI)
      throws StoreException {
    // return dataOperation(Method.POST, theGraph, theFormat, theGraphURI);
    try {
      return append(new String(ByteStreams.toByteArray(theGraph)), theFormat, theGraphURI);
    } catch (IOException e) {
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
    } catch (IOException e) {
      throw new StoreException(e);
    }
  }

  /**
   * Perform the specified data operation on the server
   * 
   * @param theMethod
   *          the HTTP method to invoke
   * @param theGraph
   *          the RDF data
   * @param theFormat
   *          the RDF syntax format the data is in
   * @param theGraphURI
   *          the graph URI for the operation
   * @return true if the operation was a success, false otherwise
   * @throws StoreException
   *           if there is an error invoking the operation
   */
  private boolean dataOperation(final Method theMethod, final String theGraph, final RDFFormat theFormat,
      final URI theGraphURI) throws StoreException {
    return dataOperation(theMethod, new ByteArrayInputStream(theGraph.getBytes(Charsets.UTF_8)), theFormat, theGraphURI);
  }

  /**
   * Perform the specified data operation on the server
   * 
   * @param theMethod
   *          the HTTP method to invoke
   * @param theGraph
   *          the input stream containing the RDF data
   * @param theFormat
   *          the RDF syntax format the data is in
   * @param theGraphURI
   *          the graph URI for the operation
   * @return true if the operation was a success, false otherwise
   * @throws StoreException
   *           if there is an error invoking the operation
   */
  private boolean dataOperation(final Method theMethod, final InputStream theGraph, final RDFFormat theFormat,
      final URI theGraphURI) throws StoreException {
    HttpResource aRes = mFourStoreResource.resource(SPARQL_DATA_ENDPOINT);

    if (theGraphURI != null) {
      // does this need to be URL encoded?
      aRes = aRes.resource(theGraphURI.toString());
    } else {
      // i think 4store requires these operations to be in a named subgraph, i
      // dont think there's some generic
      // global un-named blob of data that you can stick stuff into. so if a
      // subgraph is not specified
      // we'll just stick everything into the catch-all subgraph of our
      // choosing...
      aRes = aRes.resource(DEFAULT_SUBGRAPH);
    }

    try {
      Response aResponse = aRes.initRequest(theMethod)
          .addHeader(HttpHeaders.ContentType.getName(), theFormat.getDefaultMIMEType()).setBody(theGraph).execute();

      if (aResponse.hasErrorCode()) {
        throw responseToStoreException(aResponse);
      } else {
        return isSuccess(aResponse);
      }
    } catch (IOException e) {
      throw new StoreException(e);
    }
  }

  /**
   * @inheritDoc
   */
  public long size() throws StoreException {
    HttpResource aRes = mFourStoreResource.resource(SPARQL_SIZE_ENDPOINT);

    Response aResponse = null;
    try {
      aResponse = aRes.get();

      if (aResponse.hasErrorCode()) {
        throw responseToStoreException(aResponse);
      } else {
        // return Long.parseLong(aResponse.getContent());
        // page source looks like: <th>Total</th><td>3144265</td
        // so when we find Total, we want to move up 5 (Total) + 5 (</th>) + 4
        // (<td>)
        // and grab data until the next <
        String aStr = new String(ByteStreams.toByteArray(aResponse.getContent()));
        int aStartIndex = aStr.indexOf("Total") + 14;
        return Long.parseLong(aStr.substring(aStartIndex, aStr.indexOf("<", aStartIndex)));
      }
    } catch (IOException e) {
      throw new StoreException(e);
    } finally {
      if (aResponse != null) {
        try {
          aResponse.close();
        } catch (IOException e) {
          // swallow
        }
      }
    }
  }

  /**
   * @inheritDoc
   */
  public String status() throws StoreException {
    HttpResource aRes = mFourStoreResource.resource(SPARQL_STATUS_ENDPOINT);

    Response aResponse = null;
    try {
      aResponse = aRes.get();

      if (aResponse.hasErrorCode()) {
        throw responseToStoreException(aResponse);
      } else {
        return new String(ByteStreams.toByteArray(aResponse.getContent()));
      }
    } catch (IOException e) {
      throw new StoreException(e);
    } finally {
      if (aResponse != null) {
        try {
          aResponse.close();
        } catch (IOException e) {
          // swallow
        }
      }
    }
  }

  /**
   * Given a response, return it as a StoreException by parsing out the errore
   * message and content
   * 
   * @param theResponse
   *          the response which indicate a server error
   * @return the Response as an Exception
   */
  private StoreException responseToStoreException(Response theResponse) {
    return new StoreException("(" + theResponse.getResponseCode() + ") " + theResponse.getMessage() + "\n\n"
        + theResponse.getContent());
  }
}
