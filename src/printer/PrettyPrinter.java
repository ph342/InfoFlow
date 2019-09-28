package printer;

import java.io.PrintStream;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import syntaxtree.Cmd;
import syntaxtree.CmdAssign;
import syntaxtree.CmdBlock;
import syntaxtree.CmdCall;
import syntaxtree.CmdIf;
import syntaxtree.CmdWhile;
import syntaxtree.Exp;
import syntaxtree.ExpFalse;
import syntaxtree.ExpInteger;
import syntaxtree.ExpNot;
import syntaxtree.ExpOp;
import syntaxtree.ExpTrue;
import syntaxtree.ExpVar;
import syntaxtree.ProcDecl;
import syntaxtree.Program;
import syntaxtree.Var;

public class PrettyPrinter extends Printer {

	private Deque<Boolean> bracketing;

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
		super(ps);
		bracketing = new LinkedList<>();
		bracketing.push(false);
	}

	/**
	 * Print opening bracket if currently bracketing.
	 */
	private void openBracket() {
		if (bracketing.peek())
			this.print("(");
	}

	/**
	 * Print closing bracket if currently bracketing.
	 */
	private void closeBracket() {
		if (bracketing.peek())
			this.print(")");
	}

	private void prettyPrintActuals(List<Exp> ais, List<Var> aos) {
		this.print("(");
		bracketing.push(false);
		if (ais.size() > 0) {
			for (int i = 0; i < ais.size(); i++) {
				ais.get(i).accept(this);
				if ((i + 1) < ais.size())
					this.print(", ");
			}
			this.print("; ");
		}
		for (int i = 0; i < aos.size(); i++) {
			aos.get(i).accept(this);
			if ((i + 1) < aos.size())
				this.print(", ");
		}
		bracketing.pop();
		this.print(")");
	}

	private void prettyPrintMethodCall(String id, List<Exp> ais, List<Var> aos) {
		this.openBracket();
		this.print(id);
		this.prettyPrintActuals(ais, aos);
		this.closeBracket();
	}

	// List<Cmd> cmds;
	// List<ProcDecl> pds;
	@Override
	public Void visit(Program n) {
		for (Cmd cmd : n.cmds)
			cmd.accept(this);
		for (ProcDecl pd : n.pds) {
			this.newline();
			pd.accept(this);
		}
		return null;
	}

	// String id;
	// List<Var> infs;
	// List<Var> outfs;
	// List<Cmd> cmds;
	@Override
	public Void visit(ProcDecl n) {
		this.iprint("proc");
		this.print(" " + n.id + "(");
		if (n.infs.size() > 0) {
			this.print("in ");
			for (int i = 0; i < n.infs.size(); i++) {
				n.infs.get(i).accept(this);
				if ((i + 1) < n.infs.size())
					this.print(", ");
			}
			this.print("; ");
		}
		if (n.outfs.size() > 0) {
			this.print("out ");
			for (int i = 0; i < n.outfs.size(); i++) {
				n.outfs.get(i).accept(this);
				if ((i + 1) < n.outfs.size())
					this.print(", ");
			}
		}
		this.println(") {");
		indent++;
		for (Cmd s : n.cmds)
			s.accept(this);
		indent--;
		this.iprintln("}");
		return null;
	}

	// String id;
	@Override
	public Void visit(Var v) {
		this.print(v.id);
		return null;
	}

	// List<Cmd> cmds;
	@Override
	public Void visit(CmdBlock n) {
		this.iprintln("{");
		indent++;
		for (Cmd s : n.cmds)
			s.accept(this);
		indent--;
		this.iprintln("}");
		return null;
	}

	// Exp e;
	// Cmd cmd1, cmd2;
	@Override
	public Void visit(CmdIf n) {
		this.iprint("if (");
		n.e.accept(this);
		this.println(")");
		n.cmd1.accept(this);
		this.iprintln("else");
		n.cmd2.accept(this);
		return null;
	}

	// Exp e;
	// Cmd cmd;
	@Override
	public Void visit(CmdWhile n) {
		this.iprint("while (");
		n.e.accept(this);
		this.println(") ");
		n.cmd.accept(this);
		return null;
	}

	// Var v;
	// Exp e;
	@Override
	public Void visit(CmdAssign n) {
		this.iprint(n.v.id + " = ");
		n.e.accept(this);
		this.println(";");
		return null;
	}

	// String id;
	// List<Exp> ais;
	// List<Var> aos;
	@Override
	public Void visit(CmdCall n) {
		this.iprint("");
		this.prettyPrintMethodCall(n.id, n.ais, n.aos);
		this.println(";");
		return null;
	}

	// int i;
	@Override
	public Void visit(ExpInteger n) {
		this.print("" + n.i);
		return null;
	}

	@Override
	public Void visit(ExpTrue n) {
		this.print("true");
		return null;
	}

	@Override
	public Void visit(ExpFalse n) {
		this.print("false");
		return null;
	}

	// Var v;
	@Override
	public Void visit(ExpVar n) {
		this.print(n.v.id);
		return null;
	}

	// Exp e;
	@Override
	public Void visit(ExpNot n) {
		this.print("!");
		bracketing.push(true);
		n.e.accept(this);
		bracketing.pop();
		return null;
	}

	// Exp e1, e2;
	// ExpOp.Op op;
	@Override
	public Void visit(ExpOp n) {
		this.openBracket();
		bracketing.push(true);
		n.e1.accept(this);
		this.print(" " + n.op + " ");
		n.e2.accept(this);
		bracketing.pop();
		this.closeBracket();
		return null;
	}
}
