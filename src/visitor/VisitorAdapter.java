package visitor;

import syntaxtree.CmdAssign;
import syntaxtree.CmdBlock;
import syntaxtree.CmdCall;
import syntaxtree.CmdIf;
import syntaxtree.CmdWhile;
import syntaxtree.ExpFalse;
import syntaxtree.ExpInteger;
import syntaxtree.ExpNot;
import syntaxtree.ExpOp;
import syntaxtree.ExpTrue;
import syntaxtree.ExpVar;
import syntaxtree.ProcDecl;
import syntaxtree.Program;
import syntaxtree.Var;

/** Implements Visitor with trivial methods (all throw an error). */
public class VisitorAdapter<T> implements Visitor<T> {

	// List<Cmd> cmds;
	// List<ProcDecl> pds;
	@Override
	public T visit(Program n) {
		throw new Error("visitor called on unexpected AT node type: " + n);
	}

	// String id;
	// List<Formal> infs;
	// List<Formal> outfs;
	// List<Cmd> cmds;
	@Override
	public T visit(ProcDecl n) {
		throw new Error("visitor called on unexpected AT node type: " + n);
	}

	// List<Cmd> cmds;
	@Override
	public T visit(CmdBlock n) {
		throw new Error("visitor called on unexpected AT node type: " + n);
	}

	// Exp e;
	// Cmd cmd1,cmd2;
	@Override
	public T visit(CmdIf n) {
		throw new Error("visitor called on unexpected AT node type: " + n);
	}

	// Exp e;
	// Cmd cmd;
	@Override
	public T visit(CmdWhile n) {
		throw new Error("visitor called on unexpected AT node type: " + n);
	}

	// Var v;
	// Exp e;
	@Override
	public T visit(CmdAssign n) {
		throw new Error("visitor called on unexpected AT node type: " + n);
	}

	// String id;
	// List<Exp> ais;
	// List<Var> aos;
	@Override
	public T visit(CmdCall n) {
		throw new Error("visitor called on unexpected AT node type: " + n);
	}

	// int i;
	@Override
	public T visit(ExpInteger n) {
		throw new Error("visitor called on unexpected AT node type: " + n);
	}

	@Override
	public T visit(ExpTrue n) {
		throw new Error("visitor called on unexpected AT node type: " + n);
	}

	@Override
	public T visit(ExpFalse n) {
		throw new Error("visitor called on unexpected AT node type: " + n);
	}

	// Var v;
	@Override
	public T visit(ExpVar n) {
		throw new Error("visitor called on unexpected AT node type: " + n);
	}

	// Exp e;
	@Override
	public T visit(ExpNot n) {
		throw new Error("visitor called on unexpected AT node type: " + n);
	}

	// Exp e1, e2;
	// ExpOp.Op op;
	@Override
	public T visit(ExpOp n) {
		throw new Error("visitor called on unexpected AT node type: " + n);
	}

	// String id
	@Override
	public T visit(Var v) {
		throw new Error("visitor called on unexpected AT node type: " + v);
	}
}
