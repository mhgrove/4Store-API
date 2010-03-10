package com.clarkparsia.fourstore.cli;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Unparsed;
import uk.co.flamingpenguin.jewel.cli.Option;

import java.util.List;
import java.io.File;

/**
 * <p></p>
 *
 * @author Michael Grove
 */
@CommandLineInterface(application="query")
public interface Query extends StoreCmd {
	@Option(shortName="q", longName="query", description="The SPARQL query string to execute")
	public String getQuery();
	public boolean isQuery();

	@Option(shortName="f", longName="format", defaultValue="NTRIPLES", description="RDF Format [NTRIPLES, RDFXML, TURTLE]")
	public String getRDFFormat();
	public boolean isRDFFormat();

	@Option(shortName="l", longName="soft-limit", description="Sets the soft limit for queries sent to the database")
	public int getSoftLimit();
	public boolean isSoftLimit();

	@Unparsed
	public List<File> getFile();
	public boolean isFile();
}
