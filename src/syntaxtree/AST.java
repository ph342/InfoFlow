package syntaxtree;

import java.util.HashSet;
import java.util.Set;

/**
 * Parent class for all abstract syntax. Provides the ability to tag ASTs with
 * additional information.
 *
 * @author seb
 */
public abstract class AST {

	private static Set<String> globalTags;

	private Set<String> tags;

	/**
	 * Initialise the tag list with the global tags.
	 *
	 * @see AST.globalTag(String)
	 */
	public AST() {
		if (globalTags != null) {
			tags = new HashSet<>();
			tags.addAll(globalTags);
		}
	}

	/**
	 * Add a tag.
	 *
	 * @param t the tag
	 */
	public void tag(String t) {
		if (tags == null)
			tags = new HashSet<>();
		tags.add(t);
	}

	/**
	 * Add a tag describing a position in a source-code file (line and column) to
	 * aid error reporting.
	 *
	 * @param line   the line number
	 * @param column the column number
	 */
	public void tag(int line, int column) {
		// the column numbers seem not so helpful in practice
		// tag("line " + line + ", column " + column);
		this.tag("line " + line);
	}

	/**
	 * Add a list of tags.
	 *
	 * @param ts the tags
	 */
	public void tag(HashSet<String> ts) {
		for (String t : ts)
			this.tag(t);
	}

	/**
	 * Add a global tag. A global tag is inherited by all AST objects when they are
	 * created.
	 *
	 * @param t the tag
	 */
	public static void globalTag(String t) {
		if (globalTags == null)
			globalTags = new HashSet<>();
		globalTags.add(t);
	}

	/**
	 * Reset the global tags to empty. Note: this does not remove global tags from
	 * AST objects which have already been created.
	 */
	public static void clearGlobalTags() {
		globalTags = null;
	}

	/**
	 * The tags for this AST object. If no tags have been added, this list will be
	 * empty.
	 *
	 * @return the tags (not null)
	 */
	public HashSet<String> getTags() {
		if (tags == null)
			return new HashSet<String>();
		return (HashSet<String>) tags;
	}
}
