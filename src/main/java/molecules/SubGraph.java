package molecules;

import Jama.Matrix;
import solveur.LinAlgorithm.PerfectMatchingType;
import solveur.PerfectMatchingSolver;
import solveur.RispoliAlgorithm;

import java.util.ArrayList;
import java.util.Objects;

public class SubGraph {

	private Benzenoid molecule;
	private final int nbNodes;
	private int nbEdges;
	private final int[][] matrix;
	private final int[] disabledVertices;
	private final int[] degrees;
	private int[] disabledHexagons;
	private int nbDisabledHexagons;
	private double nbPerfectMatching;
	public SubGraph(int[][] matrix, int[] disabledVertices, int[] degrees, PerfectMatchingType perfectMatchingType) {
		this.matrix = matrix;
		this.disabledVertices = disabledVertices;
		this.degrees = degrees;

		nbNodes = matrix.length;
		nbEdges = -1;

		nbPerfectMatching = -1;

		clean();
		switch (perfectMatchingType) {

		case DET:
			computeNbPerfectMatchingsRispoli();
			break;

		case CHOCO:
			computeNbPerfectMatchingsChoco();
			break;

		}

	}

	public SubGraph(Benzenoid molecule, int[][] matrix, int[] disabledVertices, int[] degrees) {

		this.molecule = molecule;
		this.matrix = matrix;
		this.disabledVertices = disabledVertices;
		this.degrees = degrees;

		disabledHexagons = new int[molecule.getNbHexagons()];

		nbNodes = matrix.length;
		nbEdges = -1;

		nbPerfectMatching = -1;

		nbDisabledHexagons = 0;

	}

	/*
	 * Getters & Setters
	 */

	public int getNbNodes() {
		return nbNodes;
	}

	public int[][] getMatrix() {
		return matrix;
	}

	public int[] getDegrees() {
		return degrees;
	}

	public double getNbPerfectMatching() {
		return nbPerfectMatching;
	}

	public int getNbEdges() {

		nbEdges = 0;
		for (int u = 0; u < nbNodes; u++) {
			for (int v = u + 1; v < nbNodes; v++) {
				if (containsEdge(u, v))
					nbEdges++;
			}
		}

		return nbEdges;

	}

	public int getNbEnabledNodes() {

		int nbEnabledNodes = 0;
		for (int i = 0; i < nbNodes; i++) {
			if (!isDisabled(i))
				nbEnabledNodes++;
		}

		return nbEnabledNodes;
	}

	public int getNbDisabledHexagons() {
		return nbDisabledHexagons;
	}

	public int getNbTotalHexagons() {
		return disabledHexagons.length;
	}

	/*
	 * Methods
	 */

	public boolean isDisabled(int carbon) {
		return disabledVertices[carbon] == 1;
	}

	public boolean containsEdge(int u, int v) {
		return matrix[u][v] == 1 && !isDisabled(u) && !isDisabled(v);
	}

	private void clean() {

		ArrayList<Integer> leafes = new ArrayList<>();
		for (int i = 0; i < nbNodes; i++) {
			if (disabledVertices[i] == 0 && degrees[i] == 1)
				leafes.add(i);
		}

		while (leafes.size() > 0) {

			int leaf = leafes.get(0);

			ArrayList<Integer> candidats = new ArrayList<>();
			candidats.add(leaf);

			if (disabledVertices[leaf] == 0) {

				while (candidats.size() > 0) {

					int candidat = candidats.get(0);

					int neighbor = -1;
					for (int i = 0; i < nbNodes; i++) {
						if (containsEdge(candidat, i)) {
							neighbor = i;
						}
					}

					disabledVertices[candidat] = 1;

					for (int i = 0; i < nbNodes; i++) {

						if (containsEdge(neighbor, i)) {

							degrees[i]--;
							if (degrees[i] == 1)
								candidats.add(i);

							else if (degrees[i] == 0) {
								nbPerfectMatching = 0;
								candidats.clear();
								leafes.clear();
							}

						}
					}

					disabledVertices[neighbor] = 1;

					if (candidats.size() > 0)
						candidats.remove(candidats.get(0));

				}
			}

			if (leafes.size() > 0)
				leafes.remove(leafes.get(0));
		}
	}

	private void computeNbPerfectMatchingsChoco() {
		if (nbPerfectMatching != 0) {
			nbPerfectMatching = PerfectMatchingSolver.computeNbPerfectMatchings(this);
		}
	}

	private void computeNbPerfectMatchingsRispoli() {
		if (nbPerfectMatching != 0) {
			Matrix rispoliMatrix = RispoliAlgorithm.buildMatrix(this);

			if (rispoliMatrix != null) {

				int width = rispoliMatrix.getRowDimension();
				int height = rispoliMatrix.getColumnDimension();

				if (width == height)
					nbPerfectMatching = Math.round(Math.abs(rispoliMatrix.det()));
				else
					nbPerfectMatching = 0;

			} else
				nbPerfectMatching = 1;
		}
	}

	public static double nbKekuleStructures(SubGraph subGraph) {

		if (subGraph.getNbDisabledHexagons() == subGraph.getNbTotalHexagons())
			return 0;

		Matrix rispoliMatrix = RispoliAlgorithm.buildMatrix(subGraph);

		if (rispoliMatrix != null) {

			int width = rispoliMatrix.getRowDimension();
			int height = rispoliMatrix.getColumnDimension();

			if (width == height)
				return Math.round(Math.abs(rispoliMatrix.det()));
			else
				return 0;

		} else
			return 1;
	}

	public void disableHexagon(int hexagonIndex) {

		disabledHexagons[hexagonIndex] = 1;
		nbDisabledHexagons++;

		int[] hexagon = molecule.getHexagon(hexagonIndex);

		for (Integer u : hexagon) {

			boolean disableVertex = true;
			for (int i = 0; i < molecule.getNbHexagons(); i++) {
				if (i != hexagonIndex) {
					if (disabledHexagons[i] == 0) {
						int[] hexagon2 = molecule.getHexagon(i);
						for (Integer v : hexagon2) {
							if (Objects.equals(u, v)) {
								disableVertex = false;
								break;
							}
						}

						if (!disableVertex)
							break;
					}
				}
			}

			if (disableVertex)
				disabledVertices[u] = 1;
		}
	}

	public void enableHexagon(int hexagonIndex) {

		disabledHexagons[hexagonIndex] = 0;
		nbDisabledHexagons--;

		int[] hexagon = molecule.getHexagon(hexagonIndex);

		for (Integer u : hexagon) {
			disabledVertices[u] = 0;
		}
	}

	public boolean isHexagonDisabled(int hexagon) {
		return disabledHexagons[hexagon] == 1;
	}
}
