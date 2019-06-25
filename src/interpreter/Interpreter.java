package interpreter;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import syntaxtree.Cmd;
import syntaxtree.CmdAssign;
import syntaxtree.CmdBlock;
import syntaxtree.CmdCall;
import syntaxtree.CmdIf;
import syntaxtree.CmdWhile;
import syntaxtree.Exp;
import syntaxtree.ExpFalse;
import syntaxtree.ExpInteger;
import syntaxtree.ExpNot;
import syntaxtree.ExpOp;
import syntaxtree.ExpTrue;
import syntaxtree.ExpVar;
import syntaxtree.ProcDecl;
import syntaxtree.Program;
import syntaxtree.Var;
import visitor.VisitorAdapter;

public class Interpreter extends VisitorAdapter<Integer> {

	private Deque<Map<String, Integer>> stack;
	private Map<String, Integer> store;
	private Map<String, ProcDecl> symTab;

	public Interpreter(String[] assignments) {
		stack = new LinkedList<>();
		this.pushStack();
		this.initStore(assignments);
	}

	public void initStore(String[] assignments) {
		for (String ass : assignments) {
			String[] parts = ass.split("\\s*=\\s*");
			String x = parts[0];
			int v = Integer.parseInt(parts[1]);
			this.write(x, v);
		}
	}

	@Override
	public String toString() {
		String ret = "";
		for (String k : store.keySet())
			ret += k + "=" + store.get(k) + "  ";
		return ret;
	}

	private void write(String x, int v) {
		store.put(x, v);
	}

	private int read(String x) {
		return store.getOrDefault(x, 0);
	}

	private void pushStack() {
		store = new HashMap<>();
		stack.push(store);
	}

	private void popStack() {
		stack.pop();
		if (stack.isEmpty())
			throw new IllegalStateException("Top-level store has been popped!");
		store = stack.peek();
	}

	// List<Cmd> cmds;
	// List<ProcDecl> pds;
	@Override
	public Integer visit(Program n) {
		symTab = new HashMap<>();
		for (ProcDecl pd : n.pds)
			symTab.put(pd.id, pd);
		for (Cmd c : n.cmds)
			c.accept(this);
		return null;
	}

	// List<Cmd> cmds;
	@Override
	public Integer visit(CmdBlock n) {
		for (Cmd c : n.cmds)
			c.accept(this);
		return null;
	}

	// Exp e;
	// Cmd cmd1,cmd2;
	@Override
	public Integer visit(CmdIf n) {
		int cond = n.e.accept(this);
		if (cond == 0)
			n.cmd2.accept(this);
		else
			n.cmd1.accept(this);
		return null;
	}

	// Exp e;
	// Cmd cmd;
	@Override
	public Integer visit(CmdWhile n) {
		while (n.e.accept(this) != 0)
			n.cmd.accept(this);
		return null;
	}

	// Var v;
	// Exp e;
	@Override
	public Integer visit(CmdAssign n) {
		this.write(n.v.id, n.e.accept(this));
		return null;
	}

	// String id;
	// List<Exp> ais;
	// List<Var> aos;
	@Override
	public Integer visit(CmdCall n) {
		ProcDecl pd = symTab.get(n.id);
		List<Integer> actualIns = new ArrayList<>();
		for (Exp e : n.ais)
			actualIns.add(e.accept(this));
		this.pushStack();
		for (int i = 0; i < pd.infs.size(); ++i)
			this.write(pd.infs.get(i).id, actualIns.get(i));
		for (Cmd c : pd.cmds)
			c.accept(this);
		List<Integer> actualOutvals = new ArrayList<>();
		for (Var x : pd.outfs)
			actualOutvals.add(this.read(x.id));
		this.popStack();
		for (int i = 0; i < n.aos.size(); ++i)
			this.write(n.aos.get(i).id, actualOutvals.get(i));
		return null;
	}

	// int i;
	@Override
	public Integer visit(ExpInteger n) {
		return n.i;
	}

	@Override
	public Integer visit(ExpTrue n) {
		return 1;
	}

	@Override
	public Integer visit(ExpFalse n) {
		return 0;
	}

	// Var v;
	@Override
	public Integer visit(ExpVar n) {
		return this.read(n.v.id);
	}

	// Exp e;
	@Override
	public Integer visit(ExpNot n) {
		if (n.e.accept(this) == 0)
			return 1;
		else
			return 0;
	}

	// Exp e1, e2;
	// ExpOp.Op op;
	@Override
	public Integer visit(ExpOp n) {
		int arg1 = n.e1.accept(this);
		int arg2 = n.e2.accept(this);
		switch (n.op) {
		case AND:
		case TIMES:
			return arg1 * arg2;
		case DIV:
			return arg1 / arg2;
		case EQUALS:
			return (arg1 == arg2) ? 1 : 0;
		case GREATERTHAN:
			return (arg1 > arg2) ? 1 : 0;
		case LESSTHAN:
			return (arg1 < arg2) ? 1 : 0;
		case MINUS:
			return arg1 - arg2;
		case PLUS:
			return arg1 + arg2;
		case OR:
			return ((arg1 != 0) || (arg2 != 0)) ? 1 : 0;
		default:
			throw new Error("Unknown operator: " + n.op);
		}
	}
}