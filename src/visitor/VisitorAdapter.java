package visitor;

import syntaxtree.*;

/** Implements Visitor with trivial methods (all throw an error). */
public class VisitorAdapter<T> implements Visitor<T>  {

    // List<Cmd> cmds;
    // List<ProcDecl> pds;
    public T visit(Program n) {
        throw new Error("visitor called on unexpected AT node type: " + n);
    }

    // String id;
    // List<Formal> infs;
    // List<Formal> outfs;
    // List<Cmd> cmds;
    public T visit(ProcDecl n) {
        throw new Error("visitor called on unexpected AT node type: " + n);
    }

    // List<Cmd> cmds;
    public T visit(CmdBlock n) {
        throw new Error("visitor called on unexpected AT node type: " + n);
    }

    // Exp e;
    // Cmd cmd1,cmd2;
    public T visit(CmdIf n) {
        throw new Error("visitor called on unexpected AT node type: " + n);
    }

    // Exp e;
    // Cmd cmd;
    public T visit(CmdWhile n) {
        throw new Error("visitor called on unexpected AT node type: " + n);
    }

    // Var v;
    // Exp e;
    public T visit(CmdAssign n) {
        throw new Error("visitor called on unexpected AT node type: " + n);
    }

    // String id;
    // List<Exp> ais;
    // List<Var> aos;
    public T visit(CmdCall n) {
        throw new Error("visitor called on unexpected AT node type: " + n);
    }

    // int i;
    public T visit(ExpInteger n) {
        throw new Error("visitor called on unexpected AT node type: " + n);
    }

    public T visit(ExpTrue n) {
        throw new Error("visitor called on unexpected AT node type: " + n);
    }

    public T visit(ExpFalse n) {
        throw new Error("visitor called on unexpected AT node type: " + n);
    }

    // Var v;
    public T visit(ExpVar n) {
        throw new Error("visitor called on unexpected AT node type: " + n);
    }

    // Exp e;
    public T visit(ExpNot n) {
        throw new Error("visitor called on unexpected AT node type: " + n);
    }
    
    // Exp e1, e2;
    // ExpOp.Op op;
    public T visit(ExpOp n) {
        throw new Error("visitor called on unexpected AT node type: " + n);
    }
    
    // String id
    public T visit(Var v) {
        throw new Error("visitor called on unexpected AT node type: " + v);
    }
}

