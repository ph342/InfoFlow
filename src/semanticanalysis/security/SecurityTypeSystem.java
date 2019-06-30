package semanticanalysis.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import semanticanalysis.FreeVars;
import syntaxtree.Cmd;
import syntaxtree.CmdAssign;
import syntaxtree.CmdBlock;
import syntaxtree.CmdCall;
import syntaxtree.CmdIf;
import syntaxtree.CmdWhile;
import syntaxtree.Exp;
import syntaxtree.ProcDecl;
import syntaxtree.Program;
import syntaxtree.Var;
import visitor.VisitorAdapter;

/**
 * Visitor to calculate dependencies between variables, i.e. principal types for
 * all commands. The visitor does not have to override methods for expressions,
 * because the type system only considers free variables of epressions.
 * 
 * @author dak1
 */
public class SecurityTypeSystem extends VisitorAdapter<DependencyMap> {

	/**
	 * All program variables. Invariance: not null
	 */
	private Set<Var> allVariables;
	private Map<String, ProcDecl> allProcedures; // Invariance: not null

	/**
	 * @param vars Program variables
	 */
	public SecurityTypeSystem(Set<Var> vars) {
		allVariables = vars != null ? vars : new HashSet<Var>();
		allProcedures = new HashMap<String, ProcDecl>();
	}

	// List<Cmd> cmds;
	// List<ProcDecl> pds;
	@Override
	public DependencyMap visit(Program n) {
		DependencyMap dep = new DependencyMap(allVariables);

		// Cache procedure declarations without calculating dependencies
		for (ProcDecl p : n.pds)
			p.accept(this);

		for (Cmd cmd : n.cmds)
			dep = cmd.accept(this).composition(dep);

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
		dep = dep.addDependencies(new Var(DependencyMap.PC), n.e.accept(new FreeVars()));

		DependencyMap depCmd1 = n.cmd1.accept(this);
		DependencyMap depCmd2 = n.cmd2.accept(this);

		depCmd1 = depCmd1.composition(dep);
		depCmd2 = depCmd2.composition(dep);

		dep = depCmd1.union(depCmd2);
		dep = dep.removeDependencies(new Var(DependencyMap.PC)); // reset program counter

		return dep;
	}

	// Exp e;
	// Cmd cmd;
	@Override
	public DependencyMap visit(CmdWhile n) {
		DependencyMap dep = new DependencyMap(allVariables);
		dep = dep.addDependencies(new Var(DependencyMap.PC), n.e.accept(new FreeVars()));

		DependencyMap depCmd = n.cmd.accept(this);

		dep = depCmd.composition(dep);
		dep = dep.closure();

		// add dependences of the pc to t to record the level of termination-sensitivity
		dep = dep.raiseTerminationLevel();
		dep = dep.removeDependencies(new Var(DependencyMap.PC)); // reset program counter

		return dep;
	}

	// Var v;
	// Exp e;
	@Override
	public DependencyMap visit(CmdAssign n) {
		DependencyMap dep = new DependencyMap(allVariables);

		Set<Var> dependencies = n.e.accept(new FreeVars());
		dependencies.add(new Var(DependencyMap.PC));

		dep = dep.addDependencies(n.v, dependencies);
		return dep; // TODO oneliner possible in a lot of methods
	}

	// String id;
	// List<Formal> infs;
	// List<Formal> outfs;
	// List<Cmd> cmds;
	@Override
	public DependencyMap visit(ProcDecl n) {
		allProcedures.put(n.id, n);
		return null;
	}

	// String id;
	// List<Exp> ais;
	// List<Var> aos;
	@Override
	public DependencyMap visit(CmdCall n) {
		ProcDecl proc = allProcedures.get(n.id); // cannot fail because of well-formedness
		// TODO ...
		// Set of all formals
		HashSet<Var> allFormals = new HashSet<Var>(proc.outfs);
		allFormals.addAll(proc.infs);

		// Mapping of all formals to their placeholders
		HashMap<Var, Var> formalsToPlaceholders = new HashMap<>();

		// Set of all placeholders
		HashSet<Var> allPlaceholders = new HashSet<>();

		// Set of all placeholders and all actuals
		HashSet<Var> placeholderFormalsAndActuals = new HashSet<>();

		DependencyMap deltaIn;
		DependencyMap deltaOut;
		DependencyMap delta; // cannot use placeholder
		DependencyMap deltaWithPlaceholder;
		DependencyMap finalDepWithPlaceholders; // composition
		DependencyMap finalDep;

		/*
		 * Well-formedness ensures that formals are unique, but not that the names are
		 * disjunct from program variables. To avoid name clashes when mapping formals
		 * and actuals, formals will be replaced by unique placeholders.
		 */
		int counter = 0;
		for (Var f : allFormals) {
			formalsToPlaceholders.put(f, new Var(String.valueOf(counter)));
			allPlaceholders.add(new Var(String.valueOf(counter)));
			++counter;
		}

		placeholderFormalsAndActuals.addAll(allPlaceholders);
		placeholderFormalsAndActuals.addAll(n.aos);
		for (Exp act : n.ais)
			placeholderFormalsAndActuals.addAll(act.accept(new FreeVars()));

		// ∆in: formal ins -> fv(E) der actual ins
		deltaIn = new DependencyMap(placeholderFormalsAndActuals);

		for (int i = 0; i < n.ais.size(); ++i)
			deltaIn = deltaIn.addDependencies(formalsToPlaceholders.get(proc.infs.get(i)),
					n.ais.get(i).accept(new FreeVars()));

		// ∆out: actual outs -> formal outs + pc
		deltaOut = new DependencyMap(placeholderFormalsAndActuals);

		for (int i = 0; i < n.aos.size(); ++i) {
			Set<Var> outDependencies = new HashSet<>();
			outDependencies.add(formalsToPlaceholders.get(proc.outfs.get(i)));
			outDependencies.add(new Var(DependencyMap.PC)); // pc is a dependency

			deltaOut = deltaOut.addDependencies(n.aos.get(i), outDependencies);
		}

		// Temporarily replace the set of program variables with the set of formals
		Set<Var> originalVars = allVariables;
		allVariables = allFormals;

		// Generate the dependencies for the procedure body
		delta = new DependencyMap(allFormals);

		for (Cmd cmd : proc.cmds)
			delta = cmd.accept(this).composition(delta);

		allVariables = originalVars;

		deltaWithPlaceholder = new DependencyMap(placeholderFormalsAndActuals);

		// Delta now: formals -> formals + pc
		// needed: placeholders -> placeholders
		for (Var formal : allFormals) {
			Set<Var> dependencies = delta.getDependencies(formal);
			Set<Var> dependenciesPlaceholders = new HashSet<>();

			// replace formal variables with placeholders
			for (Iterator<Var> it = dependencies.iterator(); it.hasNext();) {
				Var dep = it.next();

				if (formalsToPlaceholders.containsKey(dep))
					dependenciesPlaceholders.add(formalsToPlaceholders.get(dep));
				else
					dependenciesPlaceholders.add(dep); // pc doesn't have a placeholder

				it.remove(); // no auxiliary space needed
			}

			deltaWithPlaceholder = deltaWithPlaceholder.addDependencies(formalsToPlaceholders.get(formal),
					dependenciesPlaceholders);
		}

		// Type composition of this procedure call
		finalDepWithPlaceholders = deltaOut.composition(deltaWithPlaceholder).composition(deltaIn);

		// replace out-formal-placeholders in this map
		// finalDep: actual outs -> placeholders,
		// placeholders -> actual ins,
		// actual -> actual
		// needed: all vars
		finalDep = new DependencyMap(allVariables);

		for (Var act : n.aos) {
			Set<Var> dependencies = finalDepWithPlaceholders.getDependencies(act);
			Set<Var> dependenciesNoPlaceholders = new HashSet<>();

			for (Iterator<Var> it = dependencies.iterator(); it.hasNext();) {
				Var dep = it.next();
				if (!allPlaceholders.contains(dep))
					dependenciesNoPlaceholders.add(dep);

				finalDep = finalDep.addDependencies(act, dependenciesNoPlaceholders);
			}
		}
		return finalDep;
	}
}