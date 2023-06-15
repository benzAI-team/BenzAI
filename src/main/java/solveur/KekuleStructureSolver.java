package solveur;

import benzenoid.Benzenoid;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import solution.ClarCoverSolution;
import utils.Couple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum KekuleStructureSolver {
    ;

    public static ArrayList<int[][]> computeKekuleStructures(Benzenoid molecule, int nbSolutionsMax) {

		ArrayList<int[][]> structures = new ArrayList<>();

		Model model = new Model("Kekulé Structures");

		BoolVar[] edges = new BoolVar[molecule.getNbBonds()];

		Couple<Integer, Integer> [] indexesEdges = new Couple[molecule.getNbBonds()];
		int [][] edgesIndexes = new int[molecule.getNbCarbons()][molecule.getNbCarbons()];
		int [] adjacentEdges = new int[molecule.getNbCarbons()];
		int index = 0;
		
		for (int i = 0; i < molecule.getNbCarbons() ; i++) {
			Arrays.fill(edgesIndexes[i], -1);
		}
		
		for (int i = 0; i < molecule.getNbCarbons() ; i++) {
			for (int j = (i + 1); j < molecule.getNbCarbons() ; j++) {
				if (molecule.getEdgeMatrix()[i][j] == 1) {
					edges[index] = model.boolVar("e_" + i + "_" + j);
					edgesIndexes[i][j] = index;
					edgesIndexes[j][i] = index;
					indexesEdges[index] = new Couple<>(i, j);
					System.out.println("(" + i + ", " + j + ") -> " + index);
					adjacentEdges[i] ++;
					adjacentEdges[j] ++;
					index ++;
				}
			}
		}
		
		for (int i = 0; i < molecule.getNbCarbons() ; i++) {
			BoolVar[] edgeSum = new BoolVar[adjacentEdges[i]];
			index = 0;
			for (int j = 0; j < molecule.getNbCarbons() ; j++) {
				if (molecule.getEdgeMatrix()[i][j] == 1) {
					int edgeIndex = edgesIndexes[i][j];
					edgeSum[index] = edges[edgeIndex];
					index ++;
				}
			}
			model.sum(edgeSum, "=", 1).post();
		}

		model.getSolver().setSearch(new IntStrategy(edges, new FirstFail(model), new IntDomainMin()));
		Solver solver = model.getSolver();
		int nbSolutions = 0;

		while (solver.solve() && nbSolutions < nbSolutionsMax) {
			Solution solution = new Solution(model);
			solution.record();

			int[] edgesValues = new int[molecule.getNbBonds()];

			for (int j = 0; j < molecule.getNbBonds(); j++) {
				edgesValues[j] = solution.getIntVal(edges[j]);
			}

			/*
			 * Computing the curent Kekulé's structure
			 */

			int [][] structure = new int[molecule.getNbCarbons()][molecule.getNbCarbons()];
			for (int i = 0; i < molecule.getNbCarbons() ; i++) {
				for (int j = 0; j < molecule.getNbCarbons() ; j++) {
					if (molecule.getEdgeMatrix()[i][j] == 0)
						structure[i][j] = -1;
					else
						structure[i][j] = 0;
				}
			}
			
			for (int i = 0 ; i < edgesValues.length ; i++) {
				if (edgesValues[i] == 1) {
					Couple<Integer, Integer> couple = indexesEdges[i];
					int u = couple.getX();
					int v = couple.getY();
					structure[u][v] = 1;
					structure[v][u] = 1;
				}
			}
			
			structures.add(structure);
			nbSolutions ++;
		}

		return structures;
	}

	public static ArrayList<ClarCoverSolution> solve(Benzenoid molecule) {

		Model model = new Model("Clar Cover");

		BoolVar[] circles = new BoolVar[molecule.getNbHexagons()];
		BoolVar[] bonds = new BoolVar[molecule.getNbBonds()];
		BoolVar[] singleElectrons = new BoolVar[molecule.getNbCarbons()];

		BoolVar[][] bondsMatrix = new BoolVar[molecule.getNbCarbons()][molecule.getNbCarbons()];

		IntVar nbCircles = model.intVar("nb_circles", 0, molecule.getNbHexagons());
		IntVar nbSingleElectrons = model.intVar("nb_single_electrons", 0, 2);

		for (int i = 0; i < molecule.getNbHexagons(); i++)
			circles[i] = model.boolVar("circle[" + i + "]");

		int index = 0;
		for (int i = 0; i < molecule.getNbCarbons(); i++) {
			for (int j = (i + 1); j < molecule.getNbCarbons(); j++) {
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
			int[][] bondsInt = new int[molecule.getNbCarbons()][molecule.getNbCarbons()];
			int[] singleElectronsInt = new int[molecule.getNbCarbons()];

			for (int i = 0; i < circles.length; i++)
				circlesInt[i] = solution.getIntVal(circles[i]);

			for (int i = 0; i < molecule.getNbCarbons(); i++) {

				singleElectronsInt[i] = solution.getIntVal(singleElectrons[i]);

				for (int j = (i + 1); j < molecule.getNbCarbons(); j++) {
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
