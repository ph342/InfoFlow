package semanticanalysis.wellformedness;

import java.util.Set;

import semanticanalysis.StaticAnalysisException;

/**
 * Exception thrown to report an error during well-formedness checking.
 */
public class WellFormednessException extends StaticAnalysisException {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of <code>WellFormedednessException</code> without
	 * detail message.
	 */
	public WellFormednessException() {
	}

	/**
	 * Constructs an instance of <code>TypeCheckingException</code> with the
	 * specified detail message.
	 * 
	 * @param msg the detail message.
	 */
	public WellFormednessException(String msg) {
		super(msg);
	}

	public WellFormednessException(String msg, Set<String> tags) {
		super(msg, tags);
	}

}
