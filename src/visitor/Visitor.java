package visitor;

import syntaxtree.*;

public interface Visitor<T> {

    public T visit(Program n);

    public T visit(ProcDecl n);

    public T visit(CmdBlock n);

    public T visit(CmdIf n);

    public T visit(CmdWhile n);

    public T visit(CmdAssign n);

    public T visit(CmdCall n);

    public T visit(ExpInteger n);

    public T visit(ExpTrue n);

    public T visit(ExpFalse n);

    public T visit(ExpVar n);

    public T visit(ExpNot n);
    
    public T visit(ExpOp n);
    
    public T visit(Var v);
}
