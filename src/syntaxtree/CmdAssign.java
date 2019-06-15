package syntaxtree;

import visitor.Visitor;

public class CmdAssign extends Cmd {

    public final Var v;
    public final Exp e;

    public CmdAssign(Var av, Exp ae) {
        v = av;
        e = ae;
    }

    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
