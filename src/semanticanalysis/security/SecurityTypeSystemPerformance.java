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
 * Measuring performance of a security type system
 * 
 * @author Dominik
 */
public class SecurityTypeSystemPerformance extends SecurityTypeSystem {

	private ArrayList<SimpleEntry<String, Float>> log;
	private int indent;
	private static final String INDENT = "--";

	public SecurityTypeSystemPerformance(Set<Var> vars) {
		super(vars);
		log = new ArrayList<AbstractMap.SimpleEntry<String, Float>>();
	}

	@Override
	public DependencyMap visit(Program n) {
		Timer t = new Timer();
		t.startTimer();
		--indent;
		DependencyMap m = super.visit(n);
		log.add(new SimpleEntry<String, Float>(getIndent() + "Program", t.getRuntime()));

		System.out.println("Runtimes of security type-checker:");
		log.forEach(entry -> {
			String out = String.format("%-20s %.2f%2s\n", entry.getKey(), entry.getValue(), "ms");
			System.out.print(out);
		});
		System.out.println();

		return m;
	}

	@Override
	public DependencyMap visit(CmdIf n) {
		Timer t = new Timer();
		int currentListIndex = log.size();
		t.startTimer();
		DependencyMap m = super.visit(n);
		log.add(currentListIndex, new SimpleEntry<String, Float>(getIndent() + "If", t.getRuntime()));
		return m;
	}

	@Override
	public DependencyMap visit(CmdWhile n) {
		Timer t = new Timer();
		int currentListIndex = log.size();
		t.startTimer();
		DependencyMap m = super.visit(n);
		log.add(currentListIndex, new SimpleEntry<String, Float>(getIndent() + "While", t.getRuntime()));
		return m;
	}

	@Override
	public DependencyMap visit(CmdAssign n) {
		Timer t = new Timer();
		t.startTimer();
		DependencyMap m = super.visit(n);
		log.add(new SimpleEntry<String, Float>(getIndent() + "Assign", t.getRuntime()));
		return m;
	}

	@Override
	public DependencyMap visit(CmdCall n) {
		Timer t = new Timer();
		int currentListIndex = log.size();
		t.startTimer();
		DependencyMap m = super.visit(n);
		log.add(currentListIndex, new SimpleEntry<String, Float>(getIndent() + "Call", t.getRuntime()));
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
			++indent;
		}

		private Float getRuntime() {
			return ((float) (System.nanoTime() - lStartTime) / 1000000);
		}
	}
}
