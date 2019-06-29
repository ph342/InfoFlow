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
 * Immutable function to map variables to a subset of the powerset of all
 * variables. "The semantic content of Delta can be understood as a set of
 * dependencies." (p.7)
 * 
 * @author dak1
 */
/**
 * @author dak1
 *
 */
/**
 * @author dak1
 *
 */
public final class DependencyMap {

	/**
	 * Row-major matrix [row][column]
	 */
	private final boolean[][] matrix;

	/**
	 * Maps variables to row/column indexes
	 */
	private final HashMap<String, Integer> indexMap;
	private static final int PC_INDEX = 0; // program counter
	private static final int T_INDEX = 1; // termination variable

	public DependencyMap(boolean[][] matrix, HashMap<String, Integer> indexMap) {
		this.matrix = matrix;
		this.indexMap = indexMap;
	}

	/**
	 * Creates a dependency map equivalent to the identity function. Complexity O(v)
	 * 
	 * @param vars All variables
	 */
	public DependencyMap(Set<Var> vars) {
		if (vars == null)
			vars = new HashSet<Var>();

		// elements are automatically initialised to false, + 2 for pc and t
		matrix = new boolean[vars.size() + 2][vars.size() + 2];

		indexMap = new HashMap<String, Integer>(vars.size());

		// assign variables to indexes
		// the program counter is at index 0, the termination variable at index 1
		int idx = 2;
		for (Iterator<Var> i = vars.iterator(); i.hasNext(); ++idx)
			indexMap.put(i.next().id, idx);

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
		this.indexMap = new HashMap<String, Integer>(d.indexMap); // shallow copy
		this.matrix = deepCopy(d.matrix); // deep copy
	}

	/**
	 * Add dependencies to a variable. Existing dependencies remain unchanged. If an
	 * empty set or null is passed, no actions will be taken. Complexity O(v^2)
	 * 
	 * @param v    Variable
	 * @param vars Dependent variables or nothing
	 */
	public DependencyMap addDependencies(Var v, Set<Var> vars) {
		if (vars == null || vars.size() == 0)
			return this;

		boolean[][] m = new boolean[matrix.length][matrix.length];
		int row = indexMap.get(v.id);

		for (int i = 0; i < matrix.length; ++i) {
			for (int col = 0; col < matrix.length; ++col)
				m[i][col] = matrix[i][col];

			if (i == row)
				// set every dependent variable to true
				vars.forEach((dependentVar) -> {
					m[row][indexMap.get(dependentVar.id)] = true;
				});
		}

		return new DependencyMap(m, new HashMap<String, Integer>(indexMap));
	}

	/**
	 * Add dependencies to the program counter. Existing dependencies remain
	 * unchanged. If an empty set or null is passed, no actions will be taken.
	 * Complexity O(v^2)
	 * 
	 * @param vars Dependent variables or nothing
	 */
	public DependencyMap addDependencies(Set<Var> vars) {
		if (vars == null || vars.size() == 0)
			return this;

		boolean[][] m = new boolean[matrix.length][matrix.length];

		for (int i = 0; i < matrix.length; ++i) {
			for (int col = 0; col < matrix.length; ++col)
				m[i][col] = matrix[i][col];

			if (i == PC_INDEX)
				// program counter is in row 0
				// set every dependent variable to true
				vars.forEach((dependentVar) -> {
					m[PC_INDEX][indexMap.get(dependentVar.id)] = true;
				});
		}

		return new DependencyMap(m, new HashMap<String, Integer>(indexMap));
	}

	/**
	 * Make a variable dependent on the program counter. Existing dependencies of
	 * the variable remain unchanged. Complexity O(v^2)
	 * 
	 * @param v Variable that is dependent on the pc
	 */
	public DependencyMap addDependencies(Var v) {
		boolean[][] m = new boolean[matrix.length][matrix.length];
		int row = indexMap.get(v.id);

		for (int i = 0; i < matrix.length; ++i) {
			for (int col = 0; col < matrix.length; ++col)
				m[i][col] = matrix[i][col];

			if (i == row)
				// set pc to true
				m[i][PC_INDEX] = true;
		}

		return new DependencyMap(m, new HashMap<String, Integer>(indexMap));
	}

	/**
	 * Removes dependencies of a variable, except the dependency on itself.
	 * (Identity function for one variable) Complexity O(n^2)
	 * 
	 * @param v Variable to be reset
	 */
	public DependencyMap removeDependencies(Var v) {
		boolean[][] m = new boolean[matrix.length][matrix.length];
		int varRow = indexMap.get(v.id);

		for (int row = 0; row < matrix.length; ++row) {
			for (int col = 0; col < matrix.length; ++col)
				if (row == varRow)
					m[row][col] = row == col;
				else
					m[row][col] = matrix[row][col];
		}

		return new DependencyMap(m, new HashMap<String, Integer>(indexMap));
	}

	/**
	 * Removes dependencies of the program counter, except the dependency on itself.
	 * (Identity function for pc) Complexity O(n^2)
	 */
	public DependencyMap removeDependencies() {
		boolean[][] m = new boolean[matrix.length][matrix.length];

		for (int row = 0; row < matrix.length; ++row) {
			for (int col = 0; col < matrix.length; ++col)
				if (row == PC_INDEX)
					m[row][col] = row == col;
				else
					m[row][col] = matrix[row][col];
		}

		return new DependencyMap(m, new HashMap<String, Integer>(indexMap));
	}

	/**
	 * Add the dependencies of the program counter to record the maximum level of
	 * data which can influence a loop condition. Complexity O(n^2)
	 */
	public DependencyMap raiseTerminationLevel() {
		boolean[][] m = new boolean[matrix.length][matrix.length];

		for (int row = 0; row < matrix.length; ++row) {
			for (int col = 0; col < matrix.length; ++col)
				if (row == T_INDEX)
					m[row][col] = m[PC_INDEX][col] || row == col;
				else
					m[row][col] = matrix[row][col];
		}

		return new DependencyMap(m, new HashMap<String, Integer>(indexMap));
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

		return new DependencyMap(m, new HashMap<String, Integer>(indexMap));
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

		return new DependencyMap(m, new HashMap<String, Integer>(indexMap));
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

		return new DependencyMap(m, new HashMap<String, Integer>(indexMap));
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
		if (indexMap == null) {
			if (other.indexMap != null)
				return false;
		} else if (!indexMap.equals(other.indexMap))
			return false;
		if (!Arrays.deepEquals(matrix, other.matrix))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String result = "Dependencies of variables:\n";
		Stack<String> dependentVars = new Stack<String>();

		// Dependencies of pc
		result += "pc -> ";
		for (int column = 0; column < matrix.length; ++column) {
			if (matrix[PC_INDEX][column])
				if (column == PC_INDEX)
					dependentVars.push("pc");
				else
					for (Iterator<Entry<String, Integer>> it = indexMap.entrySet().iterator(); it.hasNext();) {
						Entry<String, Integer> entry = it.next();
						if (entry.getValue() == column)
							dependentVars.push(entry.getKey());
					}
		}
		result += dependentVars.toString() + "\n";
		dependentVars.clear();

		// Dependencies of t
		result += "t -> ";
		for (int column = 0; column < matrix.length; ++column) {
			if (matrix[T_INDEX][column])
				if (column == PC_INDEX)
					dependentVars.push("pc");
				else if (column == T_INDEX)
					dependentVars.push("t");
				else
					for (Iterator<Entry<String, Integer>> it = indexMap.entrySet().iterator(); it.hasNext();) {
						Entry<String, Integer> entry = it.next();
						if (entry.getValue() == column)
							dependentVars.push(entry.getKey());
					}
		}
		result += dependentVars.toString() + "\n";

		// Dependencies of all other variables
		for (Entry<String, Integer> e : indexMap.entrySet()) { // loop through variables
			result += e.getKey() + " -> ";
			dependentVars.clear();

			for (int column = 0; column < matrix.length; ++column) {
				if (matrix[e.getValue()][column])
					if (column == PC_INDEX)
						dependentVars.push("pc");
					else if (column == T_INDEX)
						dependentVars.push("t");
					else
						for (Iterator<Entry<String, Integer>> it = indexMap.entrySet().iterator(); it.hasNext();) {
							Entry<String, Integer> entry = it.next();
							if (entry.getValue() == column)
								dependentVars.push(entry.getKey());
						}
			}
			result += dependentVars.toString() + "\n";
		}
		return result;
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
