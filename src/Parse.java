
import syntaxtree.Program;

/**
 * A harness to test the parser.
 */
public class Parse {

    public static void main(String[] args) throws Throwable {
        parser.WhileParser parser;
        try {
            if (args.length == 0) {
                // Read program to be parsed from standard input
                parser = new parser.WhileParser(System.in);
            } else {
                // Read program to be parsed from file
                try {
                    parser = new parser.WhileParser(new java.io.FileInputStream(args[0]));
                } catch (java.io.FileNotFoundException e) {
                    System.err.println("Unable to read file " + args[0]);
                    return;
                }
            }
            System.out.println("parsing...");
            Program p = parser.nt_Program();
            System.out.println("...parse completed.");
            System.out.print(p);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
