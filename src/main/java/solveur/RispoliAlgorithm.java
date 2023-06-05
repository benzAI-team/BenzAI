package solveur;

import Jama.Matrix;
import molecules.Benzenoid;
import molecules.SubGraph;
import solveur.LinAlgorithm.PerfectMatchingType;

import java.util.ArrayList;

public enum RispoliAlgorithm {
    ;

    public static SubGraph removeCircuit(Benzenoid molecule, ArrayList<Integer> circuit, PerfectMatchingType type) {

		int nbNodes = molecule.getNbNodes();
		int[][] matrix = molecule.getEdgeMatrix();
		int[] disabledVertices = new int[molecule.getNbNodes()];

		for (Integer i : circuit) {
			disabledVertices[i] = 1;
		}

		int[] degrees = molecule.getDegrees();

		for (int i = 0; i < nbNodes; i++) {
			if (disabledVertices[i] == 1)
				degrees[i] = -1;
			else {
				degrees[i] = 0;
				for (int j = 0; j < nbNodes; j++) {
					if (disabledVertices[j] == 0 && matrix[i][j] == 1)
						degrees[i]++;
				}
			}
		}

		return new SubGraph(matrix, disabledVertices, degrees, type);
	}

	private static int findUncheckedCarbon(int[] checkedCarbons) {
		for (int i = 0; i < checkedCarbons.length; i++) {
			if (checkedCarbons[i] == 0)
				return i;
		}
		return -1;
	}

	public static Matrix buildMatrix(SubGraph subGraph) {

		ArrayList<Integer> lines = new ArrayList<>();
		ArrayList<Integer> columns = new ArrayList<>();

		int[] checkedCarbons = new int[subGraph.getNbNodes()];

		int nbFinalNodes = subGraph.getNbNodes();

		for (int i = 0; i < subGraph.getNbNodes(); i++) {
			if (subGraph.isDisabled(i)) {
				checkedCarbons[i] = 1;
				nbFinalNodes--;
			}
		}

		int nbCheckedCarbons = 0;

		while (nbCheckedCarbons < nbFinalNodes) {

			ArrayList<Integer> candidats = new ArrayList<>();
			int candidat = findUncheckedCarbon(checkedCarbons);

			candidats.add(candidat);
			checkedCarbons[candidat] = 1;
			nbCheckedCarbons++;
			lines.add(candidat);

			int state = 1;

			while (true) {

				ArrayList<Integer> newCandidats = new ArrayList<>();

				for (Integer i : candidats) {
					for (int j = 0; j < subGraph.getNbNodes(); j++) {

						if (subGraph.containsEdge(i, j) && checkedCarbons[j] == 0) {

							checkedCarbons[j] = 1;
							nbCheckedCarbons++;

							if (state == 0)
								lines.add(j);
							else
								columns.add(j);

							newCandidats.add(j);
						}
					}
				}

				if (newCandidats.size() == 0)
					break;

				state = 1 - state;
				candidats = newCandidats;
			}
		}

		if (lines.size() != columns.size()) {
			System.out.print("");
		}

		double[][] matrix = new double[lines.size()][columns.size()];

		for (int i = 0; i < lines.size(); i++) {
			for (int j = 0; j < columns.size(); j++) {

				int u = lines.get(i);
				int v = columns.get(j);

				if (subGraph.containsEdge(u, v)) {
					matrix[i][j] = 1;
				}
			}
		}

		if (matrix.length > 0)
			return new Matrix(matrix);
		else
			return null;
	}
}
