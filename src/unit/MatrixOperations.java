package unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import semanticanalysis.security.DependencyMap;
import syntaxtree.Var;

class MatrixOperations {

	// 1 1 0 0
	// 0 0 0 1
	// 0 0 0 0
	// 1 0 1 0
	static final boolean[][] matrixA = { { true, true, false, false }, { false, false, false, true },
			{ false, false, false, false }, { true, false, true, false } };;
	// 0 1 1 1
	// 1 0 1 0
	// 1 0 0 1
	// 0 0 0 1
	static final boolean[][] matrixB = { { false, true, true, true }, { true, false, true, false },
			{ true, false, false, true }, { false, false, false, true } };

	static HashMap<String, Integer> hm;

	DependencyMap A;
	DependencyMap B;

	@BeforeEach
	void init() {
		A = new DependencyMap(matrixA, hm);
		B = new DependencyMap(matrixB, hm);
	}

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		hm = new HashMap<String, Integer>();
		hm.put("a", 1);
		hm.put("b", 2);
		hm.put("c", 3);
	}

	@Test
	void update() {
//      1 1 0 0  
//	    0 0 0 1 
//	    0 1 0 0  
//	    1 0 1 0  
		HashSet<Var> hashSet = new HashSet<Var>();
		hashSet.add(new Var("a"));

		boolean[][] expectedMatrix = new boolean[4][4];
		for (int i = 0; i < matrixA[0].length; ++i) // deep copy
			for (int j = 0; j < matrixA[1].length; ++j)
				expectedMatrix[i][j] = matrixA[i][j];

		expectedMatrix[2][1] = true;
		DependencyMap expected = new DependencyMap(expectedMatrix, hm);

		DependencyMap actual = A.update(new Var("b"), hashSet); // Test
		assertEquals(expected, actual);
	}

	@Test
	void union() {
//		1 2	1 1
//		1 0	1 1
//		1 0	0 1
//		1 0	1 1
		boolean[][] expectedMatrix = { { true, true, true, true }, { true, false, true, true },
				{ true, false, false, true }, { true, false, true, true } };

		DependencyMap expected = new DependencyMap(expectedMatrix, hm);

		assertEquals(expected, A.union(B)); // test
	}

	@Test
	void composition() {
//		1 1	2 1
//		0 0	0 1
//		0 0	0 0
//		1 1	1 2		
		boolean[][] expectedMatrix = { { true, true, true, true }, { false, false, false, true },
				{ false, false, false, false }, { true, true, true, true } };

		DependencyMap expected = new DependencyMap(expectedMatrix, hm);

		assertEquals(expected, A.composition(B)); // test
	}

	@Test
	void closure() {
//	     1 1 1 1
//	     1 1 1 1
//	     0 0 0 0
//	     1 1 1 1
		boolean[][] expectedMatrix = { { true, true, true, true }, { true, true, true, true },
				{ false, false, false, false }, { true, true, true, true } };

		DependencyMap expected = new DependencyMap(expectedMatrix, hm);

		assertEquals(expected, A.closure()); // test
	}

}
