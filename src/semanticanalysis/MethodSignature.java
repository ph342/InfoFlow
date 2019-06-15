package semanticanalysis;

import syntaxtree.ProcDecl;

/**
 * While-language method signatures.
 */
public class MethodSignature {

    public final String methodName;
    public final int inArity, outArity; 

    public MethodSignature(ProcDecl pd) {
        this.methodName = pd.id;
        this.inArity = pd.infs.size();
        this.outArity = pd.outfs.size();
    }
}