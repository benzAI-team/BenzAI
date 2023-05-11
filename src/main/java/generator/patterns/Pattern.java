package generator.patterns;

import molecules.Node;
import utils.HexNeighborhood;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Pattern {

	/*
	 * Label : 1 = neutral, 2 = positive, 3 = negative
	 */

	private final int nbNodes;
	private final int order;
	private final int[][] matrix;
	private final int[] labels;
	private final Node[] nodesRefs;
	private final int[][] neighborGraph;
	private final Node center;
	private final PatternFileWriter patternFileWriter = new PatternFileWriter(this);

	public Pattern(int[][] matrix, int[] labels, Node[] nodesRefs, Node center,
				   int[][] neighborGraph, int order) {
		nbNodes = nodesRefs.length;
		this.matrix = matrix;
		this.labels = labels;
		this.nodesRefs = nodesRefs;
		this.center = center;
		this.neighborGraph = neighborGraph;
		this.order = order;
	}

	/***
	 * export pattern in the given file
	 */
	public void export(File file) throws IOException {
		patternFileWriter.export(file);
	}

	private Pattern buildPattern(int[][] neighborGraph, int[] labels, int order) {
		int nbNodes = neighborGraph.length;
		int[][] matrix = new int[nbNodes][nbNodes];

		for (int i = 0; i < neighborGraph.length; i++) {
			for (HexNeighborhood neighbor : HexNeighborhood.values()) {
				int hexagon = neighborGraph[i][neighbor.getIndex()];
				if (hexagon != -1) {
					matrix[i][hexagon] = 1;
					matrix[hexagon][i] = 1;
				}
			}
		}

		/*
		 * Nodes refs
		 */

		Node[] nodesRefs = new Node[nbNodes];
		nodesRefs[0] = new Node(0, 0, 0);

		ArrayList<Integer> candidats = new ArrayList<>();

		for (HexNeighborhood neighbor : HexNeighborhood.values()) {
			int neighborIndex = neighborGraph[0][neighbor.getIndex()];
			if (neighborIndex != -1) {
				int x = 0;
				int y = 0;
				nodesRefs[neighborIndex] = new Node(x + neighbor.dx(), y + neighbor.dy(), neighborIndex);
				candidats.add(neighborIndex);
			}
		}

		while (!candidats.isEmpty()) {

			int candidatIndex = candidats.get(0);

			for (HexNeighborhood neighbor : HexNeighborhood.values()) {

				int neighborIndex = neighborGraph[candidatIndex][neighbor.getIndex()];

				if (neighborIndex != -1 && nodesRefs[neighborIndex] == null) {
					int x = nodesRefs[candidatIndex].getX();
					int y = nodesRefs[candidatIndex].getY();
					nodesRefs[neighborIndex] = new Node(x + neighbor.dx(), y + neighbor.dy());
					candidats.add(neighborIndex);
				}
			}
			candidats.remove(candidats.get(0));
		}

		return new Pattern(matrix, labels, nodesRefs, null, neighborGraph, order);
	}

	public ArrayList<Pattern> computeRotations() {

		ArrayList<Pattern> patterns = new ArrayList<>();
		patterns.add(this);

		int[][] neighborGraph = this.getNeighborGraph();

		/*
		 * Computing rotations
		 */

		for (int shift = 1; shift < 6; shift++) {
			int[][] newNeighborGraph = new int[nbNodes][6];
			for (int i = 0; i < nbNodes; i++)
				for (HexNeighborhood neighbor : HexNeighborhood.values())
					newNeighborGraph[i][(neighbor.getIndex() + shift) % 6] = neighborGraph[i][neighbor.getIndex()];
			Pattern pattern = buildPattern(newNeighborGraph, labels, order);
			patterns.add(pattern);
		}

		/*
		 * Computing axial symmetries
		 */

		int[][] neighborGraphSymmetry = new int[nbNodes][6];

		for (int i = 0; i < nbNodes; i++) {
			neighborGraphSymmetry[i][0] = this.getNeighbor(i, 2);
			neighborGraphSymmetry[i][1] = this.getNeighbor(i, 1);
			neighborGraphSymmetry[i][2] = this.getNeighbor(i, 0);
			neighborGraphSymmetry[i][3] = this.getNeighbor(i, 5);
			neighborGraphSymmetry[i][4] = this.getNeighbor(i, 4);
			neighborGraphSymmetry[i][5] = this.getNeighbor(i, 3);
		}

		for (int shift = 0; shift < 6; shift++) {

			int[][] newNeighborGraph = new int[nbNodes][6];

			for (int i = 0; i < nbNodes; i++) {
				for (int j = 0; j < 6; j++) {
					newNeighborGraph[i][(j + shift) % 6] = neighborGraphSymmetry[i][j];
				}
			}

			Pattern pattern = buildPattern(newNeighborGraph, labels, order);
			patterns.add(pattern);
		}

		return patterns;
	}

	public Node[] getNodesRefs() {
		return nodesRefs;
	}

	private static int[][] coordsMatrix(int nbCrowns) {

		int diameter = (2 * nbCrowns) - 1;
		int[][] coordsMatrix = new int[diameter][diameter];

		for (int i = 0; i < diameter; i++)
			Arrays.fill(coordsMatrix[i], -1);

		int index = 0;
		int m = (diameter - 1) / 2;

		int shift = diameter - nbCrowns;

		for (int i = 0; i < m; i++) {

			for (int j = 0; j < diameter - shift; j++) {
				coordsMatrix[i][j] = index;
				index++;
			}

			for (int j = diameter - shift; j < diameter; j++)
				index++;

			shift--;
		}

		for (int j = 0; j < diameter; j++) {
			coordsMatrix[m][j] = index;
			index++;
		}

		shift = 1;

		for (int i = m + 1; i < diameter; i++) {

			for (int j = 0; j < shift; j++)
				index++;

			for (int j = shift; j < diameter; j++) {
				coordsMatrix[i][j] = index;
				index++;
			}

			shift++;
		}

		return coordsMatrix;
	}

	private static int getNbOptimizedCrowns(Pattern pattern) {

		int nbPositiveHexagons = 0;
		for (int i = 0; i < pattern.getNbNodes(); i++)
			if (pattern.getLabel(i) == 2)
				nbPositiveHexagons++;

		int nbCrowns = (int) Math.floor((((double) nbPositiveHexagons + 1) / 2.0) + 1.0);

		if (nbPositiveHexagons % 2 == 1)
			nbCrowns--;

		int diameter = (2 * nbCrowns) - 1;

		int[][] coordsMatrix = coordsMatrix(nbCrowns);

		// ArrayList<Pattern> rotations = pattern.computeRotations();
		boolean ok = false;

		while (!ok) {

			// for (Pattern rotation : rotations) {

			boolean found = false;

			for (int xShift = -1 * diameter; xShift < diameter; xShift++) {
				for (int yShift = -1 * diameter; yShift < diameter; yShift++) {

					boolean embedded = true;

					for (Node node : pattern.getNodesRefs()) {
						if (pattern.getLabel(node.getIndex()) == 2) { // If node is a positive hexagon

							int x = node.getX() + xShift;
							int y = node.getY() + yShift;

							if (x >= diameter || y >= diameter || x < 0 || y < 0) {
								embedded = false;
							}

							else if (coordsMatrix[x][y] == -1) {
								embedded = false;
							}
						}
					}

					if (embedded) {
						found = true;
						break;
					}
				}

				if (found)
					break;
			}

			if (found) {
				nbCrowns--;
				diameter = (2 * nbCrowns) - 1;
				coordsMatrix = coordsMatrix(nbCrowns);
			} else {
				nbCrowns++;
				ok = true;
			}
		}

		return nbCrowns;
	}

	public Pattern mirror() {

		int[][] neighborGraphSymmetry = new int[nbNodes][6];

		for (int i = 0; i < nbNodes; i++) {
			neighborGraphSymmetry[i][0] = this.getNeighbor(i, 2);
			neighborGraphSymmetry[i][1] = this.getNeighbor(i, 1);
			neighborGraphSymmetry[i][2] = this.getNeighbor(i, 0);
			neighborGraphSymmetry[i][3] = this.getNeighbor(i, 5);
			neighborGraphSymmetry[i][4] = this.getNeighbor(i, 4);
			neighborGraphSymmetry[i][5] = this.getNeighbor(i, 3);
		}

		int[][] newNeighborGraph = new int[nbNodes][6];

		for (int i = 0; i < nbNodes; i++) {
			System.arraycopy(neighborGraphSymmetry[i], 0, newNeighborGraph[i], 0, 6);
		}

		return buildPattern(newNeighborGraph, labels, order);
	}

	public int xMin() {

		int xMin = Integer.MAX_VALUE;

		for (Node node : nodesRefs) {
			if (node != null && node.getX() < xMin)
				xMin = node.getX();
		}

		return xMin;
	}

	public int xMax() {
		int xMax = Integer.MIN_VALUE;

		for (Node node : nodesRefs) {
			if (node != null && node.getX() > xMax)
				xMax = node.getX();
		}

		return xMax;
	}

	public int yMin() {

		int yMin = Integer.MAX_VALUE;

		for (Node node : nodesRefs) {
			if (node != null && node.getY() < yMin)
				yMin = node.getY();
		}

		return yMin;
	}

	public int yMax() {

		int yMax = Integer.MIN_VALUE;

		for (Node node : nodesRefs) {
			if (node != null && node.getY() > yMax)
				yMax = node.getY();
		}

		return yMax;
	}

	public static void main(String[] args) throws IOException {
		Pattern p = PatternFileImport.importPattern(new File("expe_fragments/triangle.frg"));
		int optNbCrowns = Pattern.getNbOptimizedCrowns(p);
		System.out.println(optNbCrowns);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Node node : nodesRefs)
			builder.append(node.toString());
		return builder.toString();
	}
	/*
	 * Getters and setters
	 */

	public int getNbNodes() {
		return nbNodes;
	}

	public int getOrder() {
		return order;
	}

	public int[][] getMatrix() {
		return matrix;
	}

	public int getLabel(int index) {
		return labels[index];
	}

	public Node getNode(int index) {
		return nodesRefs[index];
	}

	public int getNeighbor(int i, int j) {
		return neighborGraph[i][j];
	}

	public int[][] getNeighborGraph() {
		return neighborGraph;
	}

	public Node getCenter() {
		return center;
	}
	public int[] getLabels() {
		return labels;
	}
}
