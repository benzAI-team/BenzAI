package generator.patterns;

import molecules.Node;
import utils.HexNeighborhood;
import utils.RelativeMatrix;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writeDegree(writer);
		writeMatrix(writer, "MATRIX\n", matrix);
		writeLabels(writer);
		writeNodes(writer);
		writeCenter(writer);
		writeMatrix(writer, "NEIGHBORS\n", neighborGraph);
		writer.close();
	}

	private void writeCenter(BufferedWriter writer) throws IOException {
		writer.write("CENTER\n");
		if (center != null)
			writer.write(center.getIndex() + "\n");
		else
			writer.write("0\n");
	}

	private void writeNodes(BufferedWriter writer) throws IOException {
		writer.write("NODES\n");
		for (Node node : nodesRefs)
			writer.write(node.getX() + " " + node.getY() + "\n");
	}

	private void writeLabels(BufferedWriter writer) throws IOException {
		writer.write("LABELS\n");
		for (int label : labels) writer.write(label + " ");
		writer.write("\n");
	}

	private void writeDegree(BufferedWriter writer) throws IOException {
		writer.write("DEGREE\n");
		writer.write(order + "\n");
	}

	private void writeMatrix(BufferedWriter writer, String str, int[][] neighborGraph) throws IOException {
		writer.write(str);
		for (int[] ints : neighborGraph) {
			for (int anInt : ints) {
				writer.write(anInt + " ");
			}
			writer.write("\n");
		}
	}

	/***
	 * import pattern from the given file
	 * @return the pattern
	 */
	public static Pattern importPattern(File file) throws IOException {
		ArrayList<String>[] lineArray = readPatternFile(file);
		int degree = getDegree(lineArray);
		int[][] matrix = getMatrix(lineArray);
		int[] labels = getLabels(lineArray);
		Node[] nodesRefs = getNodesRefs(lineArray);
		Node centerNode = getCenterNode(lineArray, nodesRefs);
		int[][] neighborGraph = getNeighborGraph(lineArray);
		return new Pattern(matrix, labels, nodesRefs, centerNode, neighborGraph, degree);
	}

	private static int[][] getNeighborGraph(ArrayList<String>[] lineArray) {
		String[] splittedLine;
		String line;
		ArrayList<String> neighborsLines = lineArray[5];
		int[][] neighborGraph = new int[neighborsLines.size()][6];

		for (int i = 0; i < neighborGraph.length; i++) {

			line = neighborsLines.get(i);
			splittedLine = line.split(" ");

			for (int j = 0; j < 6; j++) {
				neighborGraph[i][j] = Integer.parseInt(splittedLine[j]);
			}
		}
		return neighborGraph;
	}

	private static Node getCenterNode(ArrayList<String>[] lineArray, Node[] nodesRefs) {
		ArrayList<String> centerLines = lineArray[4];
		return nodesRefs[Integer.parseInt(centerLines.get(0))];
	}

	private static Node[] getNodesRefs(ArrayList<String>[] lineArray) {
		String[] splittedLine;
		String line;
		ArrayList<String> nodesLines = lineArray[3];
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
		return nodesRefs;
	}

	private static int[] getLabels(ArrayList<String>[] lineArray) {
		String line;
		ArrayList<String> labelsLines = lineArray[2];
		line = labelsLines.get(0);
		String[] splittedLine = line.split(" ");

		int[] labels = new int[splittedLine.length];
		for (int i = 0; i < labels.length; i++)
			labels[i] = Integer.parseInt(splittedLine[i]);
		return labels;
	}

	private static int[][] getMatrix(ArrayList<String>[] lineArray) {
		String line;
		ArrayList<String> matrixLines = lineArray[1];
		int[][] matrix = new int[matrixLines.size()][matrixLines.size()];

		for (int i = 0; i < matrixLines.size(); i++) {

			line = matrixLines.get(i);
			String[] splittedLine = line.split(" ");

			for (int j = 0; j < splittedLine.length; j++)
				matrix[i][j] = Integer.parseInt(splittedLine[j]);
		}
		return matrix;
	}

	private static int getDegree(ArrayList<String>[] lineArray) {
		ArrayList<String> degreeLines = lineArray[0];
		return Integer.parseInt(degreeLines.get(0));
	}

	private static ArrayList<String>[] readPatternFile(File file) throws IOException {
		ArrayList<String>[] lineArray = new ArrayList[6];
		for(int i = 0 ; i < 6; i++)
			lineArray[i] = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		int step = 0;
		HashMap<String,Integer> lineTypes = new HashMap<>();
		lineTypes.put("DEGREE", 0);
		lineTypes.put("MATRIX", 1);
		lineTypes.put("LABELS", 2);
		lineTypes.put("NODES", 3);
		lineTypes.put("CENTER", 4);
		lineTypes.put("NEIGHBORS", 5);
		while ((line = reader.readLine()) != null)
			if(lineTypes.containsKey(line))
				step = lineTypes.get(line);
			else
				lineArray[step].add(line);
		reader.close();
		return lineArray;
	}

	private Pattern buildPattern(int[][] neighborGraph, int[] labels, int order) {

		int nbNodes = neighborGraph.length;

		/*
		 * Matrix
		 */

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

}
