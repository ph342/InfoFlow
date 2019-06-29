package semanticanalysis.security;

import java.util.Set;

import semanticanalysis.FreeVars;
import syntaxtree.Cmd;
import syntaxtree.CmdAssign;
import syntaxtree.CmdBlock;
import syntaxtree.CmdIf;
import syntaxtree.CmdWhile;
import syntaxtree.ExpFalse;
import syntaxtree.ExpInteger;
import syntaxtree.ExpNot;
import syntaxtree.ExpOp;
import syntaxtree.ExpTrue;
import syntaxtree.ExpVar;
import syntaxtree.Program;
import syntaxtree.Var;
import visitor.VisitorAdapter;

/**
 * Visitor to calculate dependencies between variables, i.e. principal types for
 * all commands.
 * 
 * @author dak1
 */
public class SecurityTypeSystem extends VisitorAdapter<DependencyMap> {

	private Set<Var> allVariables;

	/**
	 * @param vars Program variables
	 */
	public SecurityTypeSystem(Set<Var> vars) {
		allVariables = vars;
	}

	// List<Cmd> cmds;
	// List<ProcDecl> pds;
	@Override
	public DependencyMap visit(Program n) {
		DependencyMap dep = new DependencyMap(allVariables);

//		DependencyMap delta1 = n.cmds.get(0).accept(this);
//		pre = delta1;
//		DependencyMap delta2 = n.cmds.get(1).accept(this);
//		pre = delta2.composition(delta1);
//		DependencyMap delta3 = n.cmds.get(2).accept(this);
//		pre = delta3.composition(pre);
//		DependencyMap delta4 = n.cmds.get(3).accept(this);
//		pre = delta4.composition(pre);
//		DependencyMap delta5 = n.cmds.get(4).accept(this);
//		pre = delta5.composition(pre);
//		DependencyMap delta6 = n.cmds.get(5).accept(this);
//		pre = delta6.composition(pre);

//		DependencyMap delta3 = n.cmds.get(2).accept(this);		
//		pre = delta3.composition(delta2);
//		DependencyMap delta4 = n.cmds.get(3).accept(this);
//		pre = delta4.composition(delta3);
//		DependencyMap delta5 = n.cmds.get(4).accept(this);
//		pre = delta5.composition(delta4);
//		DependencyMap delta6 = n.cmds.get(5).accept(this);		
//		pre = delta6.composition(delta5);

//		DependencyMap delta1 = n.cmds.get(0).accept(this);
//		pre = delta1;										
//		DependencyMap delta2 = n.cmds.get(1).accept(this);
//		pre = delta2.composition(delta1);
//		DependencyMap delta3 = n.cmds.get(2).accept(this);		
//		pre = delta3.composition(delta2);
//

//
//		System.out.print("Delta 1:\n" + delta1 + "\n");
//		System.out.print("Delta 2:\n" + delta2 + "\n");
//		System.out.print("Delta 3:\n" + delta3 + "\n");
//		System.out.print("Delta 4:\n" + delta4 + "\n");

		for (Cmd cmd : n.cmds) {
			dep = cmd.accept(this).composition(dep);
		}
		return dep;
	}

	// List<Cmd> cmds;
	@Override
	public DependencyMap visit(CmdBlock n) {
		DependencyMap dep = new DependencyMap(allVariables);
		for (Cmd cmd : n.cmds)
			dep = dep.composition(cmd.accept(this));
		return dep;
	}

	// Exp e;
	// Cmd cmd1,cmd2;
	@Override
	public DependencyMap visit(CmdIf n) {
		DependencyMap dep = new DependencyMap(allVariables);
		dep = dep.addDependencies(n.e.accept(new FreeVars()));

		DependencyMap depCmd1 = n.cmd1.accept(this);
		DependencyMap depCmd2 = n.cmd2.accept(this);

		depCmd1 = depCmd1.composition(dep);
		depCmd2 = depCmd2.composition(dep);

		dep = depCmd1.union(depCmd2);
		dep = dep.removeDependencies(); // reset program counter

		return dep;
	}

	// Exp e;
	// Cmd cmd;
	@Override
	public DependencyMap visit(CmdWhile n) {
		DependencyMap dep = new DependencyMap(allVariables);
		dep = dep.addDependencies(n.e.accept(new FreeVars()));

		DependencyMap depCmd = n.cmd.accept(this);

		dep = depCmd.composition(dep);
		dep = dep.closure();

		// add dependences of the pc to t to record the level of termination-sensitivity
		dep = dep.raiseTerminationLevel();
		dep = dep.removeDependencies(); // reset program counter

		return dep;
	}

	// Var v;
	// Exp e;
	@Override
	public DependencyMap visit(CmdAssign n) {
		DependencyMap dep = new DependencyMap(allVariables);
		dep = dep.addDependencies(n.v, n.e.accept(new FreeVars()));
		dep = dep.addDependencies(n.v);
		return dep;
	}

	@Override
	public DependencyMap visit(ExpInteger n) {
		return new DependencyMap(allVariables);
	}

	@Override
	public DependencyMap visit(ExpTrue n) {
		return new DependencyMap(allVariables);
	}

	@Override
	public DependencyMap visit(ExpFalse n) {
		return new DependencyMap(allVariables);
	}

	// Var v;
	@Override
	public DependencyMap visit(ExpVar n) {
		return new DependencyMap(allVariables);
	}

	// Exp e;
	@Override
	public DependencyMap visit(ExpNot n) {
		return n.e.accept(this);
	}

	// Exp e1, e2;
	// ExpOp.Op op;
	@Override
	public DependencyMap visit(ExpOp n) {
		return new DependencyMap(allVariables);
	}

	// String id
	@Override
	public DependencyMap visit(Var v) {
		return new DependencyMap(allVariables);
	}
}
