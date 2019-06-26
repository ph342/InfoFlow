package semanticanalysis.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import syntaxtree.Var;

/**
 * Delta is a function to map variables to a subset of the powerset of all
 * variables, i.e. to a subset of all variables
 * 
 * @author dak1
 */
public class Delta {

	/**
	 * Row-major matrix [row][column]
	 */
	private boolean[][] matrix;

	/**
	 * Maps variables to row/column indexes
	 */
	private HashMap<String, Integer> indexMap;

	public Delta(Set<Var> vars) {
		if (vars == null) // TODO test if null sets work in the whole class
			vars = new HashSet<Var>();

		// elements are automatically initialised to false
		matrix = new boolean[vars.size()][vars.size()];

		indexMap = new HashMap<String, Integer>(vars.size());

		// assign variables to indexes
		int idx = 0;
		for (Iterator<Var> i = vars.iterator(); i.hasNext(); ++idx)
			indexMap.put(i.next().id, idx);

		// initialise matrix
		this.identityFunction();
	}

	/**
	 * Initially all variables are only dependent on the singleton containing
	 * itself. The identity function maps every variable to itself.
	 */
	private void identityFunction() {
		for (int i = 0; i < matrix.length; ++i)
			matrix[i][i] = true;
	}

	/**
	 * Constant-time key search + 
	 * @param v
	 * @param vars
	 */
	public void update(Var v, Set<Var> vars) {
		vars.forEach((dependentVar) -> {
			matrix[indexMap.get(v.id)][indexMap.get(dependentVar.id)] = true;
		});
	}
}
