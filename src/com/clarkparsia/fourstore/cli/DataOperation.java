package com.clarkparsia.fourstore.cli;

import uk.co.flamingpenguin.jewel.cli.Option;
import uk.co.flamingpenguin.jewel.cli.Unparsed;

import java.io.File;
import java.util.List;

/**
 * <p></p>
 *
 * @author Michael Grove
 */
public interface DataOperation extends StoreCmd {
	@Option(shortName="g", longName="graph-uri", description="The named graph data will be added/removed from")
	public String getGraph();
	public boolean isGraph();

	@Option(shortName="f", longName="format", defaultValue="RDF/XML", description="RDF Format [NTRIPLES, RDF/XML, TURTLE]")
	public String getRDFFormat();
	public boolean isRDFFormat();

	@Unparsed
	public List<File> getFiles();
}
