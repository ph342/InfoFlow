
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import cloptions.CLOptions;
import parser.ParseException;
import parser.TokenMgrError;
import parser.WhileParser;
import prettyprint.PrettyPrinter;
import semanticanalysis.StaticAnalysisException;
import semanticanalysis.SymbolTable;
import semanticanalysis.SymbolTableBuilder;
import semanticanalysis.wellformedness.WellFormednessChecker;
import syntaxtree.Program;

/**
 * A harness to run the static analysis.
 */
public class Analyse {

	private static boolean pretty = false, wf = true, quiet = false, tree = false;

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
	 *             </ul>
	 */
	public static void main(String[] args) {

		// fetch arguments
		List<String> argList = new ArrayList<>(Arrays.asList(args));
		Set<String> options = CLOptions.options(argList, "nowf", "pretty", "quiet", "tree");
		pretty = options.contains("pretty");
		quiet = options.contains("quiet");
		wf = !options.contains("nowf");
		tree = options.contains("tree");

		Program root;
		try {
			// parse program
			reportln("Parsing...");
			root = parseInputProgram(argList);
			reportln("...parsing successful.");

			// pretty print parsed program
			if (pretty) {
				PrettyPrinter pp = new PrettyPrinter();
				root.accept(pp);
			}

			// print AST
			if (tree) {
				// TODO change Syntax (outformals) and after grammar is finished, start writing the visitors
				// ASTPrinter astp = new ASTPrinter();
				// root.accept(astp);
			}

			// Build symbol table
			SymbolTableBuilder stvisit = new SymbolTableBuilder();
			reportln("Building symbol table...");
			System.out.flush();
			root.accept(stvisit);
			reportln("...symbol table successfully built.");

			// Well-formedness check, i.e. semantic checks
			if (wf) {
				WellFormednessChecker wfChecker = new WellFormednessChecker(stvisit.symbolTable);
				reportln("Checking well-formedness conditions...");
				System.out.flush();
				root.accept(wfChecker);
				reportln("... Program is well-formed");
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
		} else {
			// Read program to be parsed from file
			return new WhileParser(new java.io.FileInputStream(argList.get(0))).nt_Program();
		}
	}

	private static void output(String fileName, String text) {
		try (Writer w = new FileWriter(fileName)) {
			w.write(text);
		} catch (IOException e) {
			System.err.println(e);
			throw new Error("Errror writing to file: " + fileName);
		}
	}

	private static void reportln(String msg) {
		if (!quiet) {
			System.out.print(msg + "\n");
			System.out.flush();
		}
	}
}
