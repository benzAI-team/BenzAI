package generator.patterns;

import benzenoid.Node;
import utils.HexNeighborhood;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Pattern {
	private final int nbNodes;
	private final int order;
	private final int[][] matrix;

	private final PatternLabel [] labels;
	private final Node[] nodesRefs;
	private final int[][] neighborGraph;
	private final Node center;
	private final PatternFileWriter patternFileWriter = new PatternFileWriter(this);
	private PatternOccurrences patternOccurrences;

	public Pattern(int[][] matrix, PatternLabel[] labels, Node[] nodesRefs, Node center,
				   int[][] neighborGraph, int order) {
		nbNodes = nodesRefs.length;
		this.matrix = matrix;
		this.labels = labels;
		this.nodesRefs = nodesRefs;
		this.center = center;
		this.neighborGraph = neighborGraph;
		this.order = order;
		this.patternOccurrences = null;
	}

	public void export(File file) throws IOException {
		patternFileWriter.export(file);
	}

	private Pattern buildPattern(int[][] neighborGraph, PatternLabel[] labels, int order) {
		int nbNodes = neighborGraph.length;
		int[][] adjacencyMatrix = buildAdjacencyMatrix(neighborGraph, nbNodes);
		Node[] nodesRefs = buildNodesRefs(neighborGraph, nbNodes);
		return new Pattern(adjacencyMatrix, labels, nodesRefs, null, neighborGraph, order);
	}

	private static int[][] buildAdjacencyMatrix(int[][] neighborGraph, int nbNodes) {
		int[][] adjacencyMatrix = new int[nbNodes][nbNodes];
		for (int hexagonIndex1 = 0; hexagonIndex1 < neighborGraph.length; hexagonIndex1++) {
			for (HexNeighborhood neighbor : HexNeighborhood.values()) {
				int hexagonIndex2 = neighborGraph[hexagonIndex1][neighbor.getIndex()];
				if (hexagonIndex2 != -1) {
					adjacencyMatrix[hexagonIndex1][hexagonIndex2] = 1;
					adjacencyMatrix[hexagonIndex2][hexagonIndex1] = 1;
				}
			}
		}
		return adjacencyMatrix;
	}

	private static Node[] buildNodesRefs(int[][] neighborGraph, int nbNodes) {
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
		return nodesRefs;
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
			if (pattern.getLabel(i) == PatternLabel.POSITIVE)
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
						if (pattern.getLabel(node.getIndex()) == PatternLabel.POSITIVE) { // If node is a positive hexagon

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

	/**
	 * Computes the shortest path length between two nodes in the graph.
	 *
	 * @param a The starting node.
	 * @param b The target node.
	 * @return The length of the shortest path between nodes a and b.
	 */
	private int shortestPath(int a, int b) {
		if(a == b) return 0;

		boolean[] visited = new boolean[nbNodes];
		visited[a] = true;

		ArrayList<Integer> neighbors1 = new ArrayList<>();
		ArrayList<Integer> neighbors2 = new ArrayList<>();

		for (int neighbor : this.getNeighborGraph()[a]) {
			if (neighbor >= 0) {
				neighbors1.add(neighbor);
				visited[neighbor] = true;
			}
		}

		int len = 1;

		while (!neighbors1.contains(b)) {
			len ++;

			for (int c : neighbors1) {
				int[] cNeighborGraph = this.getNeighborGraph()[c];
				for (int neighbor : cNeighborGraph) {
					if (neighbor >= 0 && !visited[neighbor]){
						if (neighbor == b) return len;
						neighbors2.add(neighbor);
						visited[neighbor] = true;
					}
				}
			}

			neighbors1 = new ArrayList<>(neighbors2);
			neighbors2.clear();
		}

		return len;
	}

	private int gridDistance(int nodeIndex1, int nodeIndex2){
		Node node1 = nodesRefs[nodeIndex1];
		Node node2 = nodesRefs[nodeIndex2];
		int x1 = node1.getX();
		int y1 = node1.getY();
		int x2 = node2.getX();
		int y2 = node2.getY();
		if(x1 <= x2 && y1 <= y2 || x1 >= x2 && y1 >= y2)
			return Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2));
		else
			return Math.abs(x1 - x2) + Math.abs(y1 - y2);
	}

	public int computeGridDiameter(){
		int diameter = -1;
		for (int i = 0; i < nbNodes; i++)
			for (int j = i + 1; j < nbNodes ; j++)
				if(getLabel(i) == PatternLabel.POSITIVE && getLabel(j) == PatternLabel.POSITIVE)
					diameter = Math.max(gridDistance(i ,j), diameter);
		return diameter;
	}
	/**
	 * Computes the diameter of the graph, which is the longest shortest path length
	 * among all pairs of nodes in the graph.
	 *
	 * @return The diameter of the graph.
	 */
	public int getDiameter() {
		int diameter = -1;

		for (int i = 0; i < nbNodes; i++) {
			for (int j = i + 1; j < nbNodes ; j++) {
				int len = this.shortestPath(i, j);
				System.out.printf("%2d <--> %2d : %2d | ", i, j, len);
				diameter = Math.max(diameter, len);
			}
			System.out.println();
		}
		return diameter;
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
		Pattern p = PatternFileImport.importPattern(new File("src/main/java/expe/patterns/test_pattern3"));
		int diameter = p.getDiameter();
		System.out.printf("diameter : %d%n", diameter);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Node node : nodesRefs)
			builder.append(node.toString());
		return builder.toString();
	}
	public int getNbNodes() {
		return nbNodes;
	}

	public int getNbPositiveNodes() {
		int nbPositiveHexagons = 0;
		for (int i = 0; i < this.getNbNodes(); i++)
			if (this.getLabel(i) == PatternLabel.POSITIVE)
				nbPositiveHexagons++;
		return nbPositiveHexagons;
	}

	public int getOrder() {
		return order;
	}

	public int[][] getMatrix() {
		return matrix;
	}

	public PatternLabel getLabel(int index) {
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

	/*
	 * Methods
	 */

	public Node getCenter() {
		return center;
	}

	public PatternLabel[] getLabels() {
		return labels;
	}

	public void setPatternOccurrences(PatternOccurrences patternOccurrences) {
		this.patternOccurrences = patternOccurrences;
	}

	public PatternOccurrences getPatternOccurrences() {
		return patternOccurrences;
	}
}
