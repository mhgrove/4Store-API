package com.clarkparsia.fourstore.cli;

import uk.co.flamingpenguin.jewel.cli.Option;
import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Unparsed;

/**
 * <p></p>
 *
 * @author Michael Grove
 */
@CommandLineInterface(application="help")
public interface HelpCmd extends Help {
	@Unparsed
	public String getSubCommand();
	public boolean isSubCommand();
}
