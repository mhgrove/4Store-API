package fourstore.impl;

import com.clarkparsia.sesame.utils.ExtendedGraph;
import com.clarkparsia.sesame.utils.SesameIO;

import fourstore.api.Format;
import fourstore.api.QueryException;
import fourstore.api.ResultFormat;
import fourstore.api.Store;
import fourstore.api.StoreException;
import fourstore.api.rdf.BNode;
import fourstore.api.rdf.Graph;
import fourstore.api.rdf.Literal;
import fourstore.api.rdf.Resource;
import fourstore.api.rdf.Statement;
import fourstore.api.rdf.URI;
import fourstore.api.rdf.Value;
import fourstore.api.results.ResultSet;

import fourstore.impl.rdf.FourStoreValueFactory;

import fourstore.impl.results.RdfXmlResultSetParser;
import fourstore.impl.results.ResultSetBuilder;

import fourstore.impl.sesame.SesameToFourStore;


import org.openrdf.rio.ParseException;

import org.openrdf.sesame.constants.RDFFormat;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import web.HttpHeaders;
import web.HttpResource;
import web.Method;
import web.MimeTypes;
import web.ParameterList;
import web.Request;
import web.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;

import java.net.ConnectException;
import java.net.URL;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 2, 2009 9:22:12 AM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public class StoreImpl implements Store {
	private static final String DEFAULT_SUBGRAPH = "http://clarkparsia.com/4store/repository";

	// TODO: remove me
	private static final boolean DEBUG = false;

	public static final String PARAM_QUERY = "query";
	public static final String PARAM_SOFT_LIMIT = "soft-limit";

	private int mSoftLimit;

	private URL mBaseURL;

	private HttpResource mFourStoreResource;

	/**
	 * Whether or not to use GET requests for queries to the servers.  Use POST by default just in the case that
	 * queries in GET requests can be too long for servers to handle.
	 * @see StoreFactory
	 */
	private boolean mUseGetForQueries = false;

	StoreImpl(final URL theBaseURL, final boolean theUseGetForQueries) {
		mBaseURL = theBaseURL;
		mSoftLimit = -1;

		mUseGetForQueries = theUseGetForQueries;

		mFourStoreResource = new HttpResourceImpl(mBaseURL);

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
		String aQuery = "select ?s ?p ?o where { ";

		if (theSubj != null) {
			aQuery += "filter(?s = " + toQueryString(theSubj) + "). ";
		}

		if (thePred != null) {
			aQuery += "filter(?p = " + toQueryString(thePred) + "). ";
		}

		if (theObj != null) {
			aQuery += "filter(?o = " + toQueryString(theObj) + "). ";
		}

		aQuery += " ?s ?p ?o.} limit 1";

		return !query(aQuery).isEmpty();
	}

    public static String toQueryString(Value theValue) {
        StringBuffer aBuffer = new StringBuffer();

        if (theValue instanceof URI) {
            URI aURI = (URI) theValue;
            aBuffer.append("<").append(aURI.getURI()).append(">");
        }
        else if (theValue instanceof BNode) {
            aBuffer.append("_:").append(((BNode)theValue).getId());
        }
        else if (theValue instanceof Literal) {
            Literal aLit = (Literal)theValue;
            aBuffer.append("\"").append(escape(aLit.getValue())).append("\"").append(aLit.getLanguage() != null ? "@" + aLit.getLanguage() : "");
            if (aLit.getDatatype() != null) {
                aBuffer.append("^^<").append(aLit.getDatatype().toString()).append(">");
            }
        }

        return aBuffer.toString();
    }

    private static String escape(String theString) {
        theString = theString.replaceAll("\"", "\\\\\"");

        return theString;
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
		}
		catch (IOException e) {
			throw new ConnectException(e.getMessage());
		}
	}

	/**
	 * @inheritDoc
	 */
	public void disconnect() throws ConnectException {
		// no clean-up needed
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
	public ResultSet query(String theQuery) throws QueryException {
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
						.addHeader(HttpHeaders.Accept.getName(), ResultFormat.XML.getMimeType())
						.setParameters(aParams);
			}
			else {
				aQueryRequest = aRes.initPost()
						.addHeader(HttpHeaders.ContentType.getName(), MimeTypes.FormUrlEncoded.getMimeType())
						.addHeader(HttpHeaders.Accept.getName(), ResultFormat.XML.getMimeType())
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
					// TODO: probably make the call to create the value factory a function which can be overridden by sub classes, such as the one w/ sesame support?
					RdfXmlResultSetParser aHandler = new RdfXmlResultSetParser(new ResultSetBuilder(new FourStoreValueFactory()));

					XMLReader aParser = org.xml.sax.helpers.XMLReaderFactory.createXMLReader();

					aParser.setContentHandler(aHandler);
					aParser.setFeature("http://xml.org/sax/features/validation", false);

					aParser.parse(new InputSource(new ByteArrayInputStream(aResponse.getContent().getBytes("UTF-8"))));

					return aHandler.resultSet();
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

	private void checkResultsForError(Response theResponse) throws QueryException {
		String aContent = theResponse.getContent();
		
		// TODO: could stand for more robust error checking.
		if (aContent.indexOf("parser error:") != -1) {
			// TODO: pull out the comment lines w/ the error?
			throw new QueryException("Parse Error\n" + aContent);
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
						.addHeader(HttpHeaders.Accept.getName(), Format.Turtle.getMimeType())
						.setParameters(aParams);
			}
			else {
				aQueryRequest = aRes.initPost()
						.addHeader(HttpHeaders.ContentType.getName(), MimeTypes.FormUrlEncoded.getMimeType())
						// TODO: why doesn't setting the accept header work here?
						.addHeader(HttpHeaders.Accept.getName(), Format.Turtle.getMimeType())
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
					ExtendedGraph aGraph = SesameIO.readGraph(new StringReader(aResponse.getContent()),
															  RDFFormat.RDFXML);

					return SesameToFourStore.toGraph(aGraph);
				}
				catch (ParseException e) {
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
	public Graph describe(final URI theConcept) throws QueryException {
		throw new RuntimeException("NYI");
	}

	/**
	 * @inheritDoc
	 */
	public boolean ask(final URI theConcept) throws QueryException {
		throw new RuntimeException("NYI");
	}


	/**
	 * @inheritDoc
	 */
	public boolean add(final String theGraph, final Format theFormat, final java.net.URI theGraphURI) throws StoreException {
		return dataOperation(Method.PUT, theGraph, theFormat, theGraphURI);
	}

	/**
	 * @inheritDoc
	 */
	public boolean delete(final String theGraph, final Format theFormat, final java.net.URI theGraphURI) throws StoreException {
		return dataOperation(Method.DELETE, theGraph, theFormat, theGraphURI);
	}

	/**
	 * @inheritDoc
	 */
	public boolean delete(final java.net.URI theGraphURI) throws StoreException {
		HttpResource aRes = mFourStoreResource.resource("data");

		if (theGraphURI != null) {
			// TODO: does this need to be URL encoded?
			aRes = aRes.resource(theGraphURI.toString());
		}
		else {
			// TODO: should we just delete the default subgraph here?

			throw new StoreException("No graph specified to delete");
		}

		try {
			Response aResponse = aRes.delete();

			if (aResponse.hasErrorCode()) {
				throw responseToStoreException(aResponse);
			}
			else {
				if (DEBUG) System.err.println(aResponse.getMessage() + "\n" + aResponse.getContent());

				// TODO: is there a better indication of success?
				return aResponse.getResponseCode() == 200;
			}
		}
		catch (IOException e) {
			throw new StoreException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	public boolean append(final String theGraph, final Format theFormat, final java.net.URI theGraphURI) throws StoreException {
		return dataOperation(Method.POST, theGraph, theFormat, theGraphURI);
	}

	private boolean dataOperation(final Method theMethod, final String theGraph, final Format theFormat, final java.net.URI theGraphURI) throws StoreException {
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
					.addHeader(HttpHeaders.ContentType.getName(), theFormat.getMimeType())
					.setBody(theGraph)
					.execute();

			if (aResponse.hasErrorCode()) {
				throw responseToStoreException(aResponse);
			}
			else {
				if (DEBUG) System.err.println(aResponse.getMessage() + "\n" + aResponse.getContent());

				// TODO: is there a better indication of success?
				return aResponse.getResponseCode() == 200;
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

	private StoreException responseToStoreException(Response theResponse) {
		return new StoreException(theResponse.getMessage() + "\n\n" + theResponse.getContent());
	}
}
