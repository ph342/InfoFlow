package printer;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import semanticanalysis.MethodSignature;
import semanticanalysis.SymbolTable;
import syntaxtree.AST;
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
	private SymbolTable st; // invariance: not null
	private Stack<String> indent; // invariance: not null
	private Map<String, ProcDecl> procedures; // invariance: not null

	/**
	 * Mapping of formals to actuals of procedures (invariance: not null)
	 */
	private Map<String, Exp> formalToActualMapping;

	/**
	 * Flag: Substitute formal with actual variables in a procedure body
	 */
	private boolean substituteVars;

	/**
	 * Initialise a new AST printer with stdout
	 * 
	 * @param st Symbol table
	 */
	public ASTPrinter(SymbolTable st) {
		this(System.out, st);
	}

	/**
	 * Initialise a new AST printer
	 *
	 * @param ps Output stream
	 * @param st Symbol table
	 */
	public ASTPrinter(PrintStream ps, SymbolTable st) {
		super(ps);
		indent = new Stack<>();
		procedures = new HashMap<String, ProcDecl>();
		formalToActualMapping = new HashMap<String, Exp>();
		this.st = st == null ? new SymbolTable() : st;
		substituteVars = false;
	}

	@Override
	protected void iprintln(String s) {
		indent.forEach(string -> {
			ps.print(string); // print indentation
		});
		ps.print(NODE_PREFIX + s);
		this.newline();
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
	@Override
	public Void visit(Program n) {
		this.iprintln("program");

		/*
		 * Procedure declarations are not printed in the AST, but their bodies are
		 * inserted on procedure calls later on.
		 */
		for (ProcDecl pd : n.pds)
			pd.accept(this);

		indent.push(INDENT_SPACE);

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
	@Override
	public Void visit(CmdBlock n) {
		this.iprintln("{}");
		this.pushIndent(n);

		for (Iterator<Cmd> it = n.cmds.iterator(); it.hasNext();) {
			Cmd cmd = it.next();
			if (!it.hasNext())
				cmd.tag(LAST_NODE_TAG);
			cmd.accept(this);
		}

		indent.pop();
		return null;
	}

	// Exp e;
	// Cmd cmd1,cmd2;
	@Override
	public Void visit(CmdIf n) {
		this.iprintln("if");

		this.pushIndent(n);

		n.e.accept(this);
		n.cmd1.accept(this);
		n.cmd2.tag(LAST_NODE_TAG);
		n.cmd2.accept(this);

		indent.pop();

		return null;
	}

	// Exp e;
	// Cmd cmd;
	@Override
	public Void visit(CmdWhile n) {
		this.iprintln("while");

		this.pushIndent(n);

		n.e.accept(this);
		n.cmd.tag(LAST_NODE_TAG);
		n.cmd.accept(this);

		indent.pop();

		return null;
	}

	// Var v;
	// Exp e;
	@Override
	public Void visit(CmdAssign n) {
		this.iprintln(":=");

		this.pushIndent(n);

		n.v.accept(this);
		n.e.tag(LAST_NODE_TAG);
		n.e.accept(this);

		indent.pop();

		return null;
	}

	// int i;
	@Override
	public Void visit(ExpInteger n) {
		this.iprintln(Integer.toString(n.i));

		return null;
	}

	@Override
	public Void visit(ExpTrue n) {
		this.iprintln(Boolean.toString(true));

		return null;
	}

	@Override
	public Void visit(ExpFalse n) {
		this.iprintln(Boolean.toString(false));

		return null;
	}

	// Var v;
	@Override
	public Void visit(ExpVar n) {
		n.v.accept(this);

		return null;
	}

	// Exp e;
	@Override
	public Void visit(ExpNot n) {
		this.iprintln("!");

		this.pushIndent(n);

		n.e.tag(LAST_NODE_TAG);
		n.e.accept(this);

		indent.pop();

		return null;
	}

	// Exp e1, e2;
	// ExpOp.Op op;
	@Override
	public Void visit(ExpOp n) {
		this.iprintln(n.op.toString());

		this.pushIndent(n);

		n.e1.accept(this);
		n.e2.tag(LAST_NODE_TAG);
		n.e2.accept(this);

		indent.pop();

		return null;
	}

	// String id
	@Override
	public Void visit(Var v) {
		// if this variable is a formal parameter in a procedure body, it needs to be
		// substituted by the actual expression that was passed
		if (substituteVars && formalToActualMapping.containsKey(v.id)) {
			// to avoid cascading replacements of actual parameters, only substitute "on the
			// highest" level
			substituteVars = false;
			formalToActualMapping.get(v.id).accept(this);
			substituteVars = true;
		} else
			this.iprintln(v.id);

		return null;
	}

	// String id;
	// List<Formal> infs;
	// List<Formal> outfs;
	// List<Cmd> cmds;
	@Override
	public Void visit(ProcDecl n) {
		// memorise the procedure by reference to subsitute the body later on
		procedures.put(n.id, n);
		return null;
	}

	// String id;
	// List<Exp> ais;
	// List<Var> aos;
	@Override
	public Void visit(CmdCall n) {

		// create a mapping of formal parameters to the actual parameters
		formalToActualMapping.clear();
		MethodSignature ms = st.getMethodSignature(n.id);

		for (int i = 0; i < n.ais.size(); ++i)
			formalToActualMapping.put(ms.infs.get(i).id, n.ais.get(i));

		for (int i = 0; i < n.aos.size(); ++i)
			formalToActualMapping.put(ms.outfs.get(i).id, new ExpVar(n.aos.get(i)));

		this.iprintln("cmd"); // print top level node
		this.pushIndent(n);

		/*
		 * Insert the body of the called procedure and substitute formals with actual
		 * expressions. The well-formedness checker ensures that every Call has a
		 * corresponding Declaration.
		 */
		substituteVars = true;

		for (Iterator<Cmd> it = procedures.get(n.id).cmds.iterator(); it.hasNext();) {
			Cmd cmd = it.next();
			if (!it.hasNext())
				cmd.tag(LAST_NODE_TAG);
			cmd.accept(this);
		}

		substituteVars = false;

		indent.pop();

		return null;
	}
}
