package semanticanalysis;

import java.util.List;

import syntaxtree.ProcDecl;
import syntaxtree.Var;

/**
 * While-language method signatures.
 */
public class MethodSignature {

	public final String methodName;
	public final List<Var> infs, outfs;

	public MethodSignature(ProcDecl pd) {
		methodName = pd.id;
		infs = pd.infs;
		outfs = pd.outfs;
	}
}