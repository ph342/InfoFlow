
/**
 * A harness to test the parser.
 */
public class ParseFlexi {

    public static void main(String[] args) throws Throwable {
        parser.WhileParser parser;
        // Read program to be parsed from file
        try {
            parser = new parser.WhileParser(new java.io.FileInputStream(args[0]));
        } catch (java.io.FileNotFoundException e) {
            System.out.println("Unable to read file " + args[0]);
            return;
        }
        String nt = "Program";
        if (args.length > 1) {
            nt = args[1];
        }
        switch (nt) {
            case "Program":
                parser.nt_Program();
                break;
            case "Exp":
                parser.nt_Exp();
                break;
            case "Command":
                parser.nt_Cmd();
                break;
            case "ProcDecl":
                parser.nt_ProcDecl();
                break;
            case "PrimaryExp":
                parser.nt_PrimaryExp();
                break;
            default:
                throw new Error("Unrecognised Non-Terminal: " + nt);
        }
    }
}
