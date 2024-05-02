package solveur;

import benzenoid.Benzenoid;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.nary.cnf.LogOp;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import solution.ClarCoverSolution;
import utils.Couple;

import java.util.ArrayList;
import java.util.List;

public enum ClarCoverForcedCirclesSolver {
    ;

    public static ArrayList<ClarCoverSolution> solve(Benzenoid molecule, int nbFixedCircles) {

		Model model = new Model("Clar Cover");

		BoolVar[] circles = new BoolVar[molecule.getNbHexagons()];
		BoolVar[] bonds = new BoolVar[molecule.getNbBonds()];
		BoolVar[] singleElectrons = new BoolVar[molecule.getNbCarbons()];

		BoolVar[][] bondsMatrix = new BoolVar[molecule.getNbCarbons()][molecule.getNbCarbons()];

		IntVar nbCircles = model.intVar("nb_circles", 0, molecule.getNbHexagons());
		IntVar nbSingleElectrons = model.intVar("nb_single_electrons", 0, molecule.getNbHexagons());

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

		// Contrainte : deux radicaux ne doivent pas se jouxter (pour chaque carbone, s'il est un radical, ses voisins ne le sont pas)
		for (int i = 0; i < molecule.getNbCarbons(); i++)
			for (int j = (i + 1); j < molecule.getNbCarbons(); j++)
				if (molecule.edgeExists(i, j)) {
					model.addClauses(LogOp.nand(singleElectrons[i], singleElectrons[j]));
					//System.out.println(i + "-" + j);
				}

		
		model.sum(circles, "=", nbCircles).post();
		model.sum(singleElectrons, "=", nbSingleElectrons).post();
		model.arithm(nbCircles, "=", nbFixedCircles).post(); //SEULE DIFFERENCE AVEC ClarCoverSolver !!!

		int ub = -100 * molecule.getNbHexagons();

		IntVar OBJ = model.intVar("objectif", ub, 999);
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
