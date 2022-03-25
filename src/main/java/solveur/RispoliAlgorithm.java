package solveur;

import java.util.ArrayList;

import Jama.Matrix;
import molecules.Molecule;
import molecules.SubGraph;
import parsers.GraphParser;
import solveur.LinAlgorithm.PerfectMatchingType;

public class RispoliAlgorithm {

	public static SubGraph removeCircuit(Molecule molecule, ArrayList<Integer> circuit, PerfectMatchingType type) {

		int nbNodes = molecule.getNbNodes();
		int[][] matrix = molecule.getAdjacencyMatrix();
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

		// System.out.println(lines);
		// System.out.println(columns);

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

	public static void main(String[] args) {

		Molecule molecule = GraphParser
				.parseUndirectedGraph("C:\\Users\\adrie\\Desktop\\molecules_test\\coro_3.graph_coord", null, true);
		ArrayList<Integer> circuit = new ArrayList<>();

// Circuit acene-4
//		circuit.add(10);
//		circuit.add(12);
//		circuit.add(13);
//		circuit.add(11);
//		circuit.add(9);
//		circuit.add(8);

// Circuit coro-2 (OK)
//		circuit.add(0);
//		circuit.add(1);
//		circuit.add(3);
//		circuit.add(5);
//		circuit.add(7);
//		circuit.add(2);
//		
//		circuit.add(6);
//		circuit.add(13);
//		circuit.add(15);
//		circuit.add(8);

//		circuit.add(18);
//		circuit.add(21);
//		circuit.add(23);
//		circuit.add(19);

// Circuit coro-2 bug #1 (OK)
//		circuit.add(3);
//		circuit.add(5);
//		circuit.add(6);
//		circuit.add(8);
//		circuit.add(13);
//		circuit.add(15);

// Circuit coro-2 bug #2 (OK)

//		circuit.add(13);
//		circuit.add(18);
//		circuit.add(21);
//		circuit.add(23);
//		circuit.add(19);
//		circuit.add(15);

// Circuit coro-2 full
//		circuit.add(0);
//		circuit.add(1);
//		circuit.add(4);
//		circuit.add(10);
//		circuit.add(11);
//		circuit.add(12);
//		circuit.add(20);
//		circuit.add(18);
//		circuit.add(21);
//		circuit.add(23);
//		circuit.add(19);
//		circuit.add(22);
//		circuit.add(17);
//		circuit.add(14);
//		circuit.add(16);
//		circuit.add(9);
//		circuit.add(7);
//		circuit.add(2);

//		Circuit coro-3 bug

		for (Integer u : molecule.getHexagons()[16])
			if (!circuit.contains(u))
				circuit.add(u);

		for (Integer u : molecule.getHexagons()[17])
			if (!circuit.contains(u))
				circuit.add(u);

		for (Integer u : molecule.getHexagons()[18])
			if (!circuit.contains(u))
				circuit.add(u);

		SubGraph subGraph = removeCircuit(molecule, circuit, PerfectMatchingType.DET);
		System.out.println(subGraph.getNbPerfectMatchings() + " perfect matchings");
	}
}
