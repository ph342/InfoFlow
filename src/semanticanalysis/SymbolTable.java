package semanticanalysis;

import java.util.HashMap;
import java.util.Map;

import syntaxtree.ProcDecl;

/**
 * Symbol tables containing method signatures.
 */
public class SymbolTable {

	private Map<String, MethodSignature> methodTable;

	/**
	 * Initialise a new, empty symbol table.
	 */
	public SymbolTable() {
		methodTable = new HashMap<>();
	}

	/**
	 * Find a method signature in the symbol table.
	 *
	 * @param methodName the method name
	 * @return the method signature for the named method, or null if the named
	 *         method does not exist
	 */
	public MethodSignature getMethodSignature(String methodName) {
		return methodTable.get(methodName);
	}

	/**
	 * Add a new method signature to the symbol table.
	 *
	 * @param pd the procedure declaration
	 * @return true if the signature was added, false if a signature with the given
	 *         name was already present
	 */
	public boolean addMethod(ProcDecl pd) {
		if (methodTable.containsKey(pd.id))
			return false;
		methodTable.put(pd.id, new MethodSignature(pd));
		return true;
	}

}
