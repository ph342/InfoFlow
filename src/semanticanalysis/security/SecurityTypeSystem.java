package semanticanalysis.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import semanticanalysis.FreeVars;
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
		dependencies.add(new Var(DependencyMap.PC)); // add pc

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

		// Mapping of all formals to their placeholders
		HashMap<Var, Var> formalsToPlaceholders = new HashMap<>();

		// Set of all placeholders
		HashSet<Var> allPlaceholders = new HashSet<>();

		// Set of all placeholders and all variables
		HashSet<Var> allVariablesAndPlaceholders = new HashSet<>();

		// Set of all formals
		HashSet<Var> allFormals = new HashSet<Var>(proc.outfs);
		allFormals.addAll(proc.infs);

		DependencyMap inputDependencies; // placeholders -> actuals
		DependencyMap outputDependencies; // actuals -> placeholders
		DependencyMap bodyDependencies; // placeholders -> placeholders, contains actuals
		DependencyMap finalWithPlaceholders; // composition

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

		allVariablesAndPlaceholders.addAll(allPlaceholders);
		allVariablesAndPlaceholders.addAll(allVariables);

		// Formal ins -> fv(actual ins)
		inputDependencies = new DependencyMap(allVariablesAndPlaceholders);

		for (int i = 0; i < n.ais.size(); ++i)
			inputDependencies = inputDependencies.addDependencies(formalsToPlaceholders.get(proc.infs.get(i)),
					n.ais.get(i).accept(new FreeVars()));

		// Actual outs -> formal outs + pc
		outputDependencies = new DependencyMap(allVariablesAndPlaceholders);

		for (int i = 0; i < n.aos.size(); ++i) {
			Set<Var> outDependencies = new HashSet<>();
			outDependencies.add(formalsToPlaceholders.get(proc.outfs.get(i)));
			outDependencies.add(new Var(DependencyMap.PC)); // pc is a dependency

			outputDependencies = outputDependencies.addDependencies(n.aos.get(i), outDependencies);
		}

		bodyDependencies = calculateProcBodyDependencies(proc, formalsToPlaceholders, allVariablesAndPlaceholders,
				allPlaceholders);

		// Type composition of this procedure call
		finalWithPlaceholders = outputDependencies.composition(bodyDependencies).composition(inputDependencies);

		// Remove placeholders from the mapping, since formals are no longer needed
		return finalWithPlaceholders.removeVariables(allPlaceholders);
	}

	/**
	 * Calculates the dependencies of a procedure body and substitutes formals with
	 * placeholders afterwards.
	 * 
	 * @return Dependencies with all program variables, and placeholders instead of
	 *         formals.
	 */
	private DependencyMap calculateProcBodyDependencies(ProcDecl proc, HashMap<Var, Var> formalsToPlaceholders,
			HashSet<Var> allVariablesAndPlaceholders, HashSet<Var> allPlaceholders) {

		// Temporarily replace the set of program variables with the set of formals
		Set<Var> originalVars = allVariables;
		allVariables = formalsToPlaceholders.keySet(); // the key set is equivalent to all formals

		// Generate the dependencies for the procedure body
		DependencyMap bodyDependencies = new DependencyMap(formalsToPlaceholders.keySet());

		for (Cmd cmd : proc.cmds)
			bodyDependencies = cmd.accept(this).composition(bodyDependencies);

		allVariables = originalVars;

		// Delta now: formals -> formals, without program variables
		// needed: placeholders -> placeholders and all program variables
		DependencyMap bodyDependenciesWithPlaceholders = new DependencyMap(allVariablesAndPlaceholders);
		bodyDependencies = bodyDependencies.replaceVariables(formalsToPlaceholders);

		for (Var ph : allPlaceholders)
			bodyDependenciesWithPlaceholders = bodyDependenciesWithPlaceholders.addDependencies(ph,
					bodyDependencies.getDependencies(ph));

		return bodyDependenciesWithPlaceholders;
	}
}