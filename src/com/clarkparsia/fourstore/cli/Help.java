package com.clarkparsia.fourstore.cli;

import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * <p></p>
 *
 * @author Michael Grove
 */
public interface Help {
	@Option(shortName="h", longName="help", description="Display usage information", helpRequest = true)
	public boolean getHelp();
}
