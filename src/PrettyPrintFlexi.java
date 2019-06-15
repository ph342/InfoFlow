
import java.io.InputStream;
import java.io.PrintStream;

import prettyprint.PrettyPrinter;

/**
 * A harness to test that the abstract syntax tree is being built correctly. The
 * main method pretty-prints the AST to standard out.
 */
public class PrettyPrintFlexi {
    
    public static void main(String[] args) {
        String nt = "Program";
        if (args.length > 0) nt = args[0];
        // Read source code to be parsed from standard input
        prettyPrint(System.in, nt, System.out);
    }

    public static void prettyPrint(InputStream is, String nt, PrintStream ps) {
        parser.WhileParser parser;
        try {
            parser = new parser.WhileParser(is);
            PrettyPrinter pp = new PrettyPrinter(ps);
            switch (nt) {
                case "Program":
                    parser.nt_Program().accept(pp);
                    break;
                case "Exp":
                    parser.nt_Exp().accept(pp);
                    break;
                case "Command":
                    parser.nt_Cmd().accept(pp);
                    break;
                case "ProcDecl":
                    parser.nt_ProcDecl().accept(pp);
                    break;
                case "PrimaryExp":
                    parser.nt_PrimaryExp().accept(pp);
                    break;
                default:
                    throw new Error("Unrecognised Non-Terminal: " + nt);
            }
        } catch (Throwable e) {
            ps.println(e);
        }
    }
}
