package syntaxtree;

import java.util.List;
import visitor.Visitor;

public class Program extends AST {

    public final List<Cmd> cmds;
    public final List<ProcDecl> pds;

    public Program(List<Cmd> cmd, List<ProcDecl> pds) {
        this.cmds = cmd;
        this.pds = pds;
    }

    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
    
    @Override
    public String toString() {
        return "Program{" + "cmds=\n" + cmds + "\n, pds=\n" + pds + '}';
    }
}
