package semanticanalysis.wellformedness;

import java.util.List;

import semanticanalysis.StaticAnalysisException;

/**
 * Exception thrown to report an error during well-formedness checking.
 */
public class WellFormednessException extends StaticAnalysisException {

    /**
     * Creates a new instance of <code>WellFormedednessException</code> without detail message.
     */
    public WellFormednessException() {
    }

    /**
     * Constructs an instance of <code>TypeCheckingException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public WellFormednessException(String msg) {
        super(msg);
    }
    
    public WellFormednessException(String msg, List<String> tags) {
        super(msg, tags);
    }
    
}
