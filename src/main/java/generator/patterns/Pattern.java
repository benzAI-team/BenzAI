package generator.patterns;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import molecules.Node;
import utils.RelativeMatrix;

public class Pattern {

	/*
	 * Label : 1 = neutral, 2 = positive, 3 = negative
	 */

	private int nbNodes;
	private int order;
	private int[][] matrix;
	private int[] labels;
	private Node[] nodesRefs;
	private RelativeMatrix relativeMatrix;
	private int[][] neighborGraph;

	private Node center;

	public Pattern(int[][] matrix, int[] labels, Node[] nodesRefs, RelativeMatrix relativeMatrix, Node center,
			int[][] neighborGraph, int order) {
		nbNodes = nodesRefs.length;
		this.matrix = matrix;
		this.labels = labels;
		this.nodesRefs = nodesRefs;
		this.relativeMatrix = relativeMatrix;
		this.center = center;
		this.neighborGraph = neighborGraph;
		this.order = order;
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

	public void setOrder(int order) {
		this.order = order;
	}

	public int[][] getMatrix() {
		return matrix;
	}

	public int getEdge(int i, int j) {
		return matrix[i][j];
	}

	public int getLabel(int index) {
		return labels[index];
	}

	public Node getNode(int index) {
		return nodesRefs[index];
	}

	public Node getNode(int x, int y) {
		return nodesRefs[relativeMatrix.get(x, y)];
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

	public void export(File file) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		writer.write("DEGREE\n");
		writer.write(order + "\n");

		writer.write("MATRIX\n");
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				writer.write(matrix[i][j] + " ");
			}
			writer.write("\n");
		}

		writer.write("LABELS\n");
		for (int i = 0; i < labels.length; i++)
			writer.write(labels[i] + " ");
		writer.write("\n");

		writer.write("NODES\n");
		for (Node node : nodesRefs)
			writer.write(node.getX() + " " + node.getY() + "\n");

		writer.write("CENTER\n");
		if (center != null)
			writer.write(center.getIndex() + "\n");
		else
			writer.write("0\n");

		writer.write("NEIGHBORS\n");
		for (int i = 0; i < neighborGraph.length; i++) {
			for (int j = 0; j < neighborGraph[i].length; j++) {
				writer.write(neighborGraph[i][j] + " ");
			}
			writer.write("\n");
		}

		writer.close();
	}

	public static Pattern importPattern(File file) throws IOException {

		int step = 0;

		ArrayList<String> degreeLines = new ArrayList<String>();
		ArrayList<String> matrixLines = new ArrayList<String>();
		ArrayList<String> labelsLines = new ArrayList<String>();
		ArrayList<String> nodesLines = new ArrayList<String>();
		ArrayList<String> centerLines = new ArrayList<String>();
		ArrayList<String> neighborsLines = new ArrayList<String>();

		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;

		while ((line = reader.readLine()) != null) {

			if (line.equals("DEGREE"))
				step = 0;

			else if (line.equals("MATRIX"))
				step = 1;

			else if (line.equals("LABELS"))
				step = 2;

			else if (line.equals("NODES"))
				step = 3;

			else if (line.equals("CENTER"))
				step = 4;

			else if (line.equals("NEIGHBORS"))
				step = 5;

			else {

				if (step == 0)
					degreeLines.add(line);

				else if (step == 1)
					matrixLines.add(line);

				else if (step == 2)
					labelsLines.add(line);

				else if (step == 3)
					nodesLines.add(line);

				else if (step == 4)
					centerLines.add(line);

				else if (step == 5)
					neighborsLines.add(line);
			}
		}

		reader.close();

		/*
		 * Degree
		 */

		int degree = Integer.parseInt(degreeLines.get(0));

		/*
		 * Matrix
		 */

		int[][] matrix = new int[matrixLines.size()][matrixLines.size()];

		for (int i = 0; i < matrixLines.size(); i++) {

			line = matrixLines.get(i);
			String[] splittedLine = line.split(" ");

			for (int j = 0; j < splittedLine.length; j++)
				matrix[i][j] = Integer.parseInt(splittedLine[j]);
		}

		/*
		 * Labels
		 */

		line = labelsLines.get(0);
		String[] splittedLine = line.split(" ");

		int[] labels = new int[splittedLine.length];
		for (int i = 0; i < labels.length; i++)
			labels[i] = Integer.parseInt(splittedLine[i]);

		/*
		 * Nodes & RelativeMatrix
		 */

		Node[] nodesRefs = new Node[nodesLines.size()];
		RelativeMatrix relativeMatrix = new RelativeMatrix(8 * nodesLines.size() + 1, 16 * nodesLines.size() + 1,
				4 * nodesLines.size(), 8 * nodesLines.size());

		for (int i = 0; i < nodesLines.size(); i++) {

			line = nodesLines.get(i);
			splittedLine = line.split(" ");

			int x = Integer.parseInt(splittedLine[0]);
			int y = Integer.parseInt(splittedLine[1]);

			nodesRefs[i] = new Node(x, y, i);
			relativeMatrix.set(x, y, i);
		}

		/*
		 * Center
		 */

		Node centerNode = nodesRefs[Integer.parseInt(centerLines.get(0))];

		/*
		 * Neighbor graph
		 */

		int[][] neighborGraph = new int[neighborsLines.size()][6];

		for (int i = 0; i < neighborGraph.length; i++) {

			line = neighborsLines.get(i);
			splittedLine = line.split(" ");

			for (int j = 0; j < 6; j++) {
				neighborGraph[i][j] = Integer.parseInt(splittedLine[j]);
			}
		}

		return new Pattern(matrix, labels, nodesRefs, relativeMatrix, centerNode, neighborGraph, degree);
	}

	private Pattern buildPattern(int[][] neighborGraph, int[] labels, int order) {

		int nbNodes = neighborGraph.length;

		/*
		 * Matrix
		 */

		int[][] matrix = new int[nbNodes][nbNodes];

		for (int i = 0; i < neighborGraph.length; i++) {

			for (int j = 0; j < 6; j++) {

				int hexagon = neighborGraph[i][j];

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

		for (int i = 0; i < 6; i++) {

			int neighbor = neighborGraph[0][i];

			if (neighbor != -1) {

				int x = 0;
				int y = 0;

				if (i == 0)
					nodesRefs[neighbor] = new Node(x, y - 1, neighbor);

				else if (i == 1)
					nodesRefs[neighbor] = new Node(x + 1, y, neighbor);

				else if (i == 2)
					nodesRefs[neighbor] = new Node(x + 1, y + 1, neighbor);

				else if (i == 3)
					nodesRefs[neighbor] = new Node(x, y + 1, neighbor);

				else if (i == 4)
					nodesRefs[neighbor] = new Node(x - 1, y, neighbor);

				else
					nodesRefs[neighbor] = new Node(x - 1, y - 1, neighbor);

				candidats.add(neighbor);
			}
		}

		while (candidats.size() > 0) {

			int candidat = candidats.get(0);

			for (int i = 0; i < 6; i++) {

				int neighbor = neighborGraph[candidat][i];

				if (neighbor != -1) {

					if (nodesRefs[neighbor] == null) {

						int x = nodesRefs[candidat].getX();
						int y = nodesRefs[candidat].getY();

						if (i == 0)
							nodesRefs[neighbor] = new Node(x, y - 1, neighbor);

						else if (i == 1)
							nodesRefs[neighbor] = new Node(x + 1, y, neighbor);

						else if (i == 2)
							nodesRefs[neighbor] = new Node(x + 1, y + 1, neighbor);

						else if (i == 3)
							nodesRefs[neighbor] = new Node(x, y + 1, neighbor);

						else if (i == 4)
							nodesRefs[neighbor] = new Node(x - 1, y, neighbor);

						else
							nodesRefs[neighbor] = new Node(x - 1, y - 1, neighbor);

						candidats.add(neighbor);
					}
				}
			}

			candidats.remove(candidats.get(0));
		}

		return new Pattern(matrix, labels, nodesRefs, null, null, neighborGraph, order);
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

			for (int i = 0; i < nbNodes; i++) {
				for (int j = 0; j < 6; j++) {
					newNeighborGraph[i][(j + shift) % 6] = neighborGraph[i][j];
				}
			}

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
			for (int j = 0; j < diameter; j++)
				coordsMatrix[i][j] = -1;

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

	public static int getNbOptimizedCrowns(Pattern pattern) {

		int nbPositiveHexagons = 0;
		for (int i = 0; i < pattern.getNbNodes(); i++)
			if (pattern.getLabel(i) == 2)
				nbPositiveHexagons++;

		int nbCrowns = (int) Math.floor((((double) ((double) nbPositiveHexagons + 1)) / 2.0) + 1.0);

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

							else {
								// BON
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

			if (!found) {
				nbCrowns++;
				ok = true;
			} else {
				nbCrowns--;
				diameter = (2 * nbCrowns) - 1;
				coordsMatrix = coordsMatrix(nbCrowns);
			}

			// }

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
			for (int j = 0; j < 6; j++) {
				newNeighborGraph[i][j] = neighborGraphSymmetry[i][j];
			}
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
		Pattern p = Pattern.importPattern(new File("expe_fragments/triangle.frg"));
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
}
