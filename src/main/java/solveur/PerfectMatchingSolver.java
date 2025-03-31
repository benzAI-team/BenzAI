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

}
