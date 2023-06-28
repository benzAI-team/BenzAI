package solveur;

import java.util.ArrayList;

import benzenoid.Benzenoid;
import benzenoid.RBO;
import benzenoid.SubGraph;
import solveur.LinAlgorithm.PerfectMatchingType;

public enum RBOSolver {
    ;

    public static RBO RBO(Benzenoid molecule) {

		double[][] statistics = new double[molecule.getNbCarbons()][molecule.getNbCarbons()];
		double[] RBO = new double[molecule.getNbHexagons()];

		double nbKekuleStructures = molecule.getNbKekuleStructures();

		if (nbKekuleStructures == 0)
			return new RBO(molecule, statistics, RBO);

		for (int i = 0; i < molecule.getNbCarbons(); i++) {
			for (int j = (i + 1); j < molecule.getNbCarbons(); j++) {
				if (molecule.getEdgeMatrix()[i][j] == 1) {

					ArrayList<Integer> toRemove = new ArrayList<>();
					toRemove.add(i);
					toRemove.add(j);
					SubGraph subGraph = RispoliAlgorithm.removeCircuit(molecule, toRemove, PerfectMatchingType.DET);
					double nbPerfectMatchings = subGraph.getNbPerfectMatchings();

					statistics[i][j] += nbPerfectMatchings;
					statistics[j][i] += nbPerfectMatchings;

				}
			}
		}

		for (int i = 0; i < molecule.getNbCarbons(); i++) {
			for (int j = 0; j < molecule.getNbCarbons(); j++) {
				statistics[i][j] = statistics[i][j] / nbKekuleStructures;
			}
		}

		for (int i = 0; i < molecule.getNbHexagons(); i++) {

			double sum = 0;

			int[] hexagon = molecule.getHexagon(i);

			for (int j = 0; j < 6; j++) {
				int u = hexagon[j];
				int v = hexagon[(j + 1) % 6];

				sum += statistics[u][v];
			}

			RBO[i] = sum;
		}

		return new RBO(molecule, statistics, RBO);
	}
}
