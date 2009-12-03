package fourstore.impl;

import com.clarkparsia.sesame.utils.ExtendedGraph;
import com.clarkparsia.sesame.utils.SesameIO;
import com.clarkparsia.utils.io.Encoder;
import fourstore.api.Format;
import fourstore.api.QueryException;
import fourstore.api.ResultFormat;
import fourstore.api.Store;
import fourstore.api.StoreException;
import fourstore.api.rdf.Graph;
import fourstore.api.rdf.Resource;
import fourstore.api.rdf.Statement;
import fourstore.api.rdf.URI;
import fourstore.api.rdf.Value;
import fourstore.api.results.ResultSet;
import fourstore.impl.rdf.FourStoreValueFactory;
import fourstore.impl.results.RdfXmlResultSetParser;
import fourstore.impl.results.ResultSetBuilder;
import org.openrdf.rio.ParseException;
import org.openrdf.sesame.constants.RDFFormat;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import web.HttpHeaders;
import web.HttpResource;
import web.MimeTypes;
import web.ParameterList;
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
	private static final boolean DEBUG = true;

	public static final String PARAM_QUERY = "query";
	public static final String PARAM_SOFT_LIMIT = "soft-limit";

	private int mSoftLimit;

	private URL mBaseURL;

	private HttpResource mFourStoreResource;

	/**
	 * Create a new StoreImpl
	 * @param theURL the URL of the 4Store instance
	 */
	public StoreImpl(URL theURL) {
		mBaseURL = theURL;
		mSoftLimit = -1;

		mFourStoreResource = new HttpResourceImpl(mBaseURL);

		// TODO: don't allow operations until you are connected
	}

	/**
	 * @inheritDoc
	 */
	public int getSoftLimit() {
		return mSoftLimit;
	}

	public boolean hasStatement(final Statement theStmt) throws StoreException {
		throw new RuntimeException("NYI");
	}

	public boolean hasStatement(final Resource theSubj, final URI thePred, final Value theObj) throws StoreException {
		throw new RuntimeException("NYI");
	}

	public boolean hasStatement(final java.net.URI theGraph, final Statement theStmt) throws StoreException {
		throw new RuntimeException("NYI");
	}

	public boolean hasStatement(final java.net.URI theGraph, final Resource theSubj, final URI thePred, final Value theObj) throws StoreException {
		throw new RuntimeException("NYI");
	}

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
			Response aResponse = aRes.initPost()
					.addHeader(HttpHeaders.ContentType.getName(), MimeTypes.FormUrlEncoded.getMimeType())
					.addHeader(HttpHeaders.Accept.getName(), ResultFormat.XML.getMimeType())
					.setBody(aParams.getURLEncoded())
					.execute();

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

					aParser.parse(new InputSource(new ByteArrayInputStream(aResponse.getContent().getBytes(Encoder.UTF8))));

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
			// pull out the comment lines w/ the error?
			throw new QueryException("Parse Error\n" + aContent);
		}
	}

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
			Response aResponse = aRes.initPost()
					.addHeader(HttpHeaders.ContentType.getName(), MimeTypes.FormUrlEncoded.getMimeType())
					// TODO: why doesn't setting the accept header work here?
					.addHeader(HttpHeaders.Accept.getName(), Format.Turtle.getMimeType())
					.setBody(aParams.getURLEncoded())
					.execute();

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

					// TODO: actually pass back the graph!!
					return new Graph(){};
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

	public Graph describe(final URI theConcept) throws QueryException {
		throw new RuntimeException("NYI");
	}

	public boolean ask(final URI theConcept) throws QueryException {
		throw new RuntimeException("NYI");
	}

	public boolean add(final String theGraph, final Format theFormat, final java.net.URI theGraphURI) throws StoreException {
		HttpResource aRes = mFourStoreResource.resource("data");

		if (theGraphURI != null) {
			aRes = aRes.resource(Encoder.urlEncode(theGraphURI.toString()));
		}

		try {
			Response aResponse = aRes.initPut()
					.addHeader(HttpHeaders.ContentType.getName(), theFormat.getMimeType())
					.setBody(theGraph)
					.execute();

			if (aResponse.hasErrorCode()) {
				throw responseToStoreException(aResponse);
			}
			else {
				if (DEBUG) System.err.println(aResponse.getMessage() + "\n" + aResponse.getContent());

				// TODO: is there a better indication of success?
				if (aResponse.getResponseCode() == 200) {
					return true;
				}
				else {
					return false;
				}
			}
		}
		catch (IOException e) {
			throw new StoreException(e);
		}
	}

	public boolean delete(final String theGraph, final Format theFormat, final java.net.URI theGraphURI) throws StoreException {
		HttpResource aRes = mFourStoreResource.resource("data");

		if (theGraphURI != null) {
			aRes = aRes.resource(Encoder.urlEncode(theGraphURI.toString()));
		}

		try {
			Response aResponse = aRes.initDelete()
					.addHeader(HttpHeaders.ContentType.getName(), theFormat.getMimeType())
					.setBody(theGraph)
					.execute();

			if (aResponse.hasErrorCode()) {
				throw responseToStoreException(aResponse);
			}
			else {
				if (DEBUG) System.err.println(aResponse.getMessage() + "\n" + aResponse.getContent());

				// TODO: is there a better indication of success?
				if (aResponse.getResponseCode() == 200) {
					return true;
				}
				else {
					return false;
				}
			}
		}
		catch (IOException e) {
			throw new StoreException(e);
		}
	}

	public boolean delete(final java.net.URI theGraphURI) throws StoreException {
		HttpResource aRes = mFourStoreResource.resource("data");

		if (theGraphURI != null) {
			aRes = aRes.resource(Encoder.urlEncode(theGraphURI.toString()));
		}

		try {
			Response aResponse = aRes.delete();

			if (aResponse.hasErrorCode()) {
				throw responseToStoreException(aResponse);
			}
			else {
				if (DEBUG) System.err.println(aResponse.getMessage() + "\n" + aResponse.getContent());

				// TODO: is there a better indication of success?
				if (aResponse.getResponseCode() == 200) {
					return true;
				}
				else {
					return false;
				}
			}
		}
		catch (IOException e) {
			throw new StoreException(e);
		}
	}

	public boolean append(final String theGraph, final Format theFormat, final java.net.URI theGraphURI) throws StoreException {
		throw new RuntimeException("NYI");
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
