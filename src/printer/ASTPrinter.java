package printer;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Stack;

import syntaxtree.AST;
import syntaxtree.Cmd;
import syntaxtree.CmdAssign;
import syntaxtree.CmdBlock;
import syntaxtree.CmdIf;
import syntaxtree.CmdWhile;
import syntaxtree.ExpFalse;
import syntaxtree.ExpInteger;
import syntaxtree.ExpNot;
import syntaxtree.ExpOp;
import syntaxtree.ExpTrue;
import syntaxtree.ExpVar;
import syntaxtree.Program;
import syntaxtree.Var;

/**
 * Visitor for printing an AST to an output stream
 * 
 * @author Dominik
 */
public class ASTPrinter extends Printer {

	private static final String NODE_PREFIX = "+- ";
	private static final String INDENT_PIPE = "|  ";
	private static final String INDENT_SPACE = "   ";
	private static final String LAST_NODE_TAG = "last";
	private Stack<String> indent;

	/**
	 * Initialise a new AST printer with stdout
	 */
	public ASTPrinter() {
		this(System.out);
	}

	/**
	 * Initialise a new AST printer
	 * 
	 * @param ps Output stream
	 */
	public ASTPrinter(PrintStream ps) {
		super(ps);
		indent = new Stack<String>();
	}

	@Override
	protected void iprintln(String s) {
		indent.forEach(string -> {
			ps.print(string); // print indentation
		});
		ps.print(NODE_PREFIX + s);
		newline();
	}

	/**
	 * If a node is last, i.e. there are no following nodes on the same level in the
	 * tree, print a different indentation
	 * 
	 * @param n Node
	 */
	private void pushIndent(AST n) {
		if (n.getTags().contains(LAST_NODE_TAG))
			indent.push(INDENT_SPACE);
		else
			indent.push(INDENT_PIPE);
	}

	// List<Cmd> cmds;
	// List<ProcDecl> pds;
	public Void visit(Program n) {
		iprintln("program");

		indent.push(INDENT_SPACE);

		// procedures are ignored for this printer
		for (Iterator<Cmd> it = n.cmds.iterator(); it.hasNext();) {
			Cmd cmd = it.next();
			if (!it.hasNext())
				cmd.tag(LAST_NODE_TAG);
			cmd.accept(this);
		}

		indent.pop();

		return null;
	}

	// List<Cmd> cmds;
	public Void visit(CmdBlock n) {
		// TODO a block would probably change the tree structure
		for (Iterator<Cmd> it = n.cmds.iterator(); it.hasNext();) {
			Cmd cmd = it.next();
			if (!it.hasNext())
				cmd.tag(LAST_NODE_TAG);
			cmd.accept(this);
		}
		return null;
	}

	// Exp e;
	// Cmd cmd1,cmd2;
	public Void visit(CmdIf n) {
		iprintln("if");

		pushIndent(n);

		n.e.accept(this);
		n.cmd1.accept(this);
		n.cmd2.tag(LAST_NODE_TAG);
		n.cmd2.accept(this);

		indent.pop();

		return null;
	}

	// Exp e;
	// Cmd cmd;
	public Void visit(CmdWhile n) {
		iprintln("while");

		pushIndent(n);

		n.e.accept(this);
		n.cmd.tag(LAST_NODE_TAG);
		n.cmd.accept(this);

		indent.pop();

		return null;
	}

	// Var v;
	// Exp e;
	public Void visit(CmdAssign n) {
		iprintln(":=");

		pushIndent(n);

		n.v.accept(this);
		n.e.tag(LAST_NODE_TAG);
		n.e.accept(this);

		indent.pop();

		return null;
	}

	// int i;
	public Void visit(ExpInteger n) {
		iprintln(Integer.toString(n.i));

		return null;
	}

	public Void visit(ExpTrue n) {
		iprintln(Boolean.toString(true));

		return null;
	}

	public Void visit(ExpFalse n) {
		iprintln(Boolean.toString(false));

		return null;
	}

	// Var v;
	public Void visit(ExpVar n) {
		n.v.accept(this);

		return null;
	}

	// Exp e;
	public Void visit(ExpNot n) {
		iprintln("!");

		pushIndent(n);

		n.e.tag(LAST_NODE_TAG);
		n.e.accept(this);

		indent.pop();

		return null;
	}

	// Exp e1, e2;
	// ExpOp.Op op;
	public Void visit(ExpOp n) {
		iprintln(n.op.toString());

		pushIndent(n);

		n.e1.accept(this);
		n.e2.tag(LAST_NODE_TAG);
		n.e2.accept(this);

		indent.pop();

		return null;
	}

	// String id
	public Void visit(Var v) {
		iprintln(v.id);

		return null;
	}

}
