package com.clarkparsia.fourstore.cli;

import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * <p></p>
 *
 * @author Michael Grove
 */
public interface StoreCmd extends Help {
	@Option(shortName="u", longName="url", description="URL to the remote 4Store instance")
	public String getURL();
}
