package syntaxtree;

import java.util.List;
import visitor.Visitor;

public class ProcDecl extends AST {
    
    public final String id;
    public final List<Var> infs, outfs;
    public final List<Cmd> cmds;

    public ProcDecl(String id, List<Var> infs, List<Var> outfs,  List<Cmd> cmds) {
        this.id = id;
        this.infs = infs;
        this.outfs = outfs;
        this.cmds = cmds;
    }

    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
