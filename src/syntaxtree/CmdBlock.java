package syntaxtree;

import java.util.List;

import visitor.Visitor;

public class CmdBlock extends Cmd {

	public final List<Cmd> cmds;

	public CmdBlock(List<Cmd> cmds) {
		this.cmds = cmds;
	}

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visit(this);
	}
}
