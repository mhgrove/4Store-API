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

package com.clarkparsia.fourstore.cli;

import uk.co.flamingpenguin.jewel.cli.CliFactory;
import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;

import java.util.Arrays;
import java.util.Map;
import java.util.LinkedHashMap;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.ConnectException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;

import com.clarkparsia.fourstore.impl.StoreFactory;
import com.clarkparsia.fourstore.api.Store;
import com.clarkparsia.fourstore.api.QueryException;
import com.clarkparsia.fourstore.api.StoreException;
import com.clarkparsia.openrdf.OpenRdfIO;
import com.google.common.base.Predicate;
import com.google.common.io.ByteStreams;
import org.openrdf.model.Graph;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.BindingSet;

/**
 * <p>4Store HTTP server command line client</p>
 *
 * @author Michael Grove
 */
public class Console {
	private static Map<Class<?>, Predicate<?>> OPTION_TO_CMD = new LinkedHashMap<Class<?>, Predicate<?>>();

	public static void main(String[] args) throws Exception {
		OPTION_TO_CMD.put(HelpCmd.class, new HelpCommand());
		OPTION_TO_CMD.put(Query.class, new QueryCommand());
		OPTION_TO_CMD.put(Remove.class, new RemoveCommand());
		OPTION_TO_CMD.put(Add.class, new AddCommand());
		OPTION_TO_CMD.put(Size.class, new SizeCommand());

//      String[] aFakeArgs = new String[] { "query", "--help" };
//		String[] aFakeArgs = new String[] { "help", "query" };
//		String[] aFakeArgs = new String[] { "query", "--url", "http://vx.int.clarkparsia.com:8000" , "-q", "select ?s where { ?s ?p ?o } limit 5"};
//		String[] aFakeArgs = new String[] { "size", "--url", "http://vx.int.clarkparsia.com:8000" };
//
//		args = aFakeArgs;

		try {
			if (args.length == 0) {
				System.err.println("Type 'help' for usage information.");
			}
			else {
				Class<?> aClass = cliForCommand(args[0]);
				Predicate aCmd = OPTION_TO_CMD.get(aClass);

				aCmd.apply(CliFactory.createCli(aClass).parseArguments(Arrays.copyOfRange(args, 1, args.length)));

			}
		}
		catch (ArgumentValidationException e) {
			System.out.println(e.getMessage());
		}
	}

	private static <T> Class<T> cliForCommand(String theCmd) {
		for (Class<?> aClass : OPTION_TO_CMD.keySet()) {
			if (aClass.isAnnotationPresent(CommandLineInterface.class)) {
				CommandLineInterface aInt = aClass.getAnnotation(CommandLineInterface.class);

				if (aInt.application().equals(theCmd)) {
					return (Class<T>) aClass;
				}
			}
		}

		return null;
	}

	private static String commandList() {
		StringBuffer aBuffer = new StringBuffer();
		aBuffer.append("Available commands:\n");

		for (Class<?> aClass : OPTION_TO_CMD.keySet()) {
			if (aClass.isAnnotationPresent(CommandLineInterface.class)) {
				CommandLineInterface aInt = aClass.getAnnotation(CommandLineInterface.class);
				aBuffer.append("\t").append(aInt.application()).append("\n");
			}
		}

		return aBuffer.toString();
	}

	private static class RemoveCommand extends StoreCommand<Remove> {

		public boolean apply(final Remove theCmd) {
			Store aStore = getStore(theCmd);

			RDFFormat aFormat = RDFFormat.RDFXML;
			if (theCmd.isRDFFormat()) {
				aFormat = RDFFormat.valueOf(theCmd.getRDFFormat());
			}

			URI aGraphURI = null;
			if (theCmd.isGraph()) {
				aGraphURI = ValueFactoryImpl.getInstance().createURI(theCmd.getGraph());
			}

			try {
				long aSize = aStore.size();

				for (File aFile : theCmd.getFiles()) {
					try {
						Graph aGraph = OpenRdfIO.readGraph(new FileInputStream(aFile), aFormat);
						aStore.delete(aGraph, aGraphURI);
					}
					catch (FileNotFoundException e) {
						System.out.println("File could not be found: " + aFile);
						return true;
					}
					catch (IOException e) {
						System.out.println("There was an error reading the file: " + aFile);
						return true;
					}
					catch (RDFParseException e) {
						System.out.println("There was an error while parsing the file: " + aFile);
						return true;
					}
				}

				long aNewSize = aStore.size();

				System.out.println((aSize - aNewSize ) + " triples successfully removed from the database");
			}
			catch (StoreException e) {
				System.out.println("There was an error adding a data file: " + e.getMessage());
			}

			return true;
		}
	}

	private static class AddCommand extends StoreCommand<Add> {

		public boolean apply(final Add theCmd) {
			Store aStore = getStore(theCmd);

			RDFFormat aFormat = RDFFormat.RDFXML;
			if (theCmd.isRDFFormat()) {
				aFormat = RDFFormat.valueOf(theCmd.getRDFFormat());
			}

			URI aGraphURI = null;
			if (theCmd.isGraph()) {
				aGraphURI = ValueFactoryImpl.getInstance().createURI(theCmd.getGraph());
			}

			try {
				long aSize = aStore.size();

				for (File aFile : theCmd.getFiles()) {
					try {
						aStore.add(new FileInputStream(aFile), aFormat, aGraphURI);
					}
					catch (FileNotFoundException e) {
						System.out.println("File could not be found: " + aFile);
						return true;
					}
				}

				long aNewSize = aStore.size();

				System.out.println((aNewSize - aSize ) + " triples successfully added to database");
			}
			catch (StoreException e) {
				System.out.println("There was an error adding a data file: " + e.getMessage());
			}

			return true;
		}
	}

	private static class QueryCommand extends StoreCommand<Query> {

		/**
		 * @inheritDoc
		 */
		public boolean apply(final Query theCmd) {
			try {
				Store aStore = getStore(theCmd);

				String aQuery = theCmd.getQuery();
				if (theCmd.isQuery()) {
					aQuery = theCmd.getQuery();
				}
				else if (theCmd.isFile() && theCmd.getFile().size() > 0) {
					try {
						aQuery = new String(ByteStreams.toByteArray(new FileInputStream(theCmd.getFile().get(0))));
					}
					catch (IOException e) {
						System.out.println("Unable to read query string from file: " + e.getMessage());
						return true;
					}
				}

				if (aQuery == null) {
					System.out.println("No query string specified.");

					return true;
				}

				aQuery = aQuery.trim();

				if (aQuery.toLowerCase().startsWith("select")) {
					TupleQueryResult aResult = aStore.query(aQuery);

					StringBuffer aBuffer = new StringBuffer();
					for (int i = 0; i < aResult.getBindingNames().size(); i++) {
						aBuffer.append("%-50s ");
					}

					System.out.printf(aBuffer.toString(), aResult.getBindingNames());

					while (aResult.hasNext()) {
						BindingSet aSet = aResult.next();
						String[] aLine = new String[aResult.getBindingNames().size()];

						int aIndex = 0;
						for (String aName : aResult.getBindingNames()) {
							aLine[aIndex++] = aSet.hasBinding(aName) ? aSet.getValue(aName).stringValue() : "";
						}

						System.out.printf(aBuffer.toString(), aLine);
					}
				}
				else {
					Graph aGraph = aStore.constructQuery(aQuery);
					RDFFormat aFormat = RDFFormat.NTRIPLES;

					if (theCmd.isRDFFormat()) {
						aFormat = RDFFormat.valueOf(theCmd.getRDFFormat());
					}

					try {
						OpenRdfIO.writeGraph(aGraph, new PrintWriter(System.out), aFormat);
					}
					catch (IOException e) {
						System.out.println("There was an error writing the results.");
					}
				}

				aStore.disconnect();
			}
			catch (ConnectException e) {
				// ignore disconnect error
			}
			catch (QueryEvaluationException e) {
				System.out.println("There was an error while executing the query: " + e.getMessage());
			}
			catch (QueryException e) {
				System.out.println("There was an error while executing the query: " + e.getMessage());
			}

			return true;
		}
	}

	private abstract static class StoreCommand<T extends StoreCmd> implements Predicate<T> {
		Store getStore(final T theCmd) {
			try {
				Store aStore = StoreFactory.create(new URL(theCmd.getURL()));

				aStore.connect();

				return aStore;
			}
			catch (MalformedURLException e) {
				System.out.println(theCmd.getURL() + " is not a valid URL.");
				System.exit(0);
			}
			catch (ConnectException e) {
				System.out.println("Count not connect to the 4Store instance");
				System.exit(0);
			}

			return null;
		}
	}

	private static class SizeCommand extends StoreCommand<Size> {

		public boolean apply(final Size theCmd) {
			try {
				Store aStore = getStore(theCmd);

				System.out.println(aStore.size());

				aStore.disconnect();
			}
			catch (StoreException e) {
				System.out.println("There was an error while querying for the size: " + e.getMessage());
			}
			catch (ConnectException e) {
				// ignore disconnect error
			}
			
			return true;
		}
	}

	private static class HelpCommand implements Predicate<HelpCmd> {

		/**
		 * @inheritDoc
		 */
		public boolean apply(HelpCmd theCmd) {
			String aSubCmd = theCmd.getSubCommand();

			if (aSubCmd == null || aSubCmd.length() == 0) {
				System.out.println("4Store HTTP server command line client\n");
				System.out.println("Type 'help <cmd>' or '<cmd> -h/--help' to print the usage information for a specific command\n");
				System.out.println(commandList());
				System.out.println("For more information on this library, visit the home page at http://github.com/clarkparsia/4Store-API");
				System.out.println("For information on 4store, please visit http://4store.org");
			}
			else {
				System.out.println(CliFactory.createCli(cliForCommand(aSubCmd)).getHelpMessage());
			}
			
			return true;
		}
	}
}
