package semanticanalysis.wellformedness;

import java.util.HashSet;
import java.util.Set;

import semanticanalysis.FreeVars;
import semanticanalysis.MethodSignature;
import semanticanalysis.SymbolTable;
import syntaxtree.*;
import visitor.VisitorAdapter;

/**
 * Visitors for checking that While programs are well-formed.
 */
public class WellFormednessChecker extends VisitorAdapter<Void> {

    private final SymbolTable symbolTable;
    
    private final FreeVars fv = new FreeVars();

    /**
     * Initialise a new checker.
     *
     * @param s the symbol table to use
     */
    public WellFormednessChecker(SymbolTable s) {
        symbolTable = s;
    }

    @Override
    public Void visit(Program n) {
        for (Cmd cmd : n.cmds) {
            cmd.accept(this);
        }
        for (ProcDecl pd : n.pds) {
            pd.accept(this);
        }
        return null;
    }
    
    @Override
    public Void visit(ProcDecl n) {
        if (!fv.visit(n).isEmpty()) {
            throw new WellFormednessException("Method " + n.id + " contains free variables.", n.getTags());
        }
        Set<Var> formals = new HashSet<>(n.infs);
        formals.addAll(n.outfs);
        if (formals.size() != n.infs.size() + n.outfs.size()) {
            throw new WellFormednessException("Duplicate formals in method signature: " + n.id, n.getTags());
        }
        for (Cmd s : n.cmds) s.accept(this);
        return null;
    }
    
    @Override
    public Void visit(CmdIf n) {
        n.cmd1.accept(this);
        n.cmd2.accept(this);
        return null;
    }
    @Override
    public Void visit(CmdWhile n) {
        n.cmd.accept(this);
        return null;
    }
    
    @Override
    public Void visit(CmdAssign n) {
        return null;
    }
    
    @Override
    public Void visit(CmdBlock n) {
        for (Cmd s : n.cmds) s.accept(this);
        return null;
    }
    
    @Override
    public Void visit(CmdCall n) {
        MethodSignature sig = symbolTable.getMethodSignature(n.id);
        if (sig == null) {
            throw new WellFormednessException("Method " + n.id + " does not exist.", n.getTags());
        }
        if (sig.inArity != n.ais.size() || sig.outArity != n.aos.size()) {
            throw new WellFormednessException("Arity mismatch in call to method: " + n.id, n.getTags());
        }
        Set<Var> actualOutVars = new HashSet<>(n.aos);
        if (actualOutVars.size() != n.aos.size()) {
            throw new WellFormednessException("Duplicate actual out-parameters in call to method: " + n.id, n.getTags());
        }
        return null;
    }
}
