package utils;

import java.util.ArrayList;

public class SubMolecule {

	private final int nbNodes;
    private final int nbEdges;
    private final int nbTotalNodes;
	private final int[][] adjacencyMatrix;

	// debug
	private ArrayList<String> edgesNames;

	private ArrayList<ArrayList<Integer>> edgesMatrix;

	public SubMolecule(int nbNodes, int nbEdges, int nbTotalNodes, int[][] adjacencyMatrix) {
		this.nbNodes = nbNodes;
		this.nbEdges = nbEdges;
		this.nbTotalNodes = nbTotalNodes;
		this.adjacencyMatrix = adjacencyMatrix;
		initEdgesMatrix();
	}

	public int getNbNodes() {
		return nbNodes;
	}

	public int getNbEdges() {
		return nbEdges;
	}

	public int getNbTotalNodes() {
		return nbTotalNodes;
	}

	public int[][] getAdjacencyMatrix() {
		return adjacencyMatrix;
	}

	public ArrayList<ArrayList<Integer>> getEdgesMatrix() {
		return edgesMatrix;
	}

	public void initEdgesMatrix() {

		edgesNames = new ArrayList<>();

		edgesMatrix = new ArrayList<>();

		for (int i = 0; i < nbTotalNodes; i++) {
			edgesMatrix.add(new ArrayList<>());
		}

		int edgeIndex = 0;

		for (int i = 0; i < adjacencyMatrix.length; i++) {
			for (int j = (i + 1); j < adjacencyMatrix[i].length; j++) {

				if (adjacencyMatrix[i][j] == 1) {

					edgesNames.add("e_" + i + "_" + j);

					edgesMatrix.get(i).add(edgeIndex);
					edgesMatrix.get(j).add(edgeIndex);
					edgeIndex++;
				}
			}
		}
	}

	public String getEdgeName(int index) {
		return edgesNames.get(index);
	}
}
