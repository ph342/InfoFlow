package semanticanalysis;

import java.util.Iterator;
import java.util.Set;

/**
 * Exception thrown during static analysis (typically during symbol table
 * construction and type checking).
 */
public class StaticAnalysisException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of <code>StaticAnalysisException</code> without detail
	 * message.
	 */
	public StaticAnalysisException() {
	}

	/**
	 * Constructs an instance of <code>StaticAnalysisException</code> with the
	 * specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public StaticAnalysisException(String msg) {
		super(msg);
	}

	public StaticAnalysisException(String msg, Set<String> tags) {
		super(msg + tagString(tags));
	}

	public static String tagString(Set<String> tags) {
		if ((tags == null) || (tags.size() == 0))
			return "";

		Iterator<String> it = tags.iterator();
		String tagString = " (" + it.next();

		for (Iterator<String> i = it; i.hasNext();)
			tagString += "; " + i.next();

		return tagString += ")";
	}
}
