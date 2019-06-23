package cloptions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Simple utility for extracting command-line options from a list of arg
 * strings.
 * 
 * @author seb
 */
public class CLOptions {

	/**
	 * Scan a list of command-line arguments and extract any recognised option
	 * flags.
	 * 
	 * @param args  the list of command-line arguments. All recognised flags will be
	 *              removed from the list, so the list must support the remove
	 *              operation (note that this means that the list cannot be created
	 *              simply using Arrays.asList since this creates a list with
	 *              immutable structure).
	 * @param flags the array of option flags to look for. The flags should be
	 *              specified <b>without</b> leading hyphens; a string in the arg
	 *              list will be matched if it is one of the flag strings with
	 *              either a single "-" or double "--" added to the front. For
	 *              example, both "-debug" and "--debug" will match the flag
	 *              "debug".
	 * @return the set of recognised flags <b>without</b> their leading hyphens.
	 */
	public static Set<String> getFlags(List<String> args, String... flags) {
		Set<String> options = new HashSet<>();
		int first = 0;
		while (args.size() > first) {
			boolean match = false;
			for (String flag : flags) {
				String arg = args.get(first);
				if (("-" + flag).equals(arg) || ("--" + flag).equals(arg)) {
					options.add(flag);
					args.remove(first);
					match = true;
					break;
				}
			}
			if (!match)
				++first;
		}
		return options;
	}

	/**
	 * Scan a list of command-line arguments without flags and extract assignments.
	 * 
	 * @param args The list of command-line arguments WITHOUT the flags.
	 * @return Assignments in the form x=5
	 */
	public static String[] getAssignments(List<String> args) {
		if (args.size() == 0)
			return new String[0];

		String[] ret = new String[args.size() - 1];

		for (int i = 0; i < ret.length; ++i) {
			ret[i] = args.get(i + 1);
		}

		return ret;
	}
}
