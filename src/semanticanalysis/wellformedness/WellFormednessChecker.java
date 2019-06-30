package semanticanalysis.wellformedness;

import java.util.HashSet;
import java.util.Set;

import semanticanalysis.FreeVars;
import semanticanalysis.MethodSignature;
import semanticanalysis.SymbolTable;
import syntaxtree.Cmd;
import syntaxtree.CmdAssign;
import syntaxtree.CmdBlock;
import syntaxtree.CmdCall;
import syntaxtree.CmdIf;
import syntaxtree.CmdWhile;
import syntaxtree.ProcDecl;
import syntaxtree.Program;
import syntaxtree.Var;
import visitor.VisitorAdapter;

/**
 * Visitors for checking that While programs are well-formed.
 */
public class WellFormednessChecker extends VisitorAdapter<Void> {

	private final SymbolTable symbolTable; // invariance: not null

	private boolean insideProcedureBody;

	/**
	 * Initialise a new checker.
	 *
	 * @param s the symbol table to use
	 */
	public WellFormednessChecker(SymbolTable s) {
		symbolTable = s == null ? new SymbolTable() : s;
	}

	@Override
	public Void visit(Program n) {
		for (Cmd cmd : n.cmds)
			cmd.accept(this);
		for (ProcDecl pd : n.pds)
			pd.accept(this);
		return null;
	}

	/**
	 * Free variables and local variables are not allowed in procedure bodies,
	 * because there is no scope in this language.
	 */
	@Override
	public Void visit(ProcDecl n) {
		if (!new FreeVars().visit(n).isEmpty())
			throw new WellFormednessException("Method " + n.id + " contains free variables or local variables.",
					n.getTags());
		Set<Var> formals = new HashSet<>(n.infs);
		formals.addAll(n.outfs);
		if (formals.size() != (n.infs.size() + n.outfs.size()))
			throw new WellFormednessException("Duplicate formals in method signature: " + n.id, n.getTags());

		insideProcedureBody = true;
		for (Cmd s : n.cmds)
			s.accept(this);
		insideProcedureBody = false;

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
		for (Cmd s : n.cmds)
			s.accept(this);
		return null;
	}

	@Override
	public Void visit(CmdCall n) {
		// disallow Calls inside procedure bodies
		if (insideProcedureBody)
			throw new WellFormednessException("Method " + n.id + " contains a procedure call.", n.getTags());

		MethodSignature sig = symbolTable.getMethodSignature(n.id);
		if (sig == null)
			throw new WellFormednessException("Method " + n.id + " does not exist.", n.getTags());
		if ((sig.infs.size() != n.ais.size()) || (sig.outfs.size() != n.aos.size()))
			throw new WellFormednessException("Arity mismatch in call to method: " + n.id, n.getTags());
		Set<Var> actualOutVars = new HashSet<>(n.aos);
		if (actualOutVars.size() != n.aos.size())
			throw new WellFormednessException("Duplicate actual out-parameters in call to method: " + n.id,
					n.getTags());
		return null;
	}
}
