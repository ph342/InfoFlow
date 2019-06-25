package syntaxtree;

import visitor.Visitor;

public class ExpVar extends Exp {

	public final Var v;

	public ExpVar(Var av) {
		v = av;
	}

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visit(this);
	}
}
