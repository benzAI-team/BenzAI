package molecules;

import classifier.Irregularity;
import database.models.IRSpectraEntry;
import generator.GeneralModel;
import generator.SolverResults;
import generator.patterns.Pattern;
import generator.patterns.PatternLabel;
import generator.properties.Property;
import generator.properties.model.ModelProperty;
import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.BinaryNumericalExpression;
import http.Post;
import molecules.sort.MoleculeComparator;
import molecules.sort.NbHexagonsComparator;
import parsers.GraphCoordFileBuilder;
import parsers.GraphFileBuilder;
import parsers.GraphParser;
import solution.BenzenoidSolution;
import solution.ClarCoverSolution;
import solveur.Aromaticity;
import solveur.LinAlgorithm;
import solveur.LinAlgorithm.PerfectMatchingType;
import solveur.RBOSolver;
import spectrums.ResultLogFile;
import utils.Couple;
import utils.HexNeighborhood;
import utils.Interval;
import utils.RelativeMatrix;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Benzenoid implements Comparable<Benzenoid> {

	private MoleculeComparator comparator;

	private RelativeMatrix nodesMem; // DEBUG

	private final int nbNodes;
	private final int nbEdges;
	private final int nbHexagons;
	private int nbStraightEdges;
	private int maxIndex;
	private ArrayList<ArrayList<Integer>> edgeLists;
	private final int[][] edgeMatrix;
	private ArrayList<String> edgesString;
	private final ArrayList<String> hexagonsString;
	private final Node[] nodesRefs;
	private final RelativeMatrix coords;
	private final int[][] hexagons;
	private int[][] dualGraph;
	private int[] degrees;

	private Irregularity irregularity;

	private ArrayList<ArrayList<Integer>> hexagonsVertices;

	private int nbHydrogens;

	private String name;

	private String description;

	private ArrayList<Integer> verticesSolutions;

	private Couple<Integer, Integer>[] hexagonsCoords;

	private double nbKekuleStructures = -1;
	private Aromaticity aromaticity;

	private ClarCoverSolution clarCoverSolution;
	private int[][] fixedBonds;
	private int[] fixedCircles;
	private ArrayList<int[][]> kekuleStructures;

	private ArrayList<ClarCoverSolution> clarCoverSolutions;

	private RBO RBO;

	private ArrayList<String> names;

	private ResultLogFile nicsResult;
	private boolean databaseCheckedIR;

	private boolean databaseCheckedIMS2D1A;

	private int nbCrowns = -1;

	private String ims2d1a;

	private String amesFormat = "unknown";

	/**
	 * Constructors
	 */

	public Benzenoid(int nbNodes, int nbEdges, int nbHexagons, int[][] hexagons, Node[] nodesRefs,
					 int[][] edgeMatrix, RelativeMatrix coordinates) {

		comparator = new NbHexagonsComparator();

		this.nbNodes = nbNodes;
		this.nbEdges = nbEdges;
		this.nbHexagons = nbHexagons;
		this.hexagons = hexagons;
		this.nodesRefs = nodesRefs;
		this.edgeMatrix = edgeMatrix;
		this.coords = coordinates;

		hexagonsString = new ArrayList<>();

		for (int[] hexagon : hexagons) {

			StringBuilder builder = new StringBuilder();

			builder.append("h ");

			for (int u : hexagon) {
				Node node = nodesRefs[u];
				builder.append(node.getX()).append("_").append(node.getY()).append(" ");
			}

			hexagonsString.add(builder.toString());
		}

		initHexagons();
		computeDualGraph();
		computeDegrees();
		buildHexagonsCoords2();
	}

	public Benzenoid(int nbNodes, int nbEdges, int nbHexagons, ArrayList<ArrayList<Integer>> edgeLists,
					 int[][] edgeMatrix, ArrayList<String> edgesString, ArrayList<String> hexagonsString, Node[] nodesRefs,
					 RelativeMatrix coords, RelativeMatrix nodesMem, int maxIndex) {

		this.nbNodes = nbNodes;
		this.nbEdges = nbEdges;
		this.nbHexagons = nbHexagons;
		this.edgeLists = edgeLists;
		this.edgeMatrix = edgeMatrix;
		this.edgesString = edgesString;
		this.hexagonsString = hexagonsString;
		this.nodesRefs = nodesRefs;
		this.coords = coords;
		this.nodesMem = nodesMem;
		this.maxIndex = maxIndex;

		hexagons = new int[nbHexagons][6];
		initHexagons();

		nbStraightEdges = 0;

		for (int i = 0; i < edgeMatrix.length; i++) {
			for (int j = (i + 1); j < edgeMatrix[i].length; j++) {
				if (edgeMatrix[i][j] == 1) {
					Node u1 = nodesRefs[i];
					Node u2 = nodesRefs[j];

					if (u1.getX() == u2.getX())
						nbStraightEdges++;
				}
			}
		}

		computeDualGraph();
		computeDegrees();
		buildHexagonsCoords2();
	}

	/**
	 * Getters and setters
	 */

	public int[][] getDualGraph() {
		return dualGraph;
	}

	public int getNbNodes() {
		return nbNodes;
	}

	public int getNbEdges() {
		return nbEdges;
	}

	public int getNbHexagons() {
		return nbHexagons;
	}

	public int getMaxIndex() {
		return maxIndex;
	}

	public ArrayList<ArrayList<Integer>> getEdgeLists() {
		return edgeLists;
	}

	public int[][] getEdgeMatrix() {
		return edgeMatrix;
	}

	public ArrayList<String> getEdgesString() {
		return edgesString;
	}

	public ArrayList<String> getHexagonsString() {
		return hexagonsString;
	}

	public Node getNodeRef(int index) {
		return nodesRefs[index];
	}

	public RelativeMatrix getCoords() {
		return coords;
	}

	public Node[] getNodesRefs() {
		return nodesRefs;
	}

	public ArrayList<ArrayList<Integer>> getHexagonsVertices() {
		return hexagonsVertices;
	}

	public int getNbStraightEdges() {
		return nbStraightEdges;
	}

	public int[][] getHexagons() {
		return hexagons;
	}

	public int degree(int u) {
		return degrees[u];
	}

	/**
	 * Class's methods
	 */

	private void computeDegrees() {

		degrees = new int[nbNodes];

		for (int i = 0; i < nbNodes; i++) {

			int degree = 0;
			for (int j = 0; j < nbNodes; j++) {

				if (edgeMatrix[i][j] == 1)
					degree++;
			}

			degrees[i] = degree;
		}
	}

	private void computeDualGraph() {

		dualGraph = new int[nbHexagons][6];

		for (int i = 0; i < nbHexagons; i++)
			Arrays.fill(dualGraph[i], -1);

		ArrayList<Integer> candidats = new ArrayList<>();
		candidats.add(0);

		int index = 0;

		while (index < nbHexagons) {

			int candidat = candidats.get(index);
			int[] candidatHexagon = hexagons[candidat];

			for (int i = 0; i < candidatHexagon.length; i++) {

				int u = candidatHexagon[i];
				int v = candidatHexagon[(i + 1) % 6];

				System.out.print("");

				for (int j = 0; j < nbHexagons; j++) {
					if (j != candidat) { // j != i avant

						int contains = 0;
						for (int k = 0; k < 6; k++) {
							if (hexagons[j][k] == u || hexagons[j][k] == v)
								contains++;
						}

						if (contains == 2) {

							dualGraph[candidat][i] = j;

							if (!candidats.contains(j))
								candidats.add(j);

							break;
						}
					}
				}

			}
			index++;
		}
	}

	public RelativeMatrix getNodesMem() {
		return nodesMem;
	}

	private int findHexagon(int u, int v) {

		for (int i = 0; i < nbHexagons; i++) {
			int[] hexagon = hexagons[i];

			if (hexagon[4] == u && hexagon[5] == v)
				return i;
		}

		return -1;

	}

	private ArrayList<Integer> findHexagons(int hexagon, Interval interval) {

		ArrayList<Integer> hexagons = new ArrayList<>();
		int size = interval.size() / 2;

		hexagons.add(hexagon);

		int newHexagon = hexagon;

		for (int i = 0; i < size; i++) {

			newHexagon = dualGraph[newHexagon][1];
			hexagons.add(newHexagon);
		}

		return hexagons;

	}

	public ArrayList<Integer> getAllHexagonsOfIntervals(ArrayList<Interval> intervals) {

		ArrayList<Integer> hexagons = new ArrayList<>();

		for (Interval interval : intervals) {

			int hexagon = findHexagon(interval.x1(), interval.y1());
			hexagons.addAll(findHexagons(hexagon, interval));
		}

		return hexagons;
	}

	private void initHexagons() {

		hexagonsVertices = new ArrayList<>();

		for (int i = 0; i < nbNodes; i++)
			hexagonsVertices.add(new ArrayList<>());

		for (int i = 0; i < nbHexagons; i++) {
			String hexagon = hexagonsString.get(i);
			String[] sHexagon = hexagon.split(" ");

			for (int j = 1; j < sHexagon.length; j++) {
				String[] sVertex = sHexagon[j].split(java.util.regex.Pattern.quote("_"));
				int x = Integer.parseInt(sVertex[0]);
				int y = Integer.parseInt(sVertex[1]);
				hexagons[i][j - 1] = coords.get(x, y);
				hexagonsVertices.get(coords.get(x, y)).add(i);
			}
		}
	}

	public int getNbHydrogens() {

		if (nbHydrogens == 0) {

			for (int i = 0; i < nbNodes; i++) {

				int degree = 0;
				for (int j = 0; j < nbNodes; j++) {

					if (edgeMatrix[i][j] == 1)
						degree++;
				}

				if (degree == 2)
					nbHydrogens++;
			}
		}

		return nbHydrogens;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toString() {

		if (name == null) {

			int nbCrowns = (int) Math.floor((((double) nbHexagons + 1) / 2.0) + 1.0);

			if (nbHexagons % 2 == 1)
				nbCrowns--;

			int diameter = (2 * nbCrowns) - 1;

			int[][] coordsMatrix = new int[diameter][diameter];

			/*
			 * Building coords matrix
			 */

			for (int i = 0; i < diameter; i++) {
				Arrays.fill(coordsMatrix[i], -1);
			}

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

			Couple<Integer, Integer>[] hexagons = new Couple[nbHexagons];

			ArrayList<Integer> candidats = new ArrayList<>();
			candidats.add(0);

			hexagons[0] = new Couple<>(0, 0);

			int[] checkedHexagons = new int[nbHexagons];
			checkedHexagons[0] = 1;

			while (!candidats.isEmpty()) {
				int candidat = candidats.get(0);
				for (HexNeighborhood neighbor : HexNeighborhood.values()) {
					int neighborIndex = dualGraph[candidat][neighbor.getIndex()];
					if (neighborIndex != -1) {
						if (checkedHexagons[neighborIndex] == 0) {
							hexagons[neighborIndex] = new Couple<>(hexagons[candidat].getX() + neighbor.dx(), hexagons[candidat].getY() + neighbor.dy());
							candidats.add(neighborIndex);
							checkedHexagons[neighborIndex] = 1;
						}
					}
				}

				candidats.remove(candidats.get(0));
			}

			/*
			 * Trouver une mani�re de le faire rentrer dans le coron�no�de
			 */

			StringBuilder code = new StringBuilder();

			for (int i = 0; i < diameter; i++) {
				for (int j = 0; j < diameter; j++) {

					int h = coordsMatrix[i][j];

					if (h != -1) {

						boolean ok = true;

						Couple<Integer, Integer>[] newHexagons = new Couple[hexagons.length];

						for (int k = 0; k < hexagons.length; k++) {

							Couple<Integer, Integer> hexagon = hexagons[k];

							int xh = hexagon.getY() + i;
							int yh = hexagon.getX() + j;

							if (xh >= 0 && xh < diameter && yh >= 0 && yh < diameter && coordsMatrix[xh][yh] != -1) {
								newHexagons[k] = new Couple<>(xh, yh);
							}

							else {
								ok = false;
								break;
							}
						}

						if (ok) {

							for (int k = 0; k < newHexagons.length; k++) {
								code.append(coordsMatrix[newHexagons[k].getX()][newHexagons[k].getY()]);

								if (k < newHexagons.length - 1)
									code.append("-");
							}

							name = code.toString();
							return code.toString();

						}

					}
				}
			}

			name = null;
			return null;
		}

		else
			return name;
	}

	public void exportToCML(File file) {
		//TODO
	}



	public void exportProperties(File file) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		writer.write("molecule_name\t" + description + "\n");
		writer.write("nb_carbons\t" + nbNodes + "\n");
		writer.write("nb_hydrogens\t" + this.getNbHydrogens() + "\n");
		writer.write("nb_hexagons\t" + nbHexagons + "\n");

		String nbKekuleStructures = Double.toString(getNbKekuleStructures()).split(java.util.regex.Pattern.quote("."))[0];

		writer.write(
				new String(("nb_kekule_structures\t" + nbKekuleStructures).getBytes(), StandardCharsets.UTF_8)
						+ "\n");

		getIrregularity();
		writer.write("XI\t" + irregularity.getXI() + "\n");
		writer.write("#solo\t" + irregularity.getGroup(0) + "\n");
		writer.write("#duo\t" + irregularity.getGroup(1) + "\n");
		writer.write("#trio\t" + irregularity.getGroup(2) + "\n");
		writer.write("#quatuors\t" + irregularity.getGroup(3) + "\n");

		if (aromaticity != null) {
			for (int i = 0; i < aromaticity.getLocalAromaticity().length; i++)
				writer.write("E(H_" + i + ")\t" + aromaticity.getLocalAromaticity()[i] + "\n");
		}

		ArrayList<ClarCoverSolution> clarCoverSolutions = this.getClarCoverSolutions();
		if (clarCoverSolutions != null) {
			writer.write("\nradicalar statistics\n");
			double[] stats = ClarCoverSolution.getRadicalarStatistics(clarCoverSolutions);
			for (int i = 0; i < Objects.requireNonNull(stats).length; i++)
				writer.write("C" + (i + 1) + " : " + stats[i] + "\n");
		}

		writer.close();

	}

	public BenzenoidSolution buildBenzenoidSolution() {

		ModelPropertySet modelPropertySet = new ModelPropertySet();
		modelPropertySet.getById("hexagons").addExpression(new BinaryNumericalExpression("hexagons", "=", nbHexagons));

		String name = toString();

		String[] split = name.split(java.util.regex.Pattern.quote("-"));

		GeneralModel model = new GeneralModel(modelPropertySet);

		for (String s : split) {

			int u = Integer.parseInt(s);
			model.getProblem().arithm(model.getBenzenoidVerticesBVArray()[u], "=", 1).post();
		}

		SolverResults result = model.solve();

		return result.getSolutions().get(0);
	}

	public void setVerticesSolutions(ArrayList<Integer> verticesSolutions) {
		this.verticesSolutions = verticesSolutions;
	}

	public ArrayList<Integer> getVerticesSolutions() {
		return verticesSolutions;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void buildHexagonsCoords() {

		hexagonsCoords = new Couple[nbHexagons];

		int[] checkedHexagons = new int[nbHexagons];
		checkedHexagons[0] = 1;

		ArrayList<Integer> candidats = new ArrayList<>();
		candidats.add(0);

		hexagonsCoords[0] = new Couple(0, 0);

		while (!candidats.isEmpty()) {

			int candidat = candidats.get(0);

			for (HexNeighborhood neighbor : HexNeighborhood.values()) {
				int neighborIndex = dualGraph[candidat][neighbor.getIndex()];
				if (neighborIndex != -1 && checkedHexagons[neighborIndex] == 0) {
					checkedHexagons[neighborIndex] = 1;
					hexagonsCoords[neighborIndex] = new Couple<>(hexagonsCoords[candidat].getX() + neighbor.dx(), hexagonsCoords[candidat].getY() + neighbor.dy());
					candidats.add(neighborIndex);
				}
			}
			candidats.remove(candidats.get(0));
		}
	}

	public Couple<Integer, Integer>[] getHexagonsCoords() {
		return hexagonsCoords;
	}

	public int[] getDegrees() {
		return degrees;
	}

	public static Irregularity computeParameterOfIrregularity(Benzenoid molecule) {

		if (molecule.getNbHexagons() == 1)
			return null;

		int[] N = new int[4];
		int[] checkedNodes = new int[molecule.getNbNodes()];

		ArrayList<Integer> V = new ArrayList<>();

		for (int u = 0; u < molecule.getNbNodes(); u++) {
			int degree = molecule.degree(u);
			if (degree == 2 && !V.contains(u)) {
				V.add(u);
				checkedNodes[u] = 0;
			}

			else if (degree != 2)
				checkedNodes[u] = -1;
		}

		ArrayList<Integer> candidats = new ArrayList<>();

		while (true) {

			int firstVertice = -1;
			for (Integer u : V) {
				if (checkedNodes[u] == 0) {
					firstVertice = u;
					break;
				}
			}

			if (firstVertice == -1)
				break;

			candidats.add(firstVertice);
			checkedNodes[firstVertice] = 1;

			int nbNeighbors = 1;

			while (!candidats.isEmpty()) {

				int candidat = candidats.get(0);

				for (int i = 0; i < molecule.getNbNodes(); i++) {
					if (molecule.getEdgeMatrix()[candidat][i] == 1 && checkedNodes[i] == 0) {

						checkedNodes[i] = 1;
						nbNeighbors++;
						candidats.add(i);
					}
				}

				candidats.remove(candidats.get(0));
			}

			N[nbNeighbors - 1] += nbNeighbors;
		}

		double XI = ((double) N[2] + (double) N[3]) / ((double) N[0] + (double) N[1] + (double) N[2] + (double) N[3]);
		return new Irregularity(N, XI);
	}

	public Irregularity getIrregularity() {

		if (irregularity == null)
			irregularity = Benzenoid.computeParameterOfIrregularity(this);

		return irregularity;
	}

	public Aromaticity getAromaticity() {

		if (aromaticity != null)
			return aromaticity;

		try {
			aromaticity = LinAlgorithm.solve(this, PerfectMatchingType.DET);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		aromaticity.normalize(getNbKekuleStructures());
		return aromaticity;
	}

	public double getNbKekuleStructures() {

		if (nbKekuleStructures == -1) {
			int[] disabledVertices = new int[getNbNodes()];
			int[] degrees = getDegrees();

			SubGraph subGraph = new SubGraph(getEdgeMatrix(), disabledVertices, degrees, PerfectMatchingType.DET);

			nbKekuleStructures = subGraph.getNbPerfectMatchings();
		}

		return nbKekuleStructures;
	}

	public void normalizeAromaticity() {
		if (aromaticity == null)
			aromaticity = getAromaticity();

	}

	public boolean isAromaticitySet() {
		return aromaticity != null;
	}

	public boolean edgeExists(int i, int j) {
		return edgeMatrix[i][j] == 1;
	}

	public ArrayList<Couple<Integer, Integer>> getBoundsInvolved(int carbon) {

		ArrayList<Couple<Integer, Integer>> bounds = new ArrayList<>();

		for (int i = 0; i < nbNodes; i++) {
			if (edgeExists(carbon, i))
				bounds.add(new Couple<>(carbon, i));
		}

		return bounds;

	}

	public ArrayList<Integer> getHexagonsInvolved(int carbon) {

		ArrayList<Integer> hexagons = new ArrayList<>();

		for (int i = 0; i < nbHexagons; i++) {
			int[] hexagon = getHexagons()[i];
			for (Integer u : hexagon) {
				if (u == carbon) {
					hexagons.add(i);
					break;
				}
			}
		}

		return hexagons;
	}

	public ArrayList<Integer> getHexagonsInvolved(int carbon1, int carbon2) {

		ArrayList<Integer> hexagons = new ArrayList<>();

		for (int i = 0; i < nbHexagons; i++) {

			int[] hexagon = getHexagons()[i];
			boolean containsCarbon1 = false;
			boolean containsCarbon2 = false;

			for (Integer u : hexagon) {
				if (u == carbon1)
					containsCarbon1 = true;
				if (u == carbon2)
					containsCarbon2 = true;

				if (containsCarbon1 && containsCarbon2)
					hexagons.add(i);
			}

		}

		return hexagons;

	}

	public int[] getHexagon(int hexagon) {
		return hexagons[hexagon];
	}

	public RBO getRBO() {

		if (RBO != null)
			return RBO;

		RBO = RBOSolver.RBO(this);
		return RBO;
	}

	public void setComparator(MoleculeComparator comparator) {
		this.comparator = comparator;
	}

	@Override
	public int compareTo(Benzenoid arg0) {
		return comparator.compare(this, arg0);
	}

	public void setClarCoverSolution(ClarCoverSolution clarCoverSolution) {
		this.clarCoverSolution = clarCoverSolution;
	}

	public ClarCoverSolution getClarCoverSolution() {
		return clarCoverSolution;
	}

	public int[] resonanceEnergyClar() {

		int[] clarValues = new int[nbHexagons];

		for (ClarCoverSolution solution : clarCoverSolutions) {
			for (int i = 0; i < nbHexagons; i++) {
				if (solution.isCircle(i))
					clarValues[i]++;
			}
		}

		return clarValues;
	}

	private int[][] buildCoordsMatrix(int nbCrowns, int diameter) {

		int[][] coordsMatrix = new int[diameter][diameter];
		for (int i = 0; i < diameter; i++) {
			Arrays.fill(coordsMatrix[i], -1);
		}

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

	private boolean touchBorder(int xShift, int yShift, ArrayList<Integer> topBorder, ArrayList<Integer> leftBorder,
			int[][] coordsMatrix) {

		boolean topContains = false;
		boolean leftContains = false;

		for (int i = 0; i < nbHexagons; i++) {
			Couple<Integer, Integer> coord = hexagonsCoords[i];

			int x = coord.getX() + xShift;
			int y = coord.getY() + yShift;

			if (x >= 0 && x < coordsMatrix.length && y >= 0 && y < coordsMatrix.length) {

				if (topBorder.contains(coordsMatrix[x][y]))
					topContains = true;

				if (leftBorder.contains(coordsMatrix[x][y]))
					leftContains = true;

				if (topContains && leftContains)
					return true;
			}
		}

		return false;
	}

	private boolean touchBorder(ArrayList<Integer> hexagons, ArrayList<Integer> topBorder,
			ArrayList<Integer> leftBorder) {

		boolean touchTop = false;
		boolean touchLeft = false;

		for (Integer hexagon : hexagons) {
			if (topBorder.contains(hexagon))
				touchTop = true;
			if (leftBorder.contains(hexagon))
				touchLeft = true;
		}

		return touchTop && touchLeft;
	}

	private boolean areNeighbors(int hexagon1, int hexagon2) {

		for (int i = 0; i < 6; i++) {
			if (dualGraph[hexagon1][i] == hexagon2)
				return true;
		}

		return false;
	}

	public Pattern convertToPattern(int xShift, int yShift) {

		@SuppressWarnings("unchecked")
		Couple<Integer, Integer>[] shiftCoords = new Couple[nbHexagons];

		for (int i = 0; i < nbHexagons; i++)
			shiftCoords[i] = new Couple<>(hexagonsCoords[i].getX() + xShift, hexagonsCoords[i].getY() + yShift);

		/*
		 * Nodes
		 */

		Node[] nodes = new Node[nbHexagons];

		for (int i = 0; i < nbHexagons; i++) {
			Couple<Integer, Integer> couple = shiftCoords[i];
			// nodes[i] = new Node(couple.getY(), couple.getX(), i);
			nodes[i] = new Node(couple.getX(), couple.getY(), i);
		}

		/*
		 * Matrix
		 */

		int[][] matrix = new int[nbHexagons][nbHexagons];
		int[][] neighbors = new int[nbHexagons][6];

		for (int i = 0; i < nbHexagons; i++)
			Arrays.fill(neighbors[i], -1);

		for (int i = 0; i < nbHexagons; i++) {

			Node n1 = nodes[i];

			for (int j = (i + 1); j < nbHexagons; j++) {

				Node n2 = nodes[j];

				if (areNeighbors(i, j)) {

					// Setting matrix
					matrix[i][j] = 1;
					matrix[j][i] = 1;

					// Setting neighbors
					int x1 = n1.getX();
					int y1 = n1.getY();
					int x2 = n2.getX();
					int y2 = n2.getY();

					if (x2 == x1 && y2 == y1 - 1) {
						neighbors[i][0] = j;
						neighbors[j][3] = i;
					}

					else if (x2 == x1 + 1 && y2 == y1) {
						neighbors[i][1] = j;
						neighbors[j][4] = i;
					}

					else if (x2 == x1 + 1 && y2 == y1 + 1) {
						neighbors[i][2] = j;
						neighbors[j][5] = i;
					}

					else if (x2 == x1 && y2 == y1 + 1) {
						neighbors[i][3] = j;
						neighbors[j][0] = i;
					}

					else if (x2 == x1 - 1 && y2 == y1) {
						neighbors[i][4] = j;
						neighbors[j][1] = i;
					}

					else if (x2 == x1 - 1 && y2 == y1 - 1) {
						neighbors[i][5] = j;
						neighbors[j][2] = i;
					}
				}

			}
		}

		/*
		 * Label
		 */

		PatternLabel[] labels = new PatternLabel[nbHexagons];

		for (int i = 0; i < nbHexagons; i++)
			labels[i] = PatternLabel.POSITIVE;

		return new Pattern(matrix, labels, nodes, null, neighbors, 0);
	}

	private ArrayList<String> translations(Pattern pattern, int diameter, int[][] coordsMatrix,
										   ArrayList<Integer> topBorder, ArrayList<Integer> leftBorder) {

		ArrayList<String> names = new ArrayList<>();

		int xShiftMax = Math.max(Math.abs(pattern.xMax() - pattern.xMin()), diameter);
		int yShiftMax = Math.max(Math.abs(pattern.yMax() - pattern.yMin()), diameter);

		for (int xShift = 0; xShift < xShiftMax; xShift++) {
			for (int yShift = 0; yShift < yShiftMax; yShift++) {

				Node[] initialCoords = pattern.getNodesRefs();
				Node[] shiftedCoords = new Node[initialCoords.length];

				boolean ok = true;

				for (int i = 0; i < shiftedCoords.length; i++) {
					Node node = initialCoords[i];
					// if (node != null) {
					Node newNode = new Node(node.getX() + xShift, node.getY() + yShift, i);
					shiftedCoords[i] = newNode;

					int x = newNode.getX();
					int y = newNode.getY();

					if (x < 0 || x >= diameter || y < 0 || y >= diameter || coordsMatrix[x][y] == -1) {
						ok = false;
						break;
					}
					// }
				}

				if (ok) {

					ArrayList<Integer> hexagons = new ArrayList<>();
					for (Node node : shiftedCoords) {
						int hexagon = coordsMatrix[node.getX()][node.getY()];
						hexagons.add(hexagon);
					}

					if (touchBorder(hexagons, topBorder, leftBorder)) {
						Collections.sort(hexagons);
						StringBuilder name = new StringBuilder();
						for (int i = 0; i < hexagons.size(); i++) {
							name.append(hexagons.get(i));
							if (i < hexagons.size() - 1)
								name.append("-");
						}
						names.add(name.toString());
					}

				}
			}
		}

		return names;
	}

	public ArrayList<String> getNames() {

		if (names != null)
			return names;

		/*
		 * Creating coronenoid matrix
		 */

		names = new ArrayList<>();

		int nbCrowns = (int) Math.floor((((double) nbHexagons + 1) / 2.0) + 1.0);

		if (nbHexagons % 2 == 1)
			nbCrowns--;

		int diameter = (2 * nbCrowns) - 1;

		int[][] coordsMatrix = buildCoordsMatrix(nbCrowns, diameter);

		ArrayList<Integer> topBorder = new ArrayList<>();
		ArrayList<Integer> leftBorder = new ArrayList<>();

		for (int i = 0; i < diameter; i++) {
			if (coordsMatrix[0][i] != -1)
				topBorder.add(coordsMatrix[0][i]);
		}

		for (int i = 0; i < diameter; i++) {

			int j = 0;
			while (coordsMatrix[i][j] == -1)
				j++;

			leftBorder.add(coordsMatrix[i][j]);
		}

		int xShift = -1;
		int yShift = -1;

		boolean touchBorder = false;
		for (int i = 0; i < diameter; i++) {
			for (int j = 0; j < diameter; j++) {
				if (touchBorder(i, j, topBorder, leftBorder, coordsMatrix)) {
					touchBorder = true;
					xShift = i;
					yShift = j;
					break;
				}
			}
			if (touchBorder)
				break;
		}

		Pattern pattern = convertToPattern(xShift, yShift);
		ArrayList<Pattern> rotations = pattern.computeRotations();

		for (Pattern f : rotations) {
			ArrayList<String> rotationsNames = translations(f, diameter, coordsMatrix, topBorder, leftBorder);
			for (String name : rotationsNames) {
				if (!names.contains(name))
					names.add(name);
			}
		}

		return names;
	}

	@SuppressWarnings("unchecked")
	private void buildHexagonsCoords2() {
		int[][] dualGraph = this.getDualGraph();

		int[] checkedHexagons = new int[this.getNbHexagons()];

		ArrayList<Integer> candidates = new ArrayList<>();
		candidates.add(0);

		hexagonsCoords = new Couple[nbHexagons];

		checkedHexagons[0] = 1;
		hexagonsCoords[0] = new Couple<>(0, 0);

		// centersCoords[0] = new Couple<Double, Double>(40.0, 40.0);

		while (!candidates.isEmpty()) {

			int candidateIndex = candidates.get(0);

			for (HexNeighborhood neighbor : HexNeighborhood.values()) {
				int n = dualGraph[candidateIndex][neighbor.getIndex()];
				if (n != -1) {
					if (checkedHexagons[n] == 0) {
						int x = hexagonsCoords[candidateIndex].getX() + neighbor.dx();
						int y = hexagonsCoords[candidateIndex].getY() + neighbor.dy();
						checkedHexagons[n] = 1;
						hexagonsCoords[n] = new Couple<>(x, y);
						// centersCoords[n] = new Couple<Double, Double>(xCenter, yCenter);
						candidates.add(n);
					}
				}
			}

			candidates.remove(candidates.get(0));
		}

	}

	// "/find_ims2d_1a_by_name"
	public String getIms2d1a() {

		if (ims2d1a != null || databaseCheckedIMS2D1A)
			return ims2d1a;

		if (!Post.isDatabaseConnected)
			return null;

		databaseCheckedIMS2D1A = true;

		String name = getNames().get(0);
		String url = "https://benzenoids.lis-lab.fr/find_ims2d_1a_by_name/";
		String json = "{\"name\": \"" + name + "\"}";

		try {
			List<Map> results = Post.post(url, json);

			if (!results.isEmpty()) {
				Map map = results.get(0);
				ims2d1a = (String) map.get("picture");
				return ims2d1a;
			}

		} catch (Exception e) {
			System.out.println("Connection to database failed");
			return null;
		}

		return ims2d1a;
	}

	// "{\"name\": \"1-11-20-27-28-29-30-39\"}";
	@SuppressWarnings("rawtypes")
	public ResultLogFile getIRSpectraResult() {

		if (nicsResult != null || databaseCheckedIR)
			return nicsResult;

		if (!Post.isDatabaseConnected)
			return null;

		databaseCheckedIR = true;

		String name = getNames().get(0);
		String url = "https://benzenoids.lis-lab.fr/find_by_name/";
		String json = "{\"name\": \"" + name + "\"}";

		try {
			List<Map> results = Post.post(url, json);

			if (!results.isEmpty()) {
				IRSpectraEntry content = IRSpectraEntry.buildQueryContent(results.get(0));
				amesFormat = content.getAmesFormat();
				nicsResult = content.buildResultLogFile();
				System.out.println(nicsResult);
				return nicsResult;
			}

		} catch (Exception e) {
			System.out.println("Connection to database failed");
			return null;
		}

		return null;
	}

	public boolean inDatabase() {

		String name = getNames().get(0);
		String url = "https://benzenoids.lis-lab.fr/find_id/";
		String json = "{\"name\": \"" + name + "\"}";

		try {
			List<Map> results = Post.post(url, json);

			if (!results.isEmpty()) {
				return true;
			}

		} catch (Exception e) {
			System.out.println("Connection to database failed");
			return false;
		}

		return false;

	}

	public void setNicsResult(ResultLogFile nicsResult) {
		this.nicsResult = nicsResult;
		databaseCheckedIR = true;
	}

	public boolean databaseCheckedIR() {
		return databaseCheckedIR;
	}

	public static ArrayList<Benzenoid> union(ArrayList<Benzenoid> molecules1, ArrayList<Benzenoid> molecules2) {

		ArrayList<Benzenoid> molecules = new ArrayList<>();

		for (Benzenoid molecule : molecules1) {
			if (!molecules.contains(molecule))
				molecules.add(molecule);
		}

		for (Benzenoid molecule : molecules2) {
			if (!molecules.contains(molecule))
				molecules.add(molecule);
		}

		return molecules;
	}

	public static ArrayList<Benzenoid> intersection(ArrayList<Benzenoid> molecules1, ArrayList<Benzenoid> molecules2) {

		ArrayList<Benzenoid> molecules = new ArrayList<>();

		for (Benzenoid molecule : molecules1) {
			if (molecules2.contains(molecule))
				molecules.add(molecule);
		}

		return molecules;
	}

	public static ArrayList<Benzenoid> diff(ArrayList<Benzenoid> molecules1, ArrayList<Benzenoid> molecules2) {

		ArrayList<Benzenoid> molecules = new ArrayList<>();

		for (Benzenoid molecule : molecules1) {
			if (!molecules2.contains(molecule))
				molecules.add(molecule);
		}

		return molecules;
	}

	public void setNbCrowns(int nbCrowns) {
		this.nbCrowns = nbCrowns;
	}

	public int getNbCrowns() {
		return nbCrowns;
	}

	@Override
	public boolean equals(Object obj) {

		Benzenoid molecule = (Benzenoid) obj;

		for (String name : molecule.getNames()) {
			if (this.getNames().contains(name))
				return true;
		}
		return false;
	}

	public void setClarCoverSolutions(ArrayList<ClarCoverSolution> clarCoverSolutions) {
		this.clarCoverSolutions = clarCoverSolutions;
	}

	public ArrayList<ClarCoverSolution> getClarCoverSolutions() {
		return clarCoverSolutions;
	}

	public void setFixedBonds(int[][] fixedBonds) {
		this.fixedBonds = fixedBonds;
	}

	public int[][] getFixedBonds() {
		return fixedBonds;
	}

	public int[] getFixedCircles() {
		return fixedCircles;
	}

	public void setFixedCircles(int[] fixedCircles) {
		this.fixedCircles = fixedCircles;
	}

	public int colorShift() {
		int colorShift = 0;
		for (int i = 0; i < nbHexagons; i++) {
			if (dualGraph[i][0] == -1 && dualGraph[i][5] == -1)
				colorShift++;
			if (dualGraph[i][2] == -1 && dualGraph[i][3] == -1)
				colorShift--;
		}
		return colorShift;
	}

	public ArrayList<int[][]> getKekuleStructures() {
		return kekuleStructures;
	}

	public void setKekuleStructures(ArrayList<int[][]> kekuleStructures) {
		this.kekuleStructures = kekuleStructures;
	}

	/***
	 *
	 */
	public boolean respectPostProcessing(ModelPropertySet modelPropertySet) {
		for(Property property : modelPropertySet)
			if(property.hasExpressions() && !((ModelProperty) property).getChecker().checks(this, (ModelProperty) property))
				return false;
		return true;
	}

	public String getAmesFormat() {
		return amesFormat;
	}

	/***
	 *
	 */
	public static Benzenoid buildMolecule(String description, int nbCrowns, int index, ArrayList<Integer> verticesSolution) {
		Benzenoid molecule = null;
		try {
			String graphFilename = "tmp.graph";
			String graphCoordFilename = "tmp.graph_coord";

			buildGraphFile(nbCrowns, verticesSolution, graphFilename);
			convertGraphCoordFileInstance(graphFilename, graphCoordFilename);
			molecule = buildMolecule(description, nbCrowns, index, verticesSolution, graphCoordFilename);
			deleteTmpFiles(graphFilename, graphCoordFilename);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return molecule;
	}

	/***
	 *
	 */
	private static Benzenoid buildMolecule(String description, int nbCrowns, int index, ArrayList<Integer> verticesSolution,
										   String graphCoordFilename) {
		Benzenoid molecule = GraphParser.parseUndirectedGraph(graphCoordFilename, null, false);
		molecule.setVerticesSolutions(verticesSolution);
		molecule.setDescription(buildMoleculeDescription(description, index));
		molecule.setNbCrowns(nbCrowns);
		return molecule;
	}
	/***
	 *
	 */
	private static String buildMoleculeDescription(String description, int index) {
		String[] lines = description.split("\n");
		StringBuilder b = new StringBuilder();

		b.append("solution_" + index + "\n");
		for (int j = 1; j < lines.length; j++)
			b.append(lines[j] + "\n");
		return b.toString();
	}
	/***
	 *
	 */
	private static void deleteTmpFiles(String graphFilename, String graphCoordFilename) {
		File file = new File(graphFilename);
		file.delete();
		file = new File(graphCoordFilename);
		file.delete();
	}

	/***
	 *
	 */
	private static void convertGraphCoordFileInstance(String graphFilename, String graphCoordFilename) {
		GraphCoordFileBuilder graphCoordBuilder = new GraphCoordFileBuilder(graphFilename, graphCoordFilename);
		graphCoordBuilder.convertInstance();
	}

	/***
	 *
	 */
	private static void buildGraphFile(int nbCrowns, ArrayList<Integer> verticesSolution,
									   String graphFilename) throws IOException {
		GraphFileBuilder graphBuilder = new GraphFileBuilder(verticesSolution, graphFilename,
				nbCrowns);
		graphBuilder.buildGraphFile();
	}

}
