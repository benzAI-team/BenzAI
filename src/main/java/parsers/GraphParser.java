package parsers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import molecules.Molecule;
import molecules.Node;
import molecules.UndirPonderateGraph;
import utils.RelativeMatrix;

public class GraphParser {

	public static boolean isCommentary(String[] splittedLine) {
		return (splittedLine[0].equals("c"));
	}

	public static boolean isEdge(String[] splittedLine) {
		return (splittedLine[0].equals("e"));
	}

	public static boolean isHexagon(String[] splittedLine) {
		return (splittedLine[0].equals("h"));
	}

	public static Molecule parseBenzenoidCode(String code) throws IOException {

		String[] splittedCode = code.split(Pattern.quote("-"));

		int nbHexagons = splittedCode.length;

		int nbCrowns = (int) Math.floor((((double) ((double) nbHexagons + 1)) / 2.0) + 1.0);

		if (nbHexagons % 2 == 1)
			nbCrowns--;

		int diameter = (2 * nbCrowns) - 1;

		ArrayList<Integer> verticesSolution = new ArrayList<>();

		for (int i = 0; i < diameter * diameter; i++)
			verticesSolution.add(0);

		for (String hexagonStr : splittedCode) {

			int hexagon = Integer.parseInt(hexagonStr);
			verticesSolution.set(hexagon, 1);
		}

		String graphFilename = "tmp.graph";
		String graphCoordFilename = "tmp.graph_coord";

		GraphFileBuilder graphBuilder = new GraphFileBuilder(verticesSolution, graphFilename, nbCrowns);
		graphBuilder.buildGraphFile();

		GraphCoordFileBuilder graphCoordBuilder = new GraphCoordFileBuilder(graphFilename, graphCoordFilename);
		graphCoordBuilder.convertInstance();

		Molecule molecule = GraphParser.parseUndirectedGraph(graphCoordFilename, null, false);

		File file = new File("tmp.graph");
		file.delete();

		file = new File("tmp.graph_coord");
		file.delete();

		molecule.setVerticesSolutions(verticesSolution);

		return molecule;
	}

	public static UndirPonderateGraph exportSolutionToPonderateGraph(Molecule graph, int[] edgesValues) {

		int[][] adjacencyMatrix = new int[graph.getAdjacencyMatrix().length][graph.getAdjacencyMatrix()[0].length];
		ArrayList<String> edgesString = new ArrayList<String>();

		for (int i = 0; i < adjacencyMatrix.length; i++) {
			for (int j = 0; j < adjacencyMatrix[i].length; j++) {
				adjacencyMatrix[i][j] = -1;
			}
		}

		for (int i = 0; i < edgesValues.length; i++) {
			String edgeStr = graph.getEdgesString().get(i);
			int edgeValue = edgesValues[i];

			String[] splittedString = edgeStr.split(" ");
			String[] uStr = splittedString[1].split(Pattern.quote("_"));
			String[] vStr = splittedString[2].split(Pattern.quote("_"));

			int x1 = Integer.parseInt(uStr[0]);
			int y1 = Integer.parseInt(uStr[1]);
			int x2 = Integer.parseInt(vStr[0]);
			int y2 = Integer.parseInt(vStr[1]);

			int uIndex = graph.getCoords().get(x1, y1);
			int vIndex = graph.getCoords().get(x2, y2);

			adjacencyMatrix[uIndex][vIndex] = edgeValue;
			adjacencyMatrix[vIndex][uIndex] = edgeValue;
			edgesString.add(edgeStr + " " + edgeValue);
		}

		return new UndirPonderateGraph(graph.getNbNodes(), graph.getNbEdges(), graph.getNbHexagons(),
				graph.getEdgeMatrix(), adjacencyMatrix, edgesString, graph.getHexagonsString(), graph.getNodesRefs(),
				graph.getNodesMem(), graph.getCoords(), graph.getMaxIndex());

	}

	public static void exportSolutionToPonderateGraphFile(String filename, Molecule graph, int[] edgesValues) {

		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(new File(filename)));

			w.write("p DIMACS " + graph.getNbNodes() + " " + graph.getNbEdges() + " " + graph.getNbHexagons() + "\n");

			ArrayList<String> edges = graph.getEdgesString();
			ArrayList<String> hexagons = graph.getHexagonsString();

			for (int i = 0; i < edges.size(); i++)
				w.write(edges.get(i) + " " + edgesValues[i] + "\n");

			for (String hexagon : hexagons)
				w.write(hexagon + "\n");

			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static UndirPonderateGraph parseUndirPonderateGraph(String inputFileName, String fileWithNoCoords) {

		try {
			BufferedReader r = new BufferedReader(new FileReader(new File(inputFileName)));

			String line = null;
			boolean firstLine = true;

			int nbNodes = 0, nbEdges = 0, nbHexagons = 0;

			ArrayList<String> edgesStrings = new ArrayList<String>();
			ArrayList<String> hexagonsStrings = new ArrayList<String>();
			ArrayList<ArrayList<Integer>> edgesMatrix = new ArrayList<ArrayList<Integer>>();

			int[][] adjacencyMatrix = null;
			Node[] nodes = null;

			RelativeMatrix nodesCoord = null;

			int nodeIndex = 0;
			int edgeIndex = 0;

			while ((line = r.readLine()) != null) {
				String[] splittedLine = line.split(" ");

				if (!isCommentary(splittedLine)) {

					if (firstLine) {

						firstLine = false;
						nbNodes = Integer.parseInt(splittedLine[2]);
						nbEdges = Integer.parseInt(splittedLine[3]);
						nbHexagons = Integer.parseInt(splittedLine[4]);

						nodes = new Node[nbNodes];
						nodesCoord = new RelativeMatrix(8 * nbHexagons + 1, 16 * nbHexagons + 1, 4 * nbHexagons,
								8 * nbHexagons);

						for (int i = 0; i < nbNodes; i++) {
							edgesMatrix.add(new ArrayList<Integer>());
						}

						adjacencyMatrix = new int[nbNodes][nbNodes];

						for (int i = 0; i < nbNodes; i++) {
							for (int j = 0; j < nbNodes; j++) {
								adjacencyMatrix[i][j] = -1;
							}
						}

					}

					else {

						if (isEdge(splittedLine)) {

							String uStr = splittedLine[1];
							String vStr = splittedLine[2];
							int value = Integer.parseInt(splittedLine[3]);

							String[] uSplit = uStr.split(Pattern.quote("_"));
							String[] vSplit = vStr.split(Pattern.quote("_"));

							int x1 = Integer.parseInt(uSplit[0]);
							int y1 = Integer.parseInt(uSplit[1]);
							int x2 = Integer.parseInt(vSplit[0]);
							int y2 = Integer.parseInt(vSplit[1]);

							int u, v;

							// Testing if nodes are already created
							if (nodesCoord.get(x1, y1) == -1) {
								if (fileWithNoCoords == null) {
									nodesCoord.set(x1, y1, nodeIndex);
									u = nodeIndex;
									nodes[nodeIndex] = new Node(x1, y1, nodeIndex);
									nodeIndex++;
								} else {
									u = nodesCoord.get(x1, y1);
									nodes[u] = new Node(x1, y1, u);
								}
							} else {
								u = nodesCoord.get(x1, y1);
							}

							if (nodesCoord.get(x2, y2) == -1) {
								if (fileWithNoCoords == null) {
									nodesCoord.set(x2, y2, nodeIndex);
									v = nodeIndex;
									nodes[nodeIndex] = new Node(x2, y2, nodeIndex);
									nodeIndex++;
								} else {
									v = nodesCoord.get(x2, y2);
									nodes[v] = new Node(x2, y2, v);
								}
							} else {
								v = nodesCoord.get(x2, y2);
							}

							edgesMatrix.get(u).add(edgeIndex);
							edgesMatrix.get(v).add(edgeIndex);
							adjacencyMatrix[u][v] = value;
							adjacencyMatrix[v][u] = value;
							edgesStrings.add(line + " " + value);
						}

						if (isHexagon(splittedLine)) {
							hexagonsStrings.add(line);
						}
					}

				}
			}

			r.close();

			return new UndirPonderateGraph(nbNodes, nbEdges, nbHexagons, edgesMatrix, adjacencyMatrix, edgesStrings,
					hexagonsStrings, nodes, null, null, -1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Molecule parseUndirectedGraph(File file) {
		return parseUndirectedGraph(file.getAbsolutePath(), null, false);
	}

	public static Molecule parseUndirectedGraph(String inputFileName, String fileWithNoCoordsName, boolean verbose) {

		RelativeMatrix nodesMem = null;

		try {
			BufferedReader bufferedReaderInputFile = new BufferedReader(new FileReader(new File(inputFileName)));

			if (fileWithNoCoordsName != null) {
				ArrayList<String> fileWithNoCoordsLineList = buildFileWithNoCoordsLineList(fileWithNoCoordsName);
				nodesMem = buildRelativeMatrix(bufferedReaderInputFile, fileWithNoCoordsLineList);
			}

			bufferedReaderInputFile = new BufferedReader(new FileReader(new File(inputFileName)));

			String inputFileLine = null;
			boolean isFirstLine = true;

			int nbNodes = 0, nbEdges = 0, nbHexagons = 0;

			ArrayList<String> edgesStrings = new ArrayList<String>();
			ArrayList<String> hexagonsStrings = new ArrayList<String>();
			ArrayList<ArrayList<Integer>> edgesMatrix = new ArrayList<ArrayList<Integer>>();

			int[][] adjacencyMatrix = null;
			Node[] nodes = null;

			RelativeMatrix nodesCoord = null;

			int nodeIndex = 0;
			int edgeIndex = 0;

			isFirstLine = true;

			while ((inputFileLine = bufferedReaderInputFile.readLine()) != null) {
				String[] lineWords = inputFileLine.split(" ");

				if (!isCommentary(lineWords)) {

					if (isFirstLine) {

						isFirstLine = false;
						nbNodes = Integer.parseInt(lineWords[2]);
						nbEdges = Integer.parseInt(lineWords[3]);
						nbHexagons = Integer.parseInt(lineWords[4]);

						nodes = new Node[nbNodes];
						nodesCoord = new RelativeMatrix(8 * nbHexagons + 1, 16 * nbHexagons + 1, 4 * nbHexagons,
								8 * nbHexagons);

						for (int i = 0; i < nbNodes; i++) {
							edgesMatrix.add(new ArrayList<Integer>());
						}

						adjacencyMatrix = new int[nbNodes][nbNodes];

					}

					else {

						if (isEdge(lineWords)) {

							String uStr = lineWords[1];
							String vStr = lineWords[2];

							String[] uSplit = uStr.split(Pattern.quote("_"));
							String[] vSplit = vStr.split(Pattern.quote("_"));

							int x1 = Integer.parseInt(uSplit[0]);
							int y1 = Integer.parseInt(uSplit[1]);
							int x2 = Integer.parseInt(vSplit[0]);
							int y2 = Integer.parseInt(vSplit[1]);

							int u, v;

							// Testing if nodes are already created
							if (nodesCoord.get(x1, y1) == -1) {
								nodesCoord.set(x1, y1, nodeIndex);
								u = nodeIndex;
								nodes[nodeIndex] = new Node(x1, y1, nodeIndex);
								if (verbose)
									System.out.println("nodes[" + nodeIndex + "] = (" + x1 + ", " + y1 + ")");
								nodeIndex++;
							} else {
								u = nodesCoord.get(x1, y1);
							}

							if (nodesCoord.get(x2, y2) == -1) {
								nodesCoord.set(x2, y2, nodeIndex);
								v = nodeIndex;
								nodes[nodeIndex] = new Node(x2, y2, nodeIndex);
								if (verbose)
									System.out.println("nodes[" + nodeIndex + "] = (" + x2 + ", " + y2 + ")");
								nodeIndex++;
							} else {
								v = nodesCoord.get(x2, y2);
							}

							edgesMatrix.get(u).add(edgeIndex);
							edgesMatrix.get(v).add(edgeIndex);
							adjacencyMatrix[u][v] = 1;
							adjacencyMatrix[v][u] = 1;
							edgesStrings.add(inputFileLine);
							edgeIndex++;
						}

						if (isHexagon(lineWords)) {

							hexagonsStrings.add(inputFileLine);
						}
					}

				}
			}

			bufferedReaderInputFile.close();

			int maxValue = nodesMem == null ? -1 : nodesMem.maxValue();
			return new Molecule(nbNodes, nbEdges, nbHexagons, edgesMatrix, adjacencyMatrix, edgesStrings,
					hexagonsStrings, nodes, nodesCoord, nodesMem, maxValue);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static RelativeMatrix buildRelativeMatrix(BufferedReader bufferedReaderInputFile,
			ArrayList<String> fileWithNoCoordsLineList) throws IOException {
		String inputFileLine = null;
		String fileWithNoCoordsLine = null;
		RelativeMatrix nodesMem = null;

		boolean isFirstLine = true;

		int fileWithNoCoordsLineListIndex = 0;
		while ((inputFileLine = bufferedReaderInputFile.readLine()) != null) {

			fileWithNoCoordsLine = fileWithNoCoordsLineList.get(fileWithNoCoordsLineListIndex);
			fileWithNoCoordsLineListIndex++;

			String[] inputFileLineWords = inputFileLine.split(" ");
			String[] fileWithNoCoordsLineWords = fileWithNoCoordsLine.split(" ");

			if (!isCommentary(inputFileLineWords)) {

				if (isFirstLine) {
					isFirstLine = false;

					int nbHexagons = Integer.parseInt(inputFileLineWords[4]);

					nodesMem = new RelativeMatrix(8 * nbHexagons + 1, 16 * nbHexagons + 1, 4 * nbHexagons,
							8 * nbHexagons);
				}

				if (inputFileLineWords[0].equals("h")) {
					for (int i = 1; i < inputFileLineWords.length; i++) {
						String[] ssl1 = inputFileLineWords[i].split(Pattern.quote("_"));

						int x = Integer.parseInt(ssl1[0]);
						int y = Integer.parseInt(ssl1[1]);

						int value = Integer.parseInt(fileWithNoCoordsLineWords[i]);
						nodesMem.set(x, y, value - 1);
					}
				}
			}
		}
		bufferedReaderInputFile.close();
		return nodesMem;
	}

	private static ArrayList<String> buildFileWithNoCoordsLineList(String fileWithNoCoordsName)
			throws FileNotFoundException, IOException {
		BufferedReader bufferedReaderFileWithNoCoords = new BufferedReader(new FileReader(new File(fileWithNoCoordsName)));
		ArrayList<String> lineList = new ArrayList<String>();
		String line = null;

		while ((line = bufferedReaderFileWithNoCoords.readLine()) != null) {
			String[] sl = line.split(" ");
			if (!isCommentary(sl))
				lineList.add(line);
		}

		bufferedReaderFileWithNoCoords.close();
		return lineList;
	}
}
