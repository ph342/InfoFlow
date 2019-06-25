package syntaxtree;

import visitor.Visitor;

public class CmdWhile extends Cmd {

	public final Exp e;
	public final Cmd cmd;

	public CmdWhile(Exp e, Cmd cmd) {
		this.e = e;
		this.cmd = cmd;
	}

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visit(this);
	}
}
