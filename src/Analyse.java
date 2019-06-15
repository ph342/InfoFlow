
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

    private static boolean source = false, wf = true, quiet = false;

    /**
     * Analyse a While-program.
     *
     * @param args command line arguments: the source file name
     * <p>
     * options: <ul>
     * <li> -nowf (disable well-formedness checking)
     * <li> -source (pretty-print parsed input)
     * <li> -quiet (suppress progress messages)
     * </ul>
     */
    public static void main(String[] args) {
        List<String> argList = new ArrayList<>(Arrays.asList(args));
        Set<String> options = CLOptions.options(argList, "nowf", "source", "quiet");
        source = options.contains("source");
        quiet = options.contains("quiet");
        wf = !options.contains("nowf");
        Program root;
        try {
            String inputFileName = "-";
            report("Parsing...");
            System.out.flush();
            if (argList.size() == 0) {
                // Read program to be parsed from standard input
                report("(reading from standard input)\n");
                System.out.flush();
                root = new WhileParser(System.in).nt_Program();
            } else {
                // Read program to be parsed from file
                inputFileName = argList.get(0);
                try {
                    root = new WhileParser(new java.io.FileInputStream(inputFileName)).nt_Program();
                } catch (java.io.FileNotFoundException e) {
                    System.err.println("Unable to read file " + inputFileName);
                    return;
                }
            }
            reportln("...parsed OK.");
            if (source) {
                PrettyPrinter pp = new PrettyPrinter();
                root.accept(pp);
            }
            SymbolTable symTab;
            {
                SymbolTableBuilder stvisit = new SymbolTableBuilder();
                report("Building Symbol Table...");
                System.out.flush();
                root.accept(stvisit);
                reportln("...done.");
                symTab = stvisit.symbolTable;
            }
            if (wf) {
                WellFormednessChecker wfChecker = new WellFormednessChecker(symTab);
                report("Checking program is well-formed...");
                System.out.flush();
                root.accept(wfChecker);
                reportln("...OK.");
            }

            // TODO call other analysis tools here
            
        } catch (ParseException | TokenMgrError e) {
            System.out.println("\nSyntax error: " + e.getMessage());
        } catch (StaticAnalysisException e) {
            System.out.println("\nStatic semantics error: " + e.getMessage());
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

    private static void report(String msg) {
        if (!quiet) {
            System.out.print(msg);
            System.out.flush();
        }
    }

    private static void reportln(String msg) {
        report(msg + "\n");
    }
}
