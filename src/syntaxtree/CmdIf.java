package syntaxtree;

import visitor.Visitor;

public class CmdIf extends Cmd {

	public final Exp e;
	public final Cmd cmd1, cmd2;

	public CmdIf(Exp e, Cmd cmd1, Cmd cmd2) {
		this.e = e;
		this.cmd1 = cmd1;
		this.cmd2 = cmd2;
	}

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visit(this);
	}
}
