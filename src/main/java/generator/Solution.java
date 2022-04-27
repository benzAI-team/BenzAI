package generator;

import java.util.ArrayList;

import molecules.Node;
import utils.Utils;

public class Solution {

	private int nbHexagons;

	private int[] vertices;
	private int[][] coordsMatrixCoronenoid;
	private Node[] coronenoidNodes;
	private int[][] neighbors;

	public Solution(Node[] coronenoidNodes, int[][] coordsMatrixCoronenoid, int nbHexagons, int[] vertices) {
		this.coronenoidNodes = coronenoidNodes;
		this.coordsMatrixCoronenoid = coordsMatrixCoronenoid;
		this.nbHexagons = nbHexagons;
		this.vertices = vertices;
		buildNeighbors();
		System.out.print("");
	}

	private void buildNeighbors() {

		int diameter = coordsMatrixCoronenoid.length;
		neighbors = new int[vertices.length][6];

		for (int i = 0; i < neighbors.length; i++)
			for (int j = 0; j < 6; j++)
				neighbors[i][j] = -1;

		for (int i = 0; i < vertices.length; i++) {

			int vertex = vertices[i];
			Node node = coronenoidNodes[vertex];
			int x = node.getX();
			int y = node.getY();

			if (x > 0) { // HAUT-DROIT
				int neighbor = coordsMatrixCoronenoid[x - 1][y];
				if (Utils.contains(vertices, neighbor))
					neighbors[i][0] = neighbor;
			}

			if (y + 1 < diameter) { // DROIT
				int neighbor = coordsMatrixCoronenoid[x][y + 1];
				if (Utils.contains(vertices, neighbor))
					neighbors[i][1] = neighbor;
			}

			if (x + 1 < diameter && y + 1 < diameter) { // BAS-DROIT
				int neighbor = coordsMatrixCoronenoid[x + 1][y + 1];
				if (Utils.contains(vertices, neighbor))
					neighbors[i][2] = neighbor;
			}

			if (x + 1 < diameter) { // BAS-GAUCHE
				int neighbor = coordsMatrixCoronenoid[x + 1][y];
				if (Utils.contains(vertices, neighbor))
					neighbors[i][3] = neighbor;
			}

			if (y > 0) { // GAUCHE
				int neighbor = coordsMatrixCoronenoid[x][y - 1];
				if (Utils.contains(vertices, neighbor))
					neighbors[i][4] = neighbor;
			}

			if (x > 0 && y > 0) { // GAUCHE
				int neighbor = coordsMatrixCoronenoid[x - 1][y - 1];
				if (Utils.contains(vertices, neighbor))
					neighbors[i][5] = neighbor;
			}
		}
	}

	public ArrayList<Integer[]> rotations() {

		return null;
	}
}
