package syntaxtree;

import visitor.Visitor;

public class Var extends AST {

	public final String id;

	public Var(String aid) {
		id = aid;
	}

	public <T> T accept(Visitor<T> v) {
		return v.visit(this);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Var)
			return id.equals(((Var) o).id);
		else
			return false;
	}

	@Override
	public String toString() {
		return id;
	}
}
