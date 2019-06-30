package semanticanalysis.security;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import syntaxtree.Var;

/**
 * Immutable object to map variables to a subset of the powerset of all
 * variables. "The semantic content of Delta can be understood as a set of
 * dependencies." (p.7)
 * 
 * @author dak1
 */
public final class DependencyMap {

	/**
	 * Row-major matrix [row][column]
	 */
	private final boolean[][] matrix; // TODO define invariance

	/**
	 * Maps variables to row/column indexes
	 */
	private final HashMap<String, Integer> varMap;

	// program variables cannot have a name starting with an underscore
	public static final String PC = "_pc"; // program counter
	public static final String T = "_t"; // termination variable

	/**
	 * Parameterised constructor.
	 */
	public DependencyMap(boolean[][] matrix, HashMap<String, Integer> varMap) {
		this.matrix = matrix;
		this.varMap = varMap;
	}

	/**
	 * Constructor to create a dependency map equivalent to the identity function.
	 * Complexity O(v)
	 * 
	 * @param vars All variables
	 */
	public DependencyMap(Set<Var> vars) {
		if (vars == null)
			vars = new HashSet<Var>();

		// elements are automatically initialised to false, + 2 for pc and t
		matrix = new boolean[vars.size() + 2][vars.size() + 2];
		varMap = new HashMap<String, Integer>(vars.size() + 2);

		// the program counter is at index 0, the termination variable at index 1
		varMap.put(PC, 0);
		varMap.put(T, 1);

		// assign variables to indexes
		int idx = 2;
		for (Iterator<Var> i = vars.iterator(); i.hasNext(); ++idx)
			varMap.put(i.next().id, idx);

		/*
		 * Initially all variables are only dependent on the singleton containing
		 * itself. The identity function maps every variable to itself.
		 */
		for (int i = 0; i < matrix.length; ++i)
			matrix[i][i] = true;
	}

	/**
	 * Copy constructor. Complexity O(v^2)
	 */
	public DependencyMap(DependencyMap d) {
		this.varMap = new HashMap<String, Integer>(d.varMap); // shallow copy
		this.matrix = deepCopy(d.matrix); // deep copy
	}

	/**
	 * Get the dependencies of a given variable.
	 * 
	 * @param v Variable
	 * @return Dependent variables or empty set
	 */
	public Set<Var> getDependencies(Var v) {
		if (v == null)
			return new HashSet<>();
		HashSet<Var> result = new HashSet<>();

		for (int col = 0; col < matrix.length; ++col) {
			if (!matrix[varMap.get(v.id)][col])
				continue;

			// Find variable name and add
			for (Iterator<Entry<String, Integer>> it = varMap.entrySet().iterator(); it.hasNext();) {
				Entry<String, Integer> entry = it.next();
				if (entry.getValue() == col)
					result.add(new Var(entry.getKey()));
			}
		}
		return result;
	}

	/**
	 * Add dependencies to a variable or the program counter. Existing dependencies
	 * remain unchanged. If an empty set or null is passed, no actions will be
	 * taken. Complexity O(v^2)
	 * 
	 * @param v    Variable or program counter
	 * @param vars Dependent variables (including pc) or nothing
	 */
	public DependencyMap addDependencies(Var v, Set<Var> vars) {
		if (vars == null || vars.size() == 0)
			return this;

		boolean[][] m = new boolean[matrix.length][matrix.length];
		int row = varMap.get(v.id);

		for (int i = 0; i < matrix.length; ++i) {
			for (int col = 0; col < matrix.length; ++col)
				m[i][col] = matrix[i][col];

			if (i == row)
				// set every dependent variable to true
				vars.forEach((dependentVar) -> {
					m[row][varMap.get(dependentVar.id)] = true;
				});
		}

		return new DependencyMap(m, new HashMap<String, Integer>(varMap));
	}

	/**
	 * Removes dependencies of a variable or the program counter, except the
	 * dependency on itself. (Identity function for one variable) Complexity O(v^2)
	 * 
	 * @param v Variable to be reset
	 */
	public DependencyMap removeDependencies(Var v) {
		boolean[][] m = new boolean[matrix.length][matrix.length];
		int varRow = varMap.get(v.id);

		for (int row = 0; row < matrix.length; ++row) {
			for (int col = 0; col < matrix.length; ++col)
				if (row == varRow)
					m[row][col] = row == col;
				else
					m[row][col] = matrix[row][col];
		}

		return new DependencyMap(m, new HashMap<String, Integer>(varMap));
	}

	/**
	 * Removes variables from this map. If the set is empty, no actions will be
	 * taken. Complexity O(v^2) TODO needed?
	 * 
	 * @param vars Variables to be deleted from this dependency map
	 */
	public DependencyMap removeVariables(Set<Var> vars) {
		if (vars == null || vars.size() == 0)
			return this;

		// collect indexes of the new variables
		Set<Integer> delIndex = new HashSet<Integer>();
		HashMap<String, Integer> newVars = new HashMap<String, Integer>(varMap);
		for (Var v : vars)
			if (varMap.containsKey(v.id)) {
				int indexOld = varMap.get(v.id);
				delIndex.add(indexOld);
				newVars.remove(v.id);

				// recalculate all existing indexes
				for (Iterator<Entry<String, Integer>> it = newVars.entrySet().iterator(); it.hasNext();) {
					Entry<String, Integer> e = it.next();
					if (e.getValue() > indexOld)
						e.setValue(e.getValue() - 1);
				}
			}

		// anything to be done?
		if (delIndex.size() == 0)
			return this;

		// Transform NxN matrix to MxM
		boolean[][] m = new boolean[matrix.length - delIndex.size()][matrix.length - delIndex.size()];
		int rowNew = 0, colNew = 0;

		for (int row = 0; row < matrix.length; ++row) {
			// if this row is to be deleted, skip it
			if (delIndex.contains(row))
				continue;

			// Copy this row
			colNew = 0;
			for (int col = 0; col < matrix.length; ++col) {
				// if this column is to be deleted, skip it
				if (delIndex.contains(col))
					continue;

				m[rowNew][colNew] = matrix[row][col];
				++colNew;
			}
			++rowNew;
		}

		return new DependencyMap(m, newVars);
	}

	/**
	 * Add the dependencies of the program counter to t to record the maximum level
	 * of data which can influence a loop condition. Complexity O(v^2)
	 */
	public DependencyMap raiseTerminationLevel() {
		boolean[][] m = new boolean[matrix.length][matrix.length];
		int tRow = varMap.get(T);
		int pcRow = varMap.get(PC);

		for (int row = 0; row < matrix.length; ++row) {
			for (int col = 0; col < matrix.length; ++col)
				if (row == tRow)
					m[row][col] = m[pcRow][col] || row == col;
				else
					m[row][col] = matrix[row][col];
		}

		return new DependencyMap(m, new HashMap<String, Integer>(varMap));
	}

	/**
	 * Conjunct two dependency maps, equivalent to least-upper-bound. Complexity
	 * O(v^2)
	 */
	public DependencyMap union(DependencyMap d) {
		boolean[][] m = new boolean[matrix.length][matrix.length];

		// matrix addition with boolean arithmetics
		for (int i = 0; i < matrix.length; ++i)
			for (int j = 0; j < matrix.length; ++j)
				m[i][j] = matrix[i][j] || d.matrix[i][j];

		return new DependencyMap(m, new HashMap<String, Integer>(varMap));
	}

	/**
	 * Relational composition of two dependency maps. Complexity O(v^3)
	 */
	public DependencyMap composition(DependencyMap d) {
		boolean[][] m = new boolean[matrix.length][matrix.length];

		// matrix multiplication with boolean arithmetics
		for (int i = 0; i < matrix.length; ++i)
			for (int j = 0; j < matrix.length; ++j)
				for (int k = 0; k < d.matrix.length; k++)
					m[i][j] = m[i][j] || (matrix[i][k] && d.matrix[k][j]);

		return new DependencyMap(m, new HashMap<String, Integer>(varMap));
	}

	/**
	 * Reflexive-transitive closure of a dependency map. Complexity O(v^3)
	 */
	public DependencyMap closure() {
		boolean[][] m = deepCopy(matrix);

		// Warshal's algorithm, reflectivity is given by the identity function
		for (int i = 0; i < m.length; ++i)
			for (int j = 0; j < m.length; ++j)
				for (int k = 0; k < m.length; ++k)
					m[j][k] = m[j][k] || (m[j][i] && m[i][k]);

		return new DependencyMap(m, new HashMap<String, Integer>(varMap));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DependencyMap other = (DependencyMap) obj;
		if (varMap == null) {
			if (other.varMap != null)
				return false;
		} else if (!varMap.equals(other.varMap))
			return false;
		if (!Arrays.deepEquals(matrix, other.matrix))
			return false;
		return true;
	}

	@Override
	public String toString() {
		Stack<String> result = new Stack<String>();
		Stack<String> dependentVars = new Stack<String>();

		result.push("Dependencies of variables:\n");

		// Dependencies of all variables
		for (Entry<String, Integer> e : varMap.entrySet()) {
			dependentVars.clear();

			for (int column = 0; column < matrix.length; ++column) {
				if (matrix[e.getValue()][column])
					for (Iterator<Entry<String, Integer>> it = varMap.entrySet().iterator(); it.hasNext();) {
						Entry<String, Integer> entry = it.next();
						if (entry.getValue() == column)
							dependentVars.push(entry.getKey().replaceFirst("^_pc$", "pc").replaceFirst("^_t$", "t"));
					}
			}
			// pretty print pc and t
			switch (e.getKey()) {
			case PC:
				result.insertElementAt(
						e.getKey().replaceFirst("^_pc$", "pc") + " -> {" + dependentVars.toString() + "}\n", 1);
				break;
			case T:
				result.insertElementAt(
						e.getKey().replaceFirst("^_t$", "t") + " -> {" + dependentVars.toString() + "}\n", 2);
				break;
			default:
				result.push(e.getKey() + " -> {" + dependentVars.toString() + "}\n");
			}
		}

		String unformatted = "";
		for (String s : result)
			unformatted += s;
		return unformatted.replace("[", "").replace("]", "");
	}

	/**
	 * Utility to perform a deep copy of a square matrix. Complexity O(n^2)
	 * 
	 * @param original Original matrix
	 */
	public static final boolean[][] deepCopy(boolean[][] original) {
		if (original == null || original[0].length != original[1].length)
			return null;

		final boolean[][] result = new boolean[original.length][original.length];
		for (int i = 0; i < original.length; ++i)
			for (int j = 0; j < original.length; ++j)
				result[i][j] = original[i][j];
		return result;
	}
}
