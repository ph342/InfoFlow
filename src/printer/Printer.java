package printer;

import java.io.PrintStream;

import visitor.VisitorAdapter;

/**
 * Abstract class for printers
 * @author dak1
 */
public abstract class Printer extends VisitorAdapter<Void> {

	protected PrintStream ps;
	protected int indent;
	protected static final String INDENT = "  ";

	/**
	 * Initialise a new printer with stdout
	 */
	public Printer() {
		this(System.out);
	}

	/**
	 * Initialise a new printer
	 * 
	 * @param ps Output stream
	 */
	public Printer(PrintStream ps) {
        this.ps = ps;
        indent = 0;
	}

	/**
	 * Start a new line of output.
	 */
	protected void newline() {
		ps.println();
	}

	/**
	 * Print a string prefixed by current indent whitespace.
	 */
	protected void iprint(String s) {
		indent();
		ps.print(s);
	}

	/**
	 * Print a string prefixed by current indent whitespace, followed by a newline.
	 */
	protected void iprintln(String s) {
		iprint(s);
		newline();
	}

	/**
	 * Print a string.
	 */
	protected void print(String s) {
		ps.print(s);
	}

	/**
	 * Print a string, followed by a newline.
	 */
	protected void println(String s) {
		ps.println(s);
	}

	/**
	 * Print current indent of whitespace.
	 */
	protected void indent() {
		for (int i = 0; i < indent; i++) {
			ps.print(INDENT);
		}
	}
}
