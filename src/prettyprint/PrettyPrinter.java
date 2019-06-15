package prettyprint;

import java.io.PrintStream;
import syntaxtree.*;
import visitor.Visitor;
import java.util.Deque;
import java.util.List;
import java.util.LinkedList;

public class PrettyPrinter implements Visitor<Void> {

    private int indent;
    private static final String INDENT = "  ";
    private Deque<Boolean> bracketing;
    private PrintStream ps;

    /**
     * Initialise a new pretty printer.
     */
    public PrettyPrinter() {
        this(System.out);
    }

    /**
     * Initialise a new pretty printer.
     */
    public PrettyPrinter(PrintStream ps) {
        this.ps = ps;
        indent = 0;
        bracketing = new LinkedList<Boolean>();
        bracketing.push(false);
    }

    /**
     * Start a new line of output.
     */
    private void newline() {
        ps.println();
    }

    /**
     * Print a string prefixed by current indent whitespace.
     */
    private void iprint(String s) {
        indent();
        ps.print(s);
    }

    /**
     * Print a string prefixed by current indent whitespace, followed by a
     * newline.
     */
    private void iprintln(String s) {
        iprint(s);
        newline();
    }

    /**
     * Print a string.
     */
    private void print(String s) {
        ps.print(s);
    }

    /**
     * Print a string, followed by a newline.
     */
    private void println(String s) {
        ps.println(s);
    }

    /**
     * Print current indent of whitespace.
     */
    private void indent() {
        for (int i = 0; i < indent; i++) {
            ps.print(INDENT);
        }
    }

    /**
     * Print opening bracket if currently bracketing.
     */
    private void openBracket() {
        if (bracketing.peek()) {
            print("(");
        }
    }

    /**
     * Print closing bracket if currently bracketing.
     */
    private void closeBracket() {
        if (bracketing.peek()) {
            print(")");
        }
    }

    private void prettyPrintActuals(List<Exp> ais, List<Var> aos) {
        print("(");
        bracketing.push(false);
        if (ais.size() > 0) {
            for (int i = 0; i < ais.size(); i++) {
                ais.get(i).accept(this);
                if (i + 1 < ais.size()) {
                    print(", ");
                }
            }
            print("; ");
        }
        for (int i = 0; i < aos.size(); i++) {
            aos.get(i).accept(this);
            if (i + 1 < aos.size()) {
                print(", ");
            }
        }
        bracketing.pop();
        print(")");
    }

    private void prettyPrintMethodCall(String id, List<Exp> ais, List<Var> aos) {
        openBracket();
        print(id);
        prettyPrintActuals(ais, aos);
        closeBracket();
    }

    // List<Cmd> cmds;
    // List<ProcDecl> pds;
    public Void visit(Program n) {
        for (Cmd cmd : n.cmds) cmd.accept(this);
        for (ProcDecl pd : n.pds) {
            newline();
            pd.accept(this);
        }
        return null;
    }

    // String id;
    // List<Var> infs;
    // List<Var> outfs;
    // List<Cmd> cmds;
    public Void visit(ProcDecl n) {
        iprint("proc");
        print(" " + n.id + "(");
        if (n.infs.size() > 0) {
            print("in ");
            for (int i = 0; i < n.infs.size(); i++) {
                n.infs.get(i).accept(this);
                if (i + 1 < n.infs.size()) {
                    print(", ");
                }
            }
            print("; ");
        }
        print("out ");
        for (int i = 0; i < n.outfs.size(); i++) {
            n.outfs.get(i).accept(this);
            if (i + 1 < n.outfs.size()) {
                print(", ");
            }
        }
        println(") {");
        indent++;
        for (Cmd s : n.cmds) {
            s.accept(this);
        }
        indent--;
        iprintln("}");
        return null;
    }

    // String id;
    public Void visit(Var v) {
        print(v.id);
        return null;
    }

    // List<Cmd> cmds;
    public Void visit(CmdBlock n) {
        iprintln("{");
        indent++;
        for (Cmd s : n.cmds) {
            s.accept(this);
        }
        indent--;
        iprintln("}");
        return null;
    }

    // Exp e;
    // Cmd cmd1, cmd2;
    public Void visit(CmdIf n) {
        iprint("if (");
        n.e.accept(this);
        println(") then");
        n.cmd1.accept(this);
        iprintln("else");
        n.cmd2.accept(this);
        return null;
    }

    // Exp e;
    // Cmd cmd;
    public Void visit(CmdWhile n) {
        iprint("while (");
        n.e.accept(this);
        println(") ");
        n.cmd.accept(this);
        return null;
    }

    // Var v;
    // Exp e;
    public Void visit(CmdAssign n) {
        iprint(n.v.id + " = ");
        //iprint(n.v.id + "(" + n.v.offset + "," + n.v.isField + ")" + " = ");
        n.e.accept(this);
        println(";");
        return null;
    }

    // String id;
    // List<Exp> ais;
    // List<Var> aos;
    public Void visit(CmdCall n) {
        iprint("");
        prettyPrintMethodCall(n.id, n.ais, n.aos);
        println(";");
        return null;
    }

    // int i;
    public Void visit(ExpInteger n) {
        print("" + n.i);
        return null;
    }

    public Void visit(ExpTrue n) {
        print("true");
        return null;
    }

    public Void visit(ExpFalse n) {
        print("false");
        return null;
    }

    // Var v;
    public Void visit(ExpVar n) {
        print(n.v.id);
        //print(n.v.id + "(" + n.v.offset + "," + n.v.isField + ")");
        return null;
    }

    // Exp e;
    public Void visit(ExpNot n) {
        print("!");
        bracketing.push(true);
        n.e.accept(this);
        bracketing.pop();
        return null;
    }

    // Exp e1, e2;
    // ExpOp.Op op;
    public Void visit(ExpOp n) {
        openBracket();
        bracketing.push(true);
        n.e1.accept(this);
        print(" " + n.op + " ");
        n.e2.accept(this);
        bracketing.pop();
        closeBracket();
        return null;
    }
}
