package syntaxtree;

import visitor.Visitor;

public class ExpFalse extends Exp {

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visit(this);
	}
}
