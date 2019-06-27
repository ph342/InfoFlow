package semanticanalysis.security;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import syntaxtree.Var;

/**
 * Immutable function to map variables to a subset of the powerset of all
 * variables. "The semantic content of Delta can be understood as a set of
 * dependencies." (p.7)
 * 
 * @author dak1
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

	public DependencyMap(boolean[][] matrix, HashMap<String, Integer> indexMap) {
		this.matrix = matrix;
		this.indexMap = indexMap;
	}

	/**
	 * Creates a dependency map equivalent to the identity function. Complexity
	 * O(2v)
	 * 
	 * @param vars All variables
	 */
	public DependencyMap(Set<Var> vars) {
		if (vars == null)
			vars = new HashSet<Var>();

		// elements are automatically initialised to false, + 1 for the program counter
		matrix = new boolean[vars.size() + 1][vars.size() + 1];

		indexMap = new HashMap<String, Integer>(vars.size());

		// assign variables to indexes
		// the program counter is at index 0
		int idx = 1;
		for (Iterator<Var> i = vars.iterator(); i.hasNext(); ++idx)
			indexMap.put(i.next().id, idx);

		/*
		 * Initially all variables are only dependent on the singleton containing
		 * itself. The identity function maps every variable to itself.
		 */
		for (int i = 0; i < matrix[0].length; ++i)
			matrix[i][i] = true;
	}

	/**
	 * Copy constructor. Complexity O(v^2)
	 */
	public DependencyMap(DependencyMap d) {
		indexMap = new HashMap<String, Integer>(d.indexMap); // shallow copy
		matrix = new boolean[d.matrix[0].length][d.matrix[1].length]; // deep copy
		for (int i = 0; i < matrix[0].length; ++i)
			for (int j = 0; j < matrix[1].length; ++j)
				matrix[i][j] = d.matrix[i][j];
	}

	/**
	 * Single value update. Existing dependencies remain unchanged. If an empty set
	 * or null is passed, no actions will be taken. Complexity O(v^2)
	 * 
	 * @param v    Variable
	 * @param vars Dependent variables or nothing
	 */
	public DependencyMap update(Var v, Set<Var> vars) {
		if (vars == null || vars.size() == 0)
			return this;

		boolean[][] m = new boolean[matrix[0].length][matrix[1].length];
		int row = indexMap.get(v.id);

		for (int i = 0; i < matrix[0].length; ++i)
			if (i == row) {
				// set every dependent variable to true
				vars.forEach((dependentVar) -> {
					m[row][indexMap.get(dependentVar.id)] = true;
				});
			} else {
				for (int col = 0; col < matrix[1].length; ++col)
					m[i][col] = matrix[i][col];
			}

		return new DependencyMap(m, new HashMap<String, Integer>(indexMap));
	}

	/**
	 * Conjunct two dependency maps. Complexity O(v^2)
	 */
	public DependencyMap union(DependencyMap d) {
		boolean[][] m = new boolean[matrix[0].length][matrix[1].length];

		for (int i = 0; i < matrix[0].length; ++i)
			for (int j = 0; j < matrix[1].length; ++j)
				m[i][j] = matrix[i][j] || d.matrix[i][j];

		return new DependencyMap(m, new HashMap<String, Integer>(indexMap));
	}

	/**
	 * Relational composition of two dependency maps. Complexity O(v^3)
	 */
	public DependencyMap composition(DependencyMap d) {
		boolean[][] m = new boolean[matrix[0].length][matrix[1].length];

		// matrix multiplication with boolean arithmetics
		for (int i = 0; i < matrix[0].length; ++i)
			for (int j = 0; j < matrix[1].length; ++j)
				for (int k = 0; k < d.matrix[1].length; k++)
					m[i][j] = m[i][j] || (matrix[i][k] && d.matrix[k][j]);

		return new DependencyMap(m, new HashMap<String, Integer>(indexMap));
	}

	/**
	 * Reflexive-transitive closure of two dependency maps. Complexity O(v^3)
	 * Warshal's algorithm
	 */
	public DependencyMap closure() {
		boolean[][] m = new boolean[matrix[0].length][matrix[1].length];

		for (int i = 0; i < matrix[0].length; ++i)
			for (int j = 0; j < matrix[0].length; ++j)
				for (int k = 0; k < matrix[0].length; k++)
					m[j][k] = m[j][k] || (matrix[j][i] && matrix[i][k]);

		return new DependencyMap(m, new HashMap<String, Integer>(indexMap));
	}

	@Override
	public String toString() {
		// TODO fix
		return "DependencyMap [matrix=" + Arrays.toString(matrix) + ", indexMap=" + indexMap + "]";
	}
}
