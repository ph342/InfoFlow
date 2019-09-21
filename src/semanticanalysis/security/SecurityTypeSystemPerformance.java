package semanticanalysis.security;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Set;

import syntaxtree.CmdAssign;
import syntaxtree.CmdCall;
import syntaxtree.CmdIf;
import syntaxtree.CmdWhile;
import syntaxtree.Program;
import syntaxtree.Var;

/**
 * Measuring performance of security type system
 * 
 * @author dak1
 */
public class SecurityTypeSystemPerformance extends SecurityTypeSystem {

	private ArrayList<SimpleEntry<String, Long>> log;
	private int indent;
	private static final String INDENT = "--";

	public SecurityTypeSystemPerformance(Set<Var> vars) {
		super(vars);
		log = new ArrayList<AbstractMap.SimpleEntry<String, Long>>();
	}

	@Override
	public DependencyMap visit(Program n) {
		Timer t = new Timer();
		t.startTimer();
		DependencyMap m = super.visit(n);
		log.add(new SimpleEntry<String, Long>(getIndent() + "Program", t.getRuntime()));

		System.out.println("Runtime analysis:");
		log.forEach(entry -> {
			String out = String.format("%-20s %5d%2s\n", entry.getKey(), entry.getValue(), "ms")
					.replaceAll("(.*starts\\s*)0ms", "$1");
			System.out.print(out);
		});
		System.out.println();

		return m;
	}

	@Override
	public DependencyMap visit(CmdIf n) {
		Timer t = new Timer();
		int currentListIndex = log.size();
		++indent;
		t.startTimer();
		DependencyMap m = super.visit(n);
		log.add(currentListIndex, new SimpleEntry<String, Long>(getIndent() + "If", t.getRuntime()));
		return m;
	}

	@Override
	public DependencyMap visit(CmdWhile n) {
		Timer t = new Timer();
		int currentListIndex = log.size();
		++indent;
		t.startTimer();
		DependencyMap m = super.visit(n);
		log.add(currentListIndex, new SimpleEntry<String, Long>(getIndent() + "While", t.getRuntime()));
		return m;
	}

	@Override
	public DependencyMap visit(CmdAssign n) {
		Timer t = new Timer();
		t.startTimer();
		++indent;
		DependencyMap m = super.visit(n);
		log.add(new SimpleEntry<String, Long>(getIndent() + "Assign", t.getRuntime()));
		return m;
	}

	@Override
	public DependencyMap visit(CmdCall n) {
		Timer t = new Timer();
		int currentListIndex = log.size();
		++indent;
		t.startTimer();
		DependencyMap m = super.visit(n);
		log.add(currentListIndex, new SimpleEntry<String, Long>(getIndent() + "Call", t.getRuntime()));
		return m;
	}

	private String getIndent() {
		String s = new String();
		for (int i = 0; i < indent; ++i)
			s = s.concat(INDENT);
		--indent;
		return s;
	}

	private class Timer {
		private long lStartTime;

		private void startTimer() {
			lStartTime = System.nanoTime();
		}

		private long getRuntime() {
			return (System.nanoTime() - lStartTime) / 1000000;
		}
	}
}
