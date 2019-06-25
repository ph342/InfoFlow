
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
import semanticanalysis.StaticAnalysisException;
import semanticanalysis.SymbolTableBuilder;
import semanticanalysis.wellformedness.WellFormednessChecker;
import syntaxtree.Program;

/**
 * A harness to run the static analysis.
 */
public class Analyse {

	private static boolean pretty = false, wf = true, quiet = false, tree = false, interp = false;

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
	 *             </ul>
	 */
	public static void main(String[] args) {

		// fetch arguments
		List<String> argList = new ArrayList<>(Arrays.asList(args));
		Set<String> options = CLOptions.getFlags(argList, "nowf", "pretty", "quiet", "tree", "interp");
		String[] assignments = CLOptions.getAssignments(argList);
		pretty = options.contains("pretty");
		quiet = options.contains("quiet");
		wf = !options.contains("nowf");
		tree = options.contains("tree");
		interp = options.contains("interp");

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
				ASTPrinter astp = new ASTPrinter(stvisit.symbolTable);
				root.accept(astp);
			}

			// Run interpreter
			if (interp) {
				reportln("\nInterpreting program and printing final memory store...");
				Interpreter interp = new Interpreter(assignments);
				root.accept(interp);
				reportln(interp.toString());
			}

			// TODO call other analysis tools here

		} catch (java.io.FileNotFoundException e) {
			System.err.println("Unable to read input file. " + e.getMessage());
		} catch (ParseException | TokenMgrError e) {
			System.err.println("\nSyntax error: " + e.getMessage());
		} catch (StaticAnalysisException e) {
			System.err.println("\nStatic semantics error: " + e.getMessage());
		}
	}

	private static Program parseInputProgram(List<String> argList) throws ParseException, FileNotFoundException {
		System.out.flush();
		if (argList.size() == 0) {
			// Read program to be parsed from standard input
			reportln("Reading from standard input");
			return new WhileParser(System.in).nt_Program();
		} else
			// Read program to be parsed from file
			return new WhileParser(new java.io.FileInputStream(argList.get(0))).nt_Program();
	}

	private static void reportln(String msg) {
		if (!quiet) {
			System.out.print(msg + "\n");
			System.out.flush();
		}
	}
}
