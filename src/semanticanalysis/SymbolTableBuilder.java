package semanticanalysis;

import syntaxtree.*;
import visitor.VisitorAdapter;

/**
 * Visitors which build a symbol table for a program AST.
 */
public class SymbolTableBuilder extends VisitorAdapter<Void> {

    public final SymbolTable symbolTable;

    /**
     * Initialise a new symbol table builder.
     */
    public SymbolTableBuilder() {
        symbolTable = new SymbolTable();
    }
    
    @Override
    public Void visit(Program n) {
        for (ProcDecl pd : n.pds) {
            pd.accept(this);
        }
        return null;
    }
    
    @Override
    public Void visit(ProcDecl n) {
        symbolTable.addMethod(n);
        return null;
    }
}