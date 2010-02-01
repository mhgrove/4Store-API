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

package com.clarkparsia.fourstore.test;

import com.clarkparsia.utils.io.IOUtil;

import com.clarkparsia.fourstore.api.QueryException;

import com.clarkparsia.fourstore.api.Store;
import com.clarkparsia.fourstore.impl.StoreFactory;

import com.clarkparsia.openrdf.OpenRdfIO;
import com.clarkparsia.openrdf.ExtGraph;
import com.clarkparsia.openrdf.OpenRdfUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import org.openrdf.rio.RDFFormat;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.URI;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;

/**
 * <p>Simple set of tests for the API</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
public class Test {
	public static void main(String[] args) throws Exception {
		goodTest();

//		Store aStore = StoreFactory.create(new URL("http://localhost:8000/"));
//
//		aStore.connect();
//
//		System.err.println("Status: " + aStore.status());
//
//		System.err.println(aStore.size());
//
//		String aQuery = "select distinct ?p where { ?s ?p ?o }";
//
//		// this is actually not an easy query on a big result set since the spo pulls in the entire db
//		aStore.setSoftLimit(1000);
//
//		if (aStore.getSoftLimit() != 1000) {
//			throw new Exception("setting soft limit did not work");
//		}
//
//		ResultSet aResults = aStore.query(aQuery);
//
//		System.err.println("There are " + aResults.size() + " results.");
//
//		for (Binding aBinding : aResults) {
//			System.err.println("p = " + aBinding.get("p"));
//		}
//
//		aQuery = "select ?s ?aLabel where {?s rdf:type ?type. ?s rdfs:label ?aLabel.}";
//
//		aResults = aStore.query(aQuery);
//
//		System.err.println(new TabbedResultSetFormatter().format(aResults));
	}

	private static void goodTest() throws Exception {
		// TODO: refactor into junit tests

		Store aStore = StoreFactory.create(new URL("http://localhost:8000/"));

		aStore.connect();

		System.err.println(aStore.status());

		System.err.println(aStore.size());

		// try adding some data
		aStore.add(getDataToAdd(), RDFFormat.TURTLE, null);

		verifyAdd(aStore, null, getDataToAdd());

		aStore.add(getOtherDataToAdd(), RDFFormat.TURTLE, getGraphURI());

		verifyAdd(aStore, getGraphURI(), getOtherDataToAdd());

		if (aStore.size() != getExpectedSize()) {
			throw new Exception("Adds didn't work? " + aStore.size() + " " + getExpectedSize());
		}

		// TODO: try adding some invalid data

		// try removing some data

		// this data does not exist in this graph
//		aStore.delete(getDataToAdd(), Format.Turtle, getGraphURI());
		aStore.delete(getGraphURI());

		// so lets make sure the old data is still there...
		verifyAdd(aStore, getGraphURI(), getOtherDataToAdd());

		// and that the data "removed" is not there
		verifyDelete(aStore, getGraphURI(), getDataToAdd());

		// k, now, lets try and delete a blob of data
//		aStore.delete(getDataToAdd(), Format.Turtle, null);
		aStore.delete(null);

		// make sure its gone
		verifyDelete(aStore, null, getDataToAdd());

		// lets try deleting an entire named graph
		aStore.delete(getGraphURI());

		// and make sure its gone...
		verifyDelete(aStore, getGraphURI(), getOtherDataToAdd());

		if (aStore.size() != 0) {
			throw new Exception("Deletes didn't work?");
		}

		// TODO: try removing invalid data

		// try updating some data
		aStore.add(getDataToAdd(), RDFFormat.TURTLE, getGraphURI());
		verifyAdd(aStore, getGraphURI(), getDataToAdd());

		// TODO: i assume adding to an existing named graph overwrites?
		aStore.add(getOtherDataToAdd(), RDFFormat.TURTLE, getGraphURI());

		verifyAdd(aStore, getGraphURI(), getOtherDataToAdd());
		verifyDelete(aStore, getGraphURI(), getDataToAdd());

		aStore.append(getDataToAdd(), RDFFormat.TURTLE, getGraphURI());
		verifyAdd(aStore, getGraphURI(), getDataToAdd());
		verifyAdd(aStore, getGraphURI(), getOtherDataToAdd());

		String aQuery = "select ?p where { ?s ?p ?o }";

		// this is actually not an easy query on a big result set since the spo pulls in the entire db
		aStore.setSoftLimit(1000);

		if (aStore.getSoftLimit() != 1000) {
			throw new Exception("setting soft limit did not work");
		}

		TupleQueryResult aResults = aStore.query(aQuery);


		for (BindingSet aBinding : OpenRdfUtil.iterable(aResults)) {
			System.err.println("p = " + aBinding.getValue("p"));
		}

		aQuery = "select ?uri ?aLabel where {?s rdf:type ?type. ?s rdfs:label ?aLabel.}";

		aResults = aStore.query(aQuery);

		System.err.println(format(aResults));

		aQuery = "This is not a valid query";

		try {
			aStore.query(aQuery);
		}
		catch (QueryException ex) {
			System.err.println("we expected this...");
			ex.printStackTrace();
		}

		// TODO: need some tests to make sure we actually got the correct query results.
		// TODO: need tests for graph queries, describe & ask.

		// TODO: make sure we get a notice that the soft limit was hit?

		aStore.disconnect();

		// TODO: make sure we can't do operations after disconnection
	}

	private static String getDataToAdd() throws IOException {
		return IOUtil.getFileAsString("test" + File.separator + "data1.ttl");
	}

	private static String getOtherDataToAdd() throws IOException {
		return IOUtil.getFileAsString("test" + File.separator + "data2.ttl");
	}

	private static URI getGraphURI() {
		return ValueFactoryImpl.getInstance().createURI("http://example.org/graph");
	}

	private static void verifyAdd(Store theStore, URI theGraph, String theData) throws Exception {
		ExtGraph aGraph = OpenRdfIO.readGraph(new StringReader(theData), RDFFormat.TURTLE);

		for (org.openrdf.model.Statement aStmt : aGraph) {
			if (aStmt.getSubject() instanceof org.openrdf.model.BNode || aStmt.getObject() instanceof org.openrdf.model.BNode) {
				// skip it, we can't query for bnodes in sparql like you can in serql.  if everything else in the
				// operation is verified, this is probably in there too
				continue;
			}

			if (!theStore.hasStatement(aStmt)) {
				throw new Exception("Add failed? missing: " + aStmt);
			}
		}
	}

	private static void verifyDelete(Store theStore, URI theGraph, String theData) throws Exception {
		ExtGraph aGraph = OpenRdfIO.readGraph(new StringReader(theData), RDFFormat.TURTLE);

		for (org.openrdf.model.Statement aStmt : aGraph) {
			if (aStmt.getSubject() instanceof org.openrdf.model.BNode || aStmt.getObject() instanceof org.openrdf.model.BNode) {
				// skip it, we can't query for bnodes in sparql like you can in serql.  if everything else in the
				// operation is verified, this is probably in there too
				continue;
			}
			
			if (theStore.hasStatement(aStmt)) {
				throw new Exception("delete failed?");
			}
		}
	}

	private static int getExpectedSize() throws Exception {
		// yes, this is not very efficient...
		return OpenRdfIO.readGraph(new FileInputStream("test" + File.separator + "data1.ttl"), RDFFormat.TURTLE).size() +
			   OpenRdfIO.readGraph(new FileInputStream("test" + File.separator + "data2.ttl"), RDFFormat.TURTLE).size();

	}

	private static String format(final TupleQueryResult theResultSet) {
		StringBuffer aBuffer = new StringBuffer();

		try {
			if (!theResultSet.hasNext()) {
				aBuffer.append("*** No Results ***");
			}
			else {
				// TODO: use some of the text formatting stuff to get the columns to properly align

				boolean aNeedsHeader = true;
				for (BindingSet aBinding : OpenRdfUtil.iterable(theResultSet)) {
					if (aNeedsHeader) {
						for (String aVar : aBinding.getBindingNames()) {
							aBuffer.append(aVar);
							aBuffer.append("\t");
						}
						aBuffer.append("\n---\n");

						aNeedsHeader = false;
					}

					for (String aVar : aBinding.getBindingNames()) {
						aBuffer.append(aBinding.getValue(aVar));
						aBuffer.append("\t");
					}
					aBuffer.append("\n");
				}
			}
		}
		catch (QueryEvaluationException e) {
			throw new RuntimeException(e);
		}

		return aBuffer.toString();
	}
}
