
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import cloptions.CLOptions;
import interpreter.Interpreter;
import parser.ParseException;
import parser.TokenMgrError;
import parser.WhileParser;
import printer.ASTPrinter;
import printer.PrettyPrinter;
import semanticanalysis.FreeVars;
import semanticanalysis.StaticAnalysisException;
import semanticanalysis.SymbolTableBuilder;
import semanticanalysis.security.SecurityTypeSystem;
import semanticanalysis.wellformedness.WellFormednessChecker;
import syntaxtree.Program;

/**
 * A harness to run the static analysis.
 */
public class Analyse {

	private static boolean pretty = false, wf = true, quiet = false, tree = false, interp = false, security = false;;

	/**
	 * Analyse a While-program.
	 *
	 * @param args command line arguments: the pretty file name
	 *             <p>
	 *             options:
	 *             <ul>
	 *             <li>-nowf (disable well-formedness checking)
	 *             <li>-pretty (pretty-print parsed input)
	 *             <li>-quiet (suppress progress messages)
	 *             <li>-tree (print AST)
	 *             <li>-interp (execute program and observe mem store)
	 *             <li>-sec (execute security type system checks)
	 *             </ul>
	 */
	public static void main(String[] args) {

		// fetch arguments
		List<String> argList = new ArrayList<>(Arrays.asList(args));
		Set<String> options = CLOptions.getFlags(argList, "nowf", "pretty", "quiet", "tree", "interp", "sec");
		String[] assignments = CLOptions.getAssignments(argList);
		pretty = options.contains("pretty");
		quiet = options.contains("quiet");
		wf = !options.contains("nowf");
		tree = options.contains("tree");
		interp = options.contains("interp");
		security = options.contains("sec");

		Program root;
		try {
			// parse program
			reportln("\nParsing...");
			root = parseInputProgram(argList);
			reportln("...parsing successful.");

			// pretty print parsed program
			if (pretty) {
				reportln("\nPretty-printing parsed program...");
				PrettyPrinter pp = new PrettyPrinter();
				root.accept(pp);
			}

			// Build symbol table
			reportln("\nBuilding symbol table...");
			SymbolTableBuilder stvisit = new SymbolTableBuilder();
			root.accept(stvisit);
			reportln("...symbol table successfully built.");

			// Well-formedness check, i.e. semantic checks
			if (wf) {
				reportln("\nChecking well-formedness conditions...");
				WellFormednessChecker wfChecker = new WellFormednessChecker(stvisit.symbolTable);
				root.accept(wfChecker);
				reportln("... Program is well-formed");
			}

			// print AST
			if (tree) {
				reportln("\nPrinting Abstract Syntax Tree...");
				root.accept(new ASTPrinter());
			}

			// Run interpreter
			if (interp) {
				reportln("\nInterpreting program and printing final memory store...");
				Interpreter interp = new Interpreter(assignments);
				root.accept(interp);
				reportln(interp.toString(), false);
			}

			// Security type checking
			if (security) {
				reportln("\nEstablishing dependency map for security checks...");
				SecurityTypeSystem sec = new SecurityTypeSystem(root.accept(new FreeVars()));
				reportln(root.accept(sec).toString(), false);
			}

		} catch (java.io.FileNotFoundException e) {
			System.err.println("\nUnable to read input file. " + e.getMessage());
		} catch (ParseException | TokenMgrError e) {
			System.err.println("\nSyntax error: " + e.getMessage());
		} catch (StaticAnalysisException e) {
			System.err.println("\nStatic semantics error: " + e.getMessage());
		}
	}

	private static Program parseInputProgram(List<String> argList) throws ParseException, FileNotFoundException {
		System.out.flush();
		if (argList.size() == 0) {
			reportln("Reading from standard input");
			return new WhileParser(System.in).nt_Program();
		} else
			// Read program to be parsed from file
			return new WhileParser(new java.io.FileInputStream(argList.get(0))).nt_Program();
	}

	private static void reportln(String msg, boolean silent) {
		if (!silent) {
			System.out.print(msg + "\n");
			System.out.flush();
		}
	}

	private static void reportln(String msg) {
		reportln(msg, quiet);
	}
}
