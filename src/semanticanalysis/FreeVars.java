package semanticanalysis;

import java.util.HashSet;
import java.util.Set;
import syntaxtree.*;
import visitor.VisitorAdapter;

/**
 * Visitors for calculating free-variable sets.
 */
public class FreeVars extends VisitorAdapter<Set<Var>> {
    
    @Override
    public Set<Var> visit(Program n) {
        Set<Var> fvs = new HashSet<>();
        for (Cmd cmd : n.cmds) {
            fvs.addAll(cmd.accept(this));
        }
        for (ProcDecl pd : n.pds) {
            fvs.addAll(pd.accept(this));
        }
        return fvs;
    }
    
    @Override
    public Set<Var> visit(ProcDecl n) {
        Set<Var> formals = new HashSet<>();
        formals.addAll(n.infs);
        formals.addAll(n.outfs);
        Set<Var> fvs = new HashSet<>();
        for (Cmd s : n.cmds) {
            fvs.addAll(s.accept(this));
        }
        fvs.removeAll(formals);
        return fvs;
    }
    
    @Override
    public Set<Var> visit(CmdIf n) {
        Set<Var> fvs = n.e.accept(this);
        fvs.addAll(n.cmd1.accept(this));
        fvs.addAll(n.cmd2.accept(this));
        return fvs;
    }
    
    @Override
    public Set<Var> visit(CmdWhile n) {
        Set<Var> fvs = n.e.accept(this);
        fvs.addAll(n.cmd.accept(this));
        return fvs;
    }
    
    @Override
    public Set<Var> visit(CmdAssign n) {
        Set<Var> fvs = n.e.accept(this);
        fvs.add(n.v);
        return fvs;
    }
    
    @Override
    public Set<Var> visit(CmdBlock n) {
        Set<Var> fvs = new HashSet<>();
        for (Cmd s : n.cmds) {
            fvs.addAll(s.accept(this));
        }
        return fvs;
    }
    
    @Override
    public Set<Var> visit(CmdCall n) {
        Set<Var> fvs = new HashSet<>();
        fvs.addAll(n.aos);
        for (Exp e : n.ais) {
            fvs.addAll(e.accept(this));
        }
        return fvs;
    }
    
    @Override
    public Set<Var> visit(ExpOp n) {
        Set<Var> fvs = n.e1.accept(this);
        fvs.addAll(n.e2.accept(this));
        return fvs;
    }
    
    @Override
    public Set<Var> visit(ExpInteger n) {
        return new HashSet<>();
    }
    
    @Override
    public Set<Var> visit(ExpTrue n) {
        return new HashSet<>();
    }
    
    @Override
    public Set<Var> visit(ExpFalse n) {
        return new HashSet<>();
    }

    // Var v;
    @Override
    public Set<Var> visit(ExpVar n) {
        Set<Var> fvs = new HashSet<>();
        fvs.add(n.v);
        return fvs;
    }
    
    @Override
    public Set<Var> visit(ExpNot n) {
        return n.e.accept(this);
    }
}
