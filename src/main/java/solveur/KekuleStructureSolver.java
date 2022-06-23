package solveur;

import java.util.ArrayList;
import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import molecules.Molecule;
import molecules.UndirPonderateGraph;
import parsers.GraphParser;
import solution.ClarCoverSolution;
import utils.Couple;

public class KekuleStructureSolver {

	public static ArrayList<int[][]> computeKekuleStructures(Molecule molecule, int nbSolutionsMax) {

		ArrayList<int[][]> structures = new ArrayList<>();

		Model model = new Model("Kekulé Structures");

		BoolVar[] edges = new BoolVar[molecule.getNbEdges()];

		for (int i = 0; i < molecule.getNbEdges(); i++) {
			edges[i] = model.boolVar("edge " + (i + 1));
		}

		for (int i = 0; i < molecule.getEdgeMatrix().size(); i++) {
			int nbAdjacentEdges = molecule.getEdgeMatrix().get(i).size();
			BoolVar[] adjacentEdges = new BoolVar[nbAdjacentEdges];

			for (int j = 0; j < nbAdjacentEdges; j++) {
				adjacentEdges[j] = edges[molecule.getEdgeMatrix().get(i).get(j)];
			}

			model.sum(adjacentEdges, "=", 1).post();
		}

		model.getSolver().setSearch(new IntStrategy(edges, new FirstFail(model), new IntDomainMin()));
		Solver solver = model.getSolver();
		int nbSolutions = 0;

		while (solver.solve() && nbSolutions < nbSolutionsMax) {
			Solution solution = new Solution(model);
			solution.record();

			int[] edgesValues = new int[molecule.getNbEdges()];

			for (int j = 0; j < molecule.getNbEdges(); j++) {
				edgesValues[j] = solution.getIntVal(edges[j]);
			}

			/*
			 * Computing the curent Kekulé's structure
			 */

			UndirPonderateGraph kekuleStructure = GraphParser.exportSolutionToPonderateGraph(molecule, edgesValues);
			structures.add(kekuleStructure.getAdjacencyMatrix());
		}

		return structures;
	}

	public static ArrayList<ClarCoverSolution> solve(Molecule molecule) {

		Model model = new Model("Clar Cover");

		BoolVar[] circles = new BoolVar[molecule.getNbHexagons()];
		BoolVar[] bonds = new BoolVar[molecule.getNbEdges()];
		BoolVar[] singleElectrons = new BoolVar[molecule.getNbNodes()];

		BoolVar[][] bondsMatrix = new BoolVar[molecule.getNbNodes()][molecule.getNbNodes()];

		IntVar nbCircles = model.intVar("nb_circles", 0, molecule.getNbHexagons());
		IntVar nbSingleElectrons = model.intVar("nb_single_electrons", 0, 2);

		for (int i = 0; i < molecule.getNbHexagons(); i++)
			circles[i] = model.boolVar("circle[" + i + "]");

		int index = 0;
		for (int i = 0; i < molecule.getNbNodes(); i++) {
			for (int j = (i + 1); j < molecule.getNbNodes(); j++) {
				if (molecule.edgeExists(i, j)) {
					BoolVar bondVariable = model.boolVar("bond[" + i + "][" + j + "]");
					bonds[index] = bondVariable;
					bondsMatrix[i][j] = bondVariable;
					bondsMatrix[j][i] = bondVariable;

					index++;
				}
			}

			BoolVar singleElectronVariable = model.boolVar("single_electron[" + i + "]");
			singleElectrons[i] = singleElectronVariable;

			ArrayList<BoolVar> sumList = new ArrayList<>();

			for (Couple<Integer, Integer> edge : molecule.getBoundsInvolved(i)) {
				BoolVar boundVariable = bondsMatrix[edge.getX()][edge.getY()];
				sumList.add(boundVariable);
			}

			for (Integer circle : molecule.getHexagonsInvolved(i)) {
				BoolVar circleVariable = circles[circle];
				sumList.add(circleVariable);
			}

			sumList.add(singleElectronVariable);

			BoolVar[] sum = new BoolVar[sumList.size()];
			for (int j = 0; j < sumList.size(); j++)
				sum[j] = sumList.get(j);

			model.sum(sum, "=", 1).post();
		}

		model.sum(circles, "=", nbCircles).post();
		model.arithm(nbCircles, "=", 0).post();
		model.sum(singleElectrons, "=", nbSingleElectrons).post();

		IntVar OBJ = model.intVar("objectif", -200, 999);
		model.scalar(new IntVar[] { nbCircles, nbSingleElectrons }, new int[] { 1, -100 }, "=", OBJ).post();
		model.setObjective(Model.MAXIMIZE, OBJ);

		Solver solver = model.getSolver();

		ArrayList<ClarCoverSolution> clarCoverSolutions = new ArrayList<>();

		List<Solution> solutions = solver.findAllOptimalSolutions(OBJ, Model.MAXIMIZE);

		for (Solution solution : solutions) {

			int[] circlesInt = new int[circles.length];
			int[][] bondsInt = new int[molecule.getNbNodes()][molecule.getNbNodes()];
			int[] singleElectronsInt = new int[molecule.getNbNodes()];

			for (int i = 0; i < circles.length; i++)
				circlesInt[i] = solution.getIntVal(circles[i]);

			for (int i = 0; i < molecule.getNbNodes(); i++) {

				singleElectronsInt[i] = solution.getIntVal(singleElectrons[i]);

				for (int j = (i + 1); j < molecule.getNbNodes(); j++) {
					if (bondsMatrix[i][j] != null) {
						int value = solution.getIntVal(bondsMatrix[i][j]);
						bondsInt[i][j] = value;
						bondsInt[j][i] = value;
					}
				}

			}

			clarCoverSolutions.add(new ClarCoverSolution(circlesInt, bondsInt, singleElectronsInt));

		}

		return clarCoverSolutions;
	}

}
