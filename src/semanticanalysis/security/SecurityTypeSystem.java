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
 * @author Dominik
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

		// Calculate transitive dependencies by composing the dependency maps
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
		Var pc = new Var(DependencyMap.PC); // program counter variable
		Set<Var> pcSet = new HashSet<Var>(); // set containing the program counter
		pcSet.add(pc);

		DependencyMap depCmd1 = n.cmd1.accept(this);
		DependencyMap depCmd2 = n.cmd2.accept(this);

		DependencyMap dep = new DependencyMap(allVariables);
		dep = dep.addDependencies(pc, n.e.accept(new FreeVars()));

		depCmd1 = depCmd1.composition(dep);
		depCmd2 = depCmd2.composition(dep);

		dep = depCmd1.union(depCmd2);
		dep = dep.replaceDependencies(pc, pcSet); // reset program counter

		return dep;
	}

	// Exp e;
	// Cmd cmd;
	@Override
	public DependencyMap visit(CmdWhile n) {
		Var pc = new Var(DependencyMap.PC); // program counter variable
		Set<Var> pcSet = new HashSet<Var>(); // set containing the program counter
		pcSet.add(pc);

		DependencyMap depCmd = n.cmd.accept(this);

		DependencyMap dep = new DependencyMap(allVariables);
		dep = dep.addDependencies(pc, n.e.accept(new FreeVars()));

		dep = depCmd.composition(dep);
		dep = dep.closure();

		// add dependences of the pc to t to record the level of termination-sensitivity
		dep = dep.raiseTerminationLevel();
		dep = dep.replaceDependencies(pc, pcSet); // reset program counter

		return dep;
	}

	// Var v;
	// Exp e;
	@Override
	public DependencyMap visit(CmdAssign n) {
		DependencyMap dep = new DependencyMap(allVariables);

		Set<Var> dependencies = n.e.accept(new FreeVars());
		dependencies.add(new Var(DependencyMap.PC)); // add pc

		dep = dep.replaceDependencies(n.v, dependencies);
		return dep;
	}

	@Override
	public DependencyMap visit(ProcDecl n) {
		// simply store the procedure and don't produce a dependency map
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

		DependencyMap inputDependencies; // placeholders -> actuals
		DependencyMap outputDependencies; // actuals -> placeholders
		DependencyMap bodyDependencies; // placeholders -> placeholders, contains actuals
		DependencyMap finalWithPlaceholders; // composition

		// Replace formal parameters with placeholders
		replaceFormalsWithPlaceholders(proc, formalsToPlaceholders, allPlaceholders);

		allVariablesAndPlaceholders.addAll(allPlaceholders);
		allVariablesAndPlaceholders.addAll(allVariables);

		inputDependencies = calculateProcInputDependencies(n, proc, allVariablesAndPlaceholders, formalsToPlaceholders);
		outputDependencies = calculateProcOutputDependencies(n, proc, allVariablesAndPlaceholders, formalsToPlaceholders);
		bodyDependencies = calculateProcBodyDependencies(proc, formalsToPlaceholders, allVariablesAndPlaceholders,
				allPlaceholders);

		// Type composition of this procedure call
		finalWithPlaceholders = outputDependencies.composition(bodyDependencies).composition(inputDependencies);

		// Remove placeholders from the mapping, since formals are no longer needed
		return finalWithPlaceholders.removeVariables(allPlaceholders);
	}

	/**
	 * Well-formedness ensures that formals are unique, but not that the names are
	 * disjunct from program variables. To avoid name clashes when mapping formals
	 * and actuals, formals will be replaced by unique placeholders.
	 * 
	 * @param formalsToPlaceholders Will be modified
	 * @param allPlaceholders       Will be modified
	 */
	private void replaceFormalsWithPlaceholders(ProcDecl proc, HashMap<Var, Var> formalsToPlaceholders,
			HashSet<Var> allPlaceholders) {

		HashSet<Var> allFormals = new HashSet<Var>(proc.outfs); // Set of all formals
		allFormals.addAll(proc.infs);

		int counter = 0;
		for (Var f : allFormals) {
			Var placeholder = new Var(String.valueOf(counter++));
			formalsToPlaceholders.put(f, placeholder);
			allPlaceholders.add(placeholder);
		}
	}

	/**
	 * Calculates the dependencies of the input parameters of a procedure
	 * declaration
	 * 
	 * @return Dependencies with all program variables, and placeholders instead of
	 *         formals.
	 */
	private DependencyMap calculateProcInputDependencies(CmdCall n, ProcDecl proc, HashSet<Var> allVariablesAndPlaceholders,
			HashMap<Var, Var> formalsToPlaceholders) {

		// Formal ins -> fv(actual ins)
		// Formal outs -> empty set
		DependencyMap inputDependencies = new DependencyMap(allVariablesAndPlaceholders);

		for (int i = 0; i < proc.infs.size(); ++i)
			// the formal ins are initially dependent on fv(Exp)
			inputDependencies = inputDependencies.replaceDependencies(formalsToPlaceholders.get(proc.infs.get(i)),
					n.ais.get(i).accept(new FreeVars()));

		for (int i = 0; i < proc.outfs.size(); ++i)
			// the formal outs are initially dependent on the empty set
			inputDependencies = inputDependencies.removeDependencies(formalsToPlaceholders.get(proc.outfs.get(i)));

		return inputDependencies;
	}

	/**
	 * Calculates the dependencies of the output parameters of a procedure
	 * declaration
	 * 
	 * @return Dependencies with all program variables, and placeholders instead of
	 *         formals.
	 */
	private DependencyMap calculateProcOutputDependencies(CmdCall n, ProcDecl proc,
			HashSet<Var> allVariablesAndPlaceholders, HashMap<Var, Var> formalsToPlaceholders) {

		// Actual outs -> formal outs + pc
		DependencyMap outputDependencies = new DependencyMap(allVariablesAndPlaceholders);
		Set<Var> dependencySet = new HashSet<>();

		for (int i = 0; i < n.aos.size(); ++i) {
			dependencySet.clear();
			dependencySet.add(formalsToPlaceholders.get(proc.outfs.get(i)));
			dependencySet.add(new Var(DependencyMap.PC)); // pc is a dependency

			outputDependencies = outputDependencies.replaceDependencies(n.aos.get(i), dependencySet);
		}
		return outputDependencies;
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
		Var pc = new Var(DependencyMap.PC); // program counter variable
		Var t = new Var(DependencyMap.T); // termination variable

		// Temporarily replace the set of program variables with the set of formals
		Set<Var> originalVars = allVariables;
		allVariables = formalsToPlaceholders.keySet(); // the keySet is equivalent to all formals

		// Generate the dependencies for the procedure body
		DependencyMap bodyDependencies = new DependencyMap(allVariables);

		for (Cmd cmd : proc.cmds)
			bodyDependencies = cmd.accept(this).composition(bodyDependencies);

		// restore program variables
		allVariables = originalVars;

		// Dependencies now: formals -> formals, without program variables
		// needed: placeholders -> placeholders and all program variables
		DependencyMap bodyDependenciesWithPlaceholders = new DependencyMap(allVariablesAndPlaceholders);
		bodyDependencies = bodyDependencies.replaceVariables(formalsToPlaceholders);

		// copy the dependencies of all placeholders
		for (Var ph : allPlaceholders)
			bodyDependenciesWithPlaceholders = bodyDependenciesWithPlaceholders.replaceDependencies(ph,
					bodyDependencies.getDependencies(ph));

		// copy the dependencies of pc and t
		bodyDependenciesWithPlaceholders = bodyDependenciesWithPlaceholders.replaceDependencies(pc,
				bodyDependencies.getDependencies(pc));
		bodyDependenciesWithPlaceholders = bodyDependenciesWithPlaceholders.replaceDependencies(t,
				bodyDependencies.getDependencies(t));

		return bodyDependenciesWithPlaceholders;
	}
}