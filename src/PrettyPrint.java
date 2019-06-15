import prettyprint.PrettyPrinter;

/**
 * A harness to test that the abstract syntax tree is being built correctly.
 * The main method pretty-prints the AST to standard out.
 */
public class PrettyPrint {

    public static void main(String[] args) {
        parser.WhileParser parser;
        try {
            if (args.length == 0) {
                // Read source code to be parsed from standard input
                parser = new parser.WhileParser(System.in);
            } else {
                // Read source code to be parsed from file
                try {
                    parser = new parser.WhileParser(new java.io.FileInputStream(args[0]));
                } catch (java.io.FileNotFoundException e) {
                    System.err.println("Unable to read file " + args[0]);
                    return;
                }
            }
            parser.nt_Program().accept(new PrettyPrinter());
        } catch (Throwable e) {
            //System.out.println(e.toString());
            e.printStackTrace();
        }
    }
}
