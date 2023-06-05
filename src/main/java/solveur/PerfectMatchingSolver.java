package solveur;

import java.io.File;
import java.util.ArrayList;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import Jama.Matrix;
import benzenoid.Benzenoid;
import benzenoid.SubGraph;
import parsers.GraphParser;
import solveur.LinAlgorithm.PerfectMatchingType;
import solveur.matrix_determinant.PerfectMatchingMatrix;
import utils.SubMolecule;

public enum PerfectMatchingSolver {
	;

	public static int computeNbPerfectMatchingDeterminant(SubMolecule subMolecule) {

		PerfectMatchingMatrix matrixInformations = PerfectMatchingMatrix.buildMatrix(subMolecule.getAdjacencyMatrix(),
				subMolecule.getNbNodes());

		Matrix matrix = new Matrix(matrixInformations.getMatrix());

		double[][] m = matrixInformations.getMatrix();

		for (double[] doubles : m) {
			for (double aDouble : doubles) {
				System.out.print((int) aDouble + "\t");
			}
			System.out.println();
		}

		if (matrix.getColumnDimension() != matrix.getRowDimension())
			return 0;

		double determinant = matrix.det();
		return (int) Math.round(determinant);
	}

	public static int computeNbPerfectMatchings(SubGraph subGraph) {

		int nbNodes = subGraph.getNbNodes();

		Model model = new Model("Kekul� structures");

		ArrayList<BoolVar> edgesList = new ArrayList<>();
		BoolVar[][] edges = new BoolVar[nbNodes][nbNodes];

		for (int i = 0; i < nbNodes; i++) {

			if (!subGraph.isDisabled(i) && subGraph.getDegrees()[i] > 0) {

				BoolVar[] adjacentEdges = new BoolVar[subGraph.getDegrees()[i]];
				int index = 0;

				for (int j = 0; j < nbNodes; j++) {

					if (subGraph.containsEdge(i, j)) {

						BoolVar edgeVariable;

						if (edges[i][j] == null) {
							edgeVariable = model.boolVar("e_" + i + "_" + j);
							edgesList.add(edgeVariable);
							edges[i][j] = edgeVariable;
							edges[j][i] = edgeVariable;
						}

						else
							edgeVariable = edges[i][j];

						adjacentEdges[index] = edgeVariable;
						index++;
					}
				}

				model.sum(adjacentEdges, "=", 1).post();
			}
		}

		BoolVar[] edgesArray = new BoolVar[edgesList.size()];
		for (int i = 0; i < edgesList.size(); i++)
			edgesArray[i] = edgesList.get(i);

		model.getSolver().setSearch(new IntStrategy(edgesArray, new FirstFail(model), new IntDomainMin()));
		Solver solver = model.getSolver();

		int nbSolutions = 0;

		while (solver.solve()) {
			Solution solution = new Solution(model);
			solution.record();
			nbSolutions++;
		}

		return nbSolutions;
	}

	public static int computeNbPerfectMatching(SubMolecule subMolecule) {

		Model model = new Model("Kekule's structure");

		BoolVar[] edges = new BoolVar[subMolecule.getNbEdges()];

		for (int i = 0; i < subMolecule.getNbEdges(); i++)
			edges[i] = model.boolVar(subMolecule.getEdgeName(i));

		for (int i = 0; i < subMolecule.getNbTotalNodes(); i++) {
			int degree = subMolecule.getEdgesMatrix().get(i).size();

			if (degree > 0) {
				BoolVar[] adjacentEdges = new BoolVar[degree];

				int index = 0;
				for (Integer edge : subMolecule.getEdgesMatrix().get(i)) {
					adjacentEdges[index] = edges[edge];
					index++;
				}

				model.sum(adjacentEdges, "=", 1).post();
			}
		}

		model.getSolver().setSearch(new IntStrategy(edges, new FirstFail(model), new IntDomainMin()));
		Solver solver = model.getSolver();

		int nbSolutions = 0;

		while (solver.solve()) {
			Solution solution = new Solution(model);
			solution.record();
			nbSolutions++;
		}

		return nbSolutions;
	}

	// filtering = AC_REGIN, AC_ZHANG
	public static int computeKekuleStructuresAllDiffConstraint(Benzenoid molecule, String filtering) {

		int nbNode = molecule.getNbNodes();

		int[] nodesSet = new int[nbNode];
		int[] visitedNodes = new int[nbNode];

		int deep = 0;
		int n = 0;

		ArrayList<Integer> q = new ArrayList<>();

		q.add(0);

		visitedNodes[0] = 1;

		int count = 1;

		// R�cup�rer l'ensemble des "atomes �toil�s"
		while (n < nbNode / 2) {

			int newCount = 0;

			for (int i = 0; i < count; i++) {

				int u = q.get(0);

				if (deep % 2 == 0) {
					nodesSet[u] = 1;
					n++;
				}

				for (int j = 0; j < molecule.getNbNodes(); j++) {
					if (molecule.getEdgeMatrix()[u][j] != 0) {

						if (visitedNodes[j] == 0) {
							visitedNodes[j] = 1;
							q.add(j);
							newCount++;
						}

					}
				}

				q.remove(0);
			}
			deep++;
			count = newCount;
		}

		/*
		 * G�n�rer le mod�le
		 */

		Model model = new Model("Kekule structures with all diff constraint");

		IntVar[] variables = new IntVar[molecule.getNbNodes() / 2];

		int indexVariable = 0;

		for (int i = 0; i < nodesSet.length; i++) {
			if (nodesSet[i] == 1) {

				int nbAdjacentEdges = molecule.getEdgeLists().get(i).size();
				int[] domain = new int[nbAdjacentEdges];

				int indexDomain = 0;

				for (int j = 0; j < molecule.getNbNodes(); j++) {
					if (molecule.getEdgeMatrix()[i][j] != 0) {
						domain[indexDomain] = j;
						indexDomain++;
					}
				}

				variables[indexVariable] = model.intVar("x_" + i, domain);

				indexVariable++;
			}
		}

		model.allDifferent(variables, filtering).post();

		model.getSolver().setSearch(Search.defaultSearch(model));

		Solver solver = model.getSolver();

		int nbStructures = 0;

		while (solver.solve()) {
			Solution solution = new Solution(model);
			solution.record();
			nbStructures++;

		}

		return nbStructures;
	}

	public static void main(String[] args) {

		Benzenoid molecule = GraphParser.parseUndirectedGraph(new File(args[0]));
		int [] d = new int [molecule.getNbNodes()];
		String mode = args[1];
		
		System.out.println(args[0]);
		
		SubGraph fg;
		
		long time;
		
		if ("0".equals(mode)) {
			System.out.println("method: determinant");
			long begin = System.currentTimeMillis();
			fg = new SubGraph(molecule.getEdgeMatrix(), d, molecule.getDegrees(), PerfectMatchingType.DET);
			System.out.println(fg.getNbPerfectMatchings() + " matchings");
			long end = System.currentTimeMillis();
			time = end - begin;
			
		}
		
		else {
			
			System.out.println("method: choco");
			
			String chocoMode = args[2];
			
			if ("0".equals(chocoMode)) {
				System.out.println("constraint: sum");
				long begin = System.currentTimeMillis();
				fg = new SubGraph(molecule.getEdgeMatrix(), d, molecule.getDegrees(), PerfectMatchingType.CHOCO);	
				System.out.println(fg.getNbPerfectMatchings() + " matchings");
				long end = System.currentTimeMillis();
				time = end - begin;
			}
			
			else if ("1".equals(chocoMode)) {
				System.out.println("constraint:  all-diff[AC_REGIN]");
				long begin = System.currentTimeMillis();
				System.out.println(PerfectMatchingSolver.computeKekuleStructuresAllDiffConstraint(molecule, "AC_REGIN") + " matchings");
				long end = System.currentTimeMillis();
				time = end - begin;
			}
			
			else {
				System.out.println("constraint: all-diff[AC_ZHANG]");
				long begin = System.currentTimeMillis();
				System.out.println(PerfectMatchingSolver.computeKekuleStructuresAllDiffConstraint(molecule, "AC_ZHANG") + " matchings");
				long end = System.currentTimeMillis();
				time = end - begin;
			}	
		}
		
		System.out.println("time: " + time + " ms.");
		
	}
}
