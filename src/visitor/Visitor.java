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

public interface Visitor<T> {

	T visit(Program n);

	T visit(ProcDecl n);

	T visit(CmdBlock n);

	T visit(CmdIf n);

	T visit(CmdWhile n);

	T visit(CmdAssign n);

	T visit(CmdCall n);

	T visit(ExpInteger n);

	T visit(ExpTrue n);

	T visit(ExpFalse n);

	T visit(ExpVar n);

	T visit(ExpNot n);

	T visit(ExpOp n);

	T visit(Var v);
}
