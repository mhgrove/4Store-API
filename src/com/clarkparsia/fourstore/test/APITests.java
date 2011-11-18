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

package com.clarkparsia.fourstore.test;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Ignore;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.util.ModelUtil;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.model.impl.StatementImpl;

import org.openrdf.rio.RDFFormat;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;

import org.openrdf.repository.sail.SailRepository;

import org.openrdf.sail.memory.MemoryStore;

import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResultUtil;
import org.openrdf.query.GraphQueryResult;

import com.clarkparsia.fourstore.api.Store;
import com.clarkparsia.fourstore.api.StoreException;

import com.clarkparsia.fourstore.impl.StoreFactory;
import com.clarkparsia.fourstore.impl.StoreImpl;

import com.clarkparsia.fourstore.sesame.FourStoreSail;
import com.clarkparsia.fourstore.sesame.FourStoreSailRepository;

import com.clarkparsia.openrdf.OpenRdfIO;
import com.clarkparsia.openrdf.query.SesameQueryUtils;

import static com.clarkparsia.openrdf.OpenRdfUtil.iterable;
import com.clarkparsia.common.web.Response;
import com.clarkparsia.common.web.HttpHeaders;
import com.clarkparsia.common.web.MimeTypes;
import com.clarkparsia.common.web.Request;
import com.clarkparsia.common.web.ParameterList;
import com.clarkparsia.common.web.HttpResourceImpl;
import com.clarkparsia.common.web.HttpResource;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.net.URL;

import java.io.FileInputStream;
import java.io.StringWriter;
import java.io.ByteArrayInputStream;
import java.io.StringReader;

import java.io.File;

/**
 * <p>4Store API JUnit test suite.</p>
 *
 * @author Michael Grove
 */
public class APITests {
	private static final URI TEST_CONTEXT = ValueFactoryImpl.getInstance().createURI("http://4store.clarkparsia.com/test/");

	private static Store mStore;

	private static Graph TEST_DATA;
	private static Graph NASA_DATA;

	private static final String STORE_URL = "http://vx.int.clarkparsia.com:8000/";

	@BeforeClass
	public static void beforeClass() {
		try {
			mStore = StoreFactory.create(new URL(STORE_URL));

			NASA_DATA = OpenRdfIO.readGraph(new FileInputStream("test/nasa.nt"), RDFFormat.NTRIPLES);

			Graph aGraph = OpenRdfIO.readGraph(new FileInputStream("test/test_data.rdf"), RDFFormat.RDFXML);

			int count = 0;
			TEST_DATA = new GraphImpl();
			for (Statement aStmt : aGraph) {

				// for some reason, this is the fail point for deletes with 4store.  if our graph is bigger than this,
				// it won't get deleted properly.
				if (count < 4993) {
					TEST_DATA.add(aStmt);
					count++;
				}
				else {
					break;
				}
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testSoftLimit() {
		mStore.setSoftLimit(5);

		assertEquals(mStore.getSoftLimit(), 5);
	}

	@Test @Ignore // i know this will fail, bug in 4store perhaps
	public void testDeleteFailure() {
		try {
			// test graph context to delete from
			String aGraphURI = "http://4store.clarkparsia.com/test/";

			// read in a data file
//			Graph aGraph = OpenRdfIO.readGraph(new FileInputStream("test/test_data.rdf"), RDFFormat.RDFXML);
			Graph aGraph = OpenRdfIO.readGraph(new FileInputStream("test/nasa.nt"), RDFFormat.NTRIPLES);

			// iterate over the graph and make a smaller graph suitable to test the delete behavior
			int aCount = 0;
			Graph aDeleteGraph = new GraphImpl();
			for (Statement aStmt : aGraph) {

				// for some reason, this is the fail point for deletes with 4store.  if our graph is bigger than this,
				// it won't get deleted properly.  change to 4994 and it seems to die.
				if (aCount < 4994) {
					aDeleteGraph.add(aStmt);
					aCount++;
				}
				else {
					break;
				}
			}

			HttpResource aRes = new HttpResourceImpl(new URL("http://vx.int.clarkparsia.com:8000/")).resource("update");

			StringBuffer aQuery = new StringBuffer();

			for (Statement aStmt : aDeleteGraph) {
				aQuery.append(SesameQueryUtils.getSPARQLQueryString(aStmt.getSubject())).append(" ")
						.append(SesameQueryUtils.getSPARQLQueryString(aStmt.getPredicate())).append(" ")
						.append(SesameQueryUtils.getSPARQLQueryString(aStmt.getObject())).append(".\n");
			}

			if (aGraphURI != null) {
				aQuery.insert(0, " graph <" + aGraphURI + "> {\n").append(" }");
			}

			aQuery.insert(0, "delete { ").append(" }");

			ParameterList aParams = new ParameterList()
					.add("update", aQuery.toString());

			Request aQueryRequest = aRes.initPost()
					.addHeader(HttpHeaders.ContentType.getName(), MimeTypes.FormUrlEncoded.getMimeType())
					.addHeader(HttpHeaders.ContentLength.getName(), Integer.toString(aParams.getURLEncoded().getBytes(Charsets.UTF_8).length))
					.setBody(aParams.getURLEncoded());

			Response aResponse = aQueryRequest.execute();

			System.err.println(aResponse.getMessage());
			System.err.println(aResponse.getContent());

			assertTrue(aResponse.getResponseCode() == 200);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testCRUDWithContext() {
		try {
			Graph aGraphToAdd = TEST_DATA;

			mStore.add(aGraphToAdd, TEST_CONTEXT);

			assertContains(aGraphToAdd);

			Graph aUpdateGraph = generateGraph();

			mStore.append(aUpdateGraph, TEST_CONTEXT);

			assertContains(aUpdateGraph);

			mStore.delete(aUpdateGraph, TEST_CONTEXT);

			assertNotContains(aUpdateGraph);

			mStore.delete(TEST_CONTEXT);

			assertNotContains(aGraphToAdd);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testSize() {
		try {
			long aLocalSize = mStore.size();

			FourStoreSailRepository aRepo = new FourStoreSailRepository(new URL(STORE_URL));

			aRepo.initialize();

			long aSesameSize = aRepo.getConnection().size();

			assertEquals(aLocalSize, aSesameSize);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	private Graph generateGraph() {
		// TODO: maybe generate random ones, or read from a file on disk?

		Graph aUpdateGraph = new GraphImpl();

		aUpdateGraph.add(ValueFactoryImpl.getInstance().createURI("urn:subj"),
						 ValueFactoryImpl.getInstance().createURI("urn:prop"),
						 ValueFactoryImpl.getInstance().createURI("urn:obj"));

		aUpdateGraph.add(ValueFactoryImpl.getInstance().createURI("urn:subj"),
						 ValueFactoryImpl.getInstance().createURI("urn:prop"),
						 ValueFactoryImpl.getInstance().createURI("urn:value"));

		aUpdateGraph.add(ValueFactoryImpl.getInstance().createURI("urn:subj2"),
						 ValueFactoryImpl.getInstance().createURI("urn:prop2"),
						 ValueFactoryImpl.getInstance().createURI("urn:obj2"));

		aUpdateGraph.add(ValueFactoryImpl.getInstance().createURI("urn:a"),
						 ValueFactoryImpl.getInstance().createURI("urn:b"),
						 ValueFactoryImpl.getInstance().createURI("urn:c"));
		
		return aUpdateGraph;
	}

	@Test
	public void testAppend() {

		try {
			// create the context we're going to append to
			mStore.add(new GraphImpl(), TEST_CONTEXT);

			Graph aGraph = generateGraph();

			mStore.append(aGraph, TEST_CONTEXT);

			assertContains(aGraph);

			mStore.delete(aGraph, TEST_CONTEXT);

			assertNotContains(aGraph);

			StringWriter aWriter = new StringWriter();

			OpenRdfIO.writeGraph(aGraph, aWriter, RDFFormat.TURTLE);

			String aStr = aWriter.toString();

			mStore.append(aStr, RDFFormat.TURTLE, TEST_CONTEXT);

			assertContains(aGraph);

			mStore.delete(aGraph, TEST_CONTEXT);

			assertNotContains(aGraph);

			mStore.append(new ByteArrayInputStream(aStr.getBytes(Charsets.UTF_8)), RDFFormat.TURTLE, TEST_CONTEXT);

			assertContains(aGraph);

			mStore.delete(aGraph, TEST_CONTEXT);

			assertNotContains(aGraph);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testAdd() {
		try {
			// create the context we're going to append to
			mStore.add(TEST_DATA, TEST_CONTEXT);

			assertContains(TEST_DATA);

			mStore.delete(TEST_CONTEXT);

			assertNotContains(TEST_DATA);

			StringWriter aWriter = new StringWriter();

			OpenRdfIO.writeGraph(TEST_DATA, aWriter, RDFFormat.TURTLE);

			String aStr = aWriter.toString();

			mStore.add(aStr, RDFFormat.TURTLE, TEST_CONTEXT);

			assertContains(TEST_DATA);

			mStore.delete(TEST_CONTEXT);

			assertNotContains(TEST_DATA);

			mStore.add(new ByteArrayInputStream(aStr.getBytes(Charsets.UTF_8)), RDFFormat.TURTLE, TEST_CONTEXT);

			assertContains(TEST_DATA);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testDelete() {
		try {
			mStore.add(TEST_DATA, TEST_CONTEXT);

			assertContains(TEST_DATA);

			mStore.delete(TEST_DATA, TEST_CONTEXT);

			assertNotContains(TEST_DATA);

			mStore.append(TEST_DATA, TEST_CONTEXT);

			assertContains(TEST_DATA);

			mStore.delete(TEST_CONTEXT);

			assertNotContains(TEST_DATA);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testConnection() {
		try {
			// TODO: get url from constant
			Store aStore = StoreFactory.create(new URL("http://vx.int.clarkparsia.com:8000/"));

			// TODO: verify you cant do anything until connected

			aStore.connect();

			// TODO: do something to verify that we can do something now that we're connected

			aStore.disconnect();

			// TODO: verify you can't do anything after disconnect
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testTransactions() {
		Statement aStmt = new StatementImpl(ValueFactoryImpl.getInstance().createURI("urn:test:a"),
											ValueFactoryImpl.getInstance().createURI("urn:test:b"),
											ValueFactoryImpl.getInstance().createURI("urn:test:c"));

		try {
			FourStoreSailRepository aRepo = new FourStoreSailRepository(new URL(STORE_URL));
			aRepo.initialize();

			RepositoryConnection aConn = aRepo.getConnection();

			aConn.setAutoCommit(false);

			assertFalse(aConn.hasStatement(aStmt, true, TEST_CONTEXT));

			aConn.add(aStmt, TEST_CONTEXT);

			assertTrue(aConn.hasStatement(aStmt, true, TEST_CONTEXT));

			aConn.rollback();

			// make sure rollback worked.
			assertFalse(aConn.hasStatement(aStmt, true, TEST_CONTEXT));

			aConn.add(aStmt, TEST_CONTEXT);

			assertTrue(aConn.hasStatement(aStmt, true, TEST_CONTEXT));

			aConn.commit();

			assertTrue(aConn.hasStatement(aStmt, true, TEST_CONTEXT));

			aConn.remove(aStmt, TEST_CONTEXT);

			assertFalse(aConn.hasStatement(aStmt, true, TEST_CONTEXT));

			aConn.rollback();

			assertTrue(aConn.hasStatement(aStmt, true, TEST_CONTEXT));

			aConn.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetStatements() {
		try {
			String aConstruct = "construct {<http://nasa.dataincubator.org/spacecraft/1957-001A> ?p ?o} where {<http://nasa.dataincubator.org/spacecraft/1957-001A> ?p ?o}";

			Graph aConstructGraph = mStore.constructQuery(aConstruct);

			Graph aGetStmtGraph = new GraphImpl();

			for (Statement aStmt : iterable(mStore.getStatements(ValueFactoryImpl.getInstance().createURI("http://nasa.dataincubator.org/spacecraft/1957-001A"), null, null))) {
				aGetStmtGraph.add(aStmt);
			}

			assertTrue(ModelUtil.equals(aConstructGraph, aGetStmtGraph));
		}
		catch (StoreException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testSailCRUD() {
		try {
			Repository aSail = new FourStoreSailRepository(new FourStoreSail(new URL("http://vx.int.clarkparsia.com:8000/")));

			aSail.initialize();

			RepositoryConnection aConn = aSail.getConnection();

			Graph aGraph = generateGraph();

			aConn.add(aGraph, TEST_CONTEXT);

			assertContains(aConn, aGraph, true);

			aConn.remove(aGraph, TEST_CONTEXT);

			assertContains(aConn, aGraph, false);

			for (Statement aStatement : aGraph) {
				aConn.add(aStatement, TEST_CONTEXT);

				assertTrue(aConn.hasStatement(aStatement, true, TEST_CONTEXT));
			}

			for (Statement aStatement : aGraph) {
				aConn.remove(aStatement, TEST_CONTEXT);

				assertFalse(aConn.hasStatement(aStatement, true, TEST_CONTEXT));
			}

			for (Statement aStatement : aGraph) {
				aConn.add(aStatement.getSubject(), aStatement.getPredicate(), aStatement.getObject(), TEST_CONTEXT);

				assertTrue(aConn.hasStatement(aStatement, true, TEST_CONTEXT));
			}

			for (Statement aStatement : aGraph) {
				aConn.remove(aStatement.getSubject(), aStatement.getPredicate(), aStatement.getObject(), TEST_CONTEXT);

				assertFalse(aConn.hasStatement(aStatement, true, TEST_CONTEXT));
			}

			RDFFormat aFormat = RDFFormat.NTRIPLES;

			StringWriter aData = new StringWriter();
			OpenRdfIO.writeGraph(aGraph, aData, RDFFormat.NTRIPLES);

			aConn.add(new ByteArrayInputStream(aData.toString().getBytes(Charsets.UTF_8)), null, aFormat, TEST_CONTEXT);

			assertContains(aConn, aGraph, true);

			for (Statement aStatement : aGraph) {
				aConn.remove(aStatement.getSubject(), aStatement.getPredicate(), aStatement.getObject(), TEST_CONTEXT);
			}

			assertContains(aConn, aGraph, false);

			aConn.add(new StringReader(aData.toString()), null, aFormat, TEST_CONTEXT);

			assertContains(aConn, aGraph, true);

			aConn.remove(aGraph, TEST_CONTEXT);

			assertContains(aConn, aGraph, false);

			File aTmpFile = File.createTempFile("test", ".nt");
			aTmpFile.deleteOnExit();

			Files.write(aData.toString(), aTmpFile, Charsets.UTF_8);

			Files.toString(aTmpFile, Charsets.UTF_8);

			aConn.add(aTmpFile, null, aFormat, TEST_CONTEXT);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	private void assertContains(RepositoryConnection theConn, Graph theGraph, boolean theExpected) throws RepositoryException {
		for (Statement aStatement : theGraph) {
			assertEquals(theConn.hasStatement(aStatement, true, TEST_CONTEXT), theExpected);
		}
	}

	@Test(expected=StoreException.class)
	public void testUnboundHasStatement() throws StoreException {
		mStore.hasStatement(null, null, null);
	}

	@Test(expected=StoreException.class)
	public void testUnboundGetStatements() throws StoreException {
		mStore.getStatements(null, null, null);
	}

	@Test
	public void testSailQuery() {
		try {
			// TODO: get URL from a const
			Repository aSail = new FourStoreSailRepository(new URL("http://vx.int.clarkparsia.com:8000/"));

			aSail.initialize();

			Repository aSesame = new SailRepository(new MemoryStore());

			aSesame.initialize();

			RepositoryConnection aSesameConn = aSesame.getConnection();
			RepositoryConnection aFourStoreConn = aSail.getConnection();

			aSesameConn.add(NASA_DATA, TEST_CONTEXT);

			aFourStoreConn.add(NASA_DATA, TEST_CONTEXT);

			String[] aSelectQueries = new String[] {
					"select distinct ?result where { ?uri <http://purl.org/net/schemas/space/mass> ?result }",
					"select distinct ?result where { ?result <http://purl.org/net/schemas/space/agency> \"United States\" }",
					"select distinct ?result where { ?result <http://purl.org/net/schemas/space/agency> \"U.S.S.R\". ?result <http://purl.org/net/schemas/space/alternateName> \"00001\" }"
			};

			String[] aGraphQueries = new String[] {
					"construct {<http://nasa.dataincubator.org/spacecraft/1957-001A> ?p ?o} where {<http://nasa.dataincubator.org/spacecraft/1957-001A> ?p ?o}",
					"construct { ?result <http://purl.org/net/schemas/space/agency> \"USA\" } where { ?result <http://purl.org/net/schemas/space/agency> \"United States\" }",
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\nconstruct {?s rdf:type rdf:Resource} where {?s rdf:type ?o}",
			};

			for (String aQuery : aSelectQueries) {
				TupleQueryResult aSesameResult = aSesameConn.prepareTupleQuery(QueryLanguage.SPARQL, aQuery, "http://example.org").evaluate();
				TupleQueryResult aFourStoreResult = aFourStoreConn.prepareTupleQuery(QueryLanguage.SPARQL, aQuery, "http://example.org").evaluate();

				assertTrue(QueryResultUtil.equals(aSesameResult, aFourStoreResult));
			}

			for (String aQuery : aGraphQueries) {
				GraphQueryResult aSesameResult = aSesameConn.prepareGraphQuery(QueryLanguage.SPARQL, aQuery, "http://example.org").evaluate();
				GraphQueryResult aFourStoreResult = aFourStoreConn.prepareGraphQuery(QueryLanguage.SPARQL, aQuery, "http://example.org").evaluate();

				Graph aSesameResultGraph = new GraphImpl();
				Graph aFourStoreResultGraph = new GraphImpl();

				while (aSesameResult.hasNext()) {
					aSesameResultGraph.add(aSesameResult.next());
				}

				while (aFourStoreResult.hasNext()) {
					aFourStoreResultGraph.add(aFourStoreResult.next());
				}


//				assertTrue(QueryResultUtil.equals(aSesameResult, aFourStoreResult));
				assertTrue(ModelUtil.equals(aSesameResultGraph, aFourStoreResultGraph));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	private void assertNotContains(Graph theGraph) throws StoreException {
		for (Statement aStmt : theGraph) {
			assertFalse(mStore.hasStatement(aStmt));
		}
	}

	private void assertContains(Graph theGraph) throws StoreException {
		for (Statement aStmt : theGraph) {
			assertTrue(mStore.hasStatement(aStmt));
		}
	}

	@After
	public void cleanupTest() {
		// all tests are done in the test context, so lets delete it after each test so they all have a clean slate to
		// test from
		try {
			mStore.delete(TEST_CONTEXT);
		}
		catch (StoreException e) {
			fail(e.getMessage());
		}
	}

	public static void main(String[] args) throws Exception {
		StoreImpl aStore = (StoreImpl) StoreFactory.create(new URL("http://vx.int.clarkparsia.com:8000/"));

		System.err.println(aStore.size());
		
		aStore.delete(TEST_CONTEXT);
//		aStore.delete(ValueFactoryImpl.getInstance().createURI("file:///home/mgrove/nasa.nt"));
//		aStore.delete(ValueFactoryImpl.getInstance().createURI("default:"));

		TupleQueryResult aResult = aStore.query("select  distinct ?g where { graph ?g {?s ?p ?o}}");
		while (aResult.hasNext()) {
			System.err.println(aResult.next().getValue("g"));
		}

		System.err.println(aStore.size());

//		aStore.connect();
//
//		System.err.println(aStore.status());
//
//		for (BindingSet aBinding : OpenRdfUtil.iterable(aStore.query("select distinct ?t where {?s rdf:type ?t.}"))) {
//			System.err.println(aBinding.getValue("t"));
//		}
//
//		aStore.update("PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
//					 "INSERT { <http://example/egbook3> dc:title  \"This is an example title\". <http://example/egbook3> dc:title  \"This is another example title\" }.");
//
//		for (BindingSet aBinding : OpenRdfUtil.iterable(aStore.query("select distinct ?p ?o where {<http://example/egbook3> ?p ?o.}"))) {
//			System.err.println(aBinding.getValue("t"));
//		}
//
//		Graph g = aStore.constructQuery("construct {<http://example/egbook3> ?p ?o.} where {<http://example/egbook3> ?p ?o.}");
//System.err.println("Graph size: " + g.size());
//		for (Statement s : g) {
//			System.err.println(s);
//		}
//
//		System.err.println(aStore.ask("PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
//					 "ask { <http://example/egbook3> dc:title  \"This is an example title\" }"));
//
//		aStore.delete(g, null);
//
//		System.err.println(aStore.ask("PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
//					 "ask { <http://example/egbook3> dc:title  \"This is an example title\" }"));
//
//		aStore.disconnect();
	}
}
