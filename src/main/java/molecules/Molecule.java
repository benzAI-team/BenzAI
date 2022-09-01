package molecules;

import static java.lang.ProcessBuilder.Redirect.appendTo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import classifier.Irregularity;
import database.PictureConverter;
import database.models.IRSpectraEntry;
import generator.GeneralModel;
import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;
import generator.ResultSolver;
import generator.fragments.Fragment;
import http.Post;
import modelProperty.ModelPropertySet;
import modelProperty.expression.BinaryNumericalExpression;
import molecules.sort.MoleculeComparator;
import molecules.sort.NbHexagonsComparator;
import solution.BenzenoidSolution;
import solution.ClarCoverSolution;
import solution.GraphConversion;
import solveur.Aromaticity;
import solveur.LinAlgorithm;
import solveur.LinAlgorithm.PerfectMatchingType;
import solveur.RBOSolver;
import spectrums.ResultLogFile;
import utils.Couple;
import utils.Interval;
import utils.RelativeMatrix;
import view.groups.ClarCoverGroup;
import view.groups.IMS2D1AGroup;
import view.groups.RBOGroup;
import view.groups.RadicalarClarCoverGroup;

public class Molecule implements Comparable<Molecule> {

	private MoleculeComparator comparator;

	private RelativeMatrix nodesMem; // DEBUG

	private int nbNodes, nbEdges, nbHexagons, nbStraightEdges, maxIndex;
	private ArrayList<ArrayList<Integer>> edgeMatrix;
	private int[][] adjacencyMatrix;
	private ArrayList<String> edgesString;
	private ArrayList<String> hexagonsString;
	private Node[] nodesRefs;
	private RelativeMatrix coords;
	private int[][] hexagons;
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
	private ClarCoverGroup clarCoverGroup;
	private int[][] fixedBonds;
	private int[] fixedCircles;
	private ArrayList<int[][]> kekuleStructures;

	private ArrayList<ClarCoverSolution> clarCoverSolutions;
	private RadicalarClarCoverGroup radicalarGroup;

	private RBO RBO;
	private RBOGroup rboGroup;

	private ArrayList<String> names;

	private ResultLogFile nicsResult;
	private boolean databaseCheckedIR;

	private boolean databaseCheckedIMS2D1A;

	private int nbCrowns = -1;

	private String ims2d1a;
	private IMS2D1AGroup ims2d1aGroup;

	/**
	 * Constructors
	 */

	public Molecule(int nbNodes, int nbEdges, int nbHexagons, int[][] hexagons, Node[] nodesRefs,
			int[][] adjacencyMatrix, RelativeMatrix coords) {
    
		comparator = new NbHexagonsComparator();

		this.nbNodes = nbNodes;
		this.nbEdges = nbEdges;
		this.nbHexagons = nbHexagons;
		this.hexagons = hexagons;
		this.nodesRefs = nodesRefs;
		this.adjacencyMatrix = adjacencyMatrix;
		this.coords = coords;

		hexagonsString = new ArrayList<>();

		for (int[] hexagon : hexagons) {

			StringBuilder builder = new StringBuilder();

			builder.append("h ");

			for (int u : hexagon) {
				Node node = nodesRefs[u];
				builder.append(node.getX() + "_" + node.getY() + " ");
			}

			hexagonsString.add(builder.toString());
		}

		initHexagons();
		computeDualGraph();
		computeDegrees();
		buildHexagonsCoords2();
	}


	public Molecule(int nbNodes, int nbEdges, int nbHexagons, ArrayList<ArrayList<Integer>> edgeMatrix,
			int[][] adjacencyMatrix, ArrayList<String> edgesString, ArrayList<String> hexagonsString, Node[] nodesRefs,
			RelativeMatrix coords, RelativeMatrix nodesMem, int maxIndex) {

		this.nbNodes = nbNodes;
		this.nbEdges = nbEdges;
		this.nbHexagons = nbHexagons;
		this.edgeMatrix = edgeMatrix;
		this.adjacencyMatrix = adjacencyMatrix;
		this.edgesString = edgesString;
		this.hexagonsString = hexagonsString;
		this.nodesRefs = nodesRefs;
		this.coords = coords;
		this.nodesMem = nodesMem;
		this.maxIndex = maxIndex;

		hexagons = new int[nbHexagons][6];
		initHexagons();

		nbStraightEdges = 0;

		for (int i = 0; i < adjacencyMatrix.length; i++) {
			for (int j = (i + 1); j < adjacencyMatrix[i].length; j++) {
				if (adjacencyMatrix[i][j] == 1) {
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

	public ArrayList<ArrayList<Integer>> getEdgeMatrix() {
		return edgeMatrix;
	}

	public int[][] getAdjacencyMatrix() {
		return adjacencyMatrix;
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

	public SimpleGraph<Integer, DefaultEdge> getCarbonGraph() {
		return GraphConversion.buildCarbonGraph(this);
	}

	public SimpleGraph<Integer, DefaultEdge> getHexagonGraph() {
		return GraphConversion.buildHexagonGraph(this);
	}

	private void computeDegrees() {

		degrees = new int[nbNodes];

		for (int i = 0; i < nbNodes; i++) {

			int degree = 0;
			for (int j = 0; j < nbNodes; j++) {

				if (adjacencyMatrix[i][j] == 1)
					degree++;
			}

			degrees[i] = degree;
		}
	}

	private void computeDualGraph() {

		dualGraph = new int[nbHexagons][6];

		for (int i = 0; i < nbHexagons; i++)
			for (int j = 0; j < 6; j++)
				dualGraph[i][j] = -1;

		ArrayList<Integer> candidats = new ArrayList<Integer>();
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

		ArrayList<Integer> hexagons = new ArrayList<Integer>();
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

		ArrayList<Integer> hexagons = new ArrayList<Integer>();

		for (Interval interval : intervals) {

			int hexagon = findHexagon(interval.x1(), interval.y1());
			hexagons.addAll(findHexagons(hexagon, interval));
		}

		return hexagons;
	}

	public void initHexagons() {

		hexagonsVertices = new ArrayList<ArrayList<Integer>>();

		for (int i = 0; i < nbNodes; i++)
			hexagonsVertices.add(new ArrayList<Integer>());

		for (int i = 0; i < nbHexagons; i++) {
			String hexagon = hexagonsString.get(i);
			String[] sHexagon = hexagon.split(" ");

			for (int j = 1; j < sHexagon.length; j++) {
				String[] sVertex = sHexagon[j].split(Pattern.quote("_"));
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

					if (adjacencyMatrix[i][j] == 1)
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

			int nbCrowns = (int) Math.floor((((double) ((double) nbHexagons + 1)) / 2.0) + 1.0);

			if (nbHexagons % 2 == 1)
				nbCrowns--;

			int diameter = (2 * nbCrowns) - 1;

			int[][] coordsMatrix = new int[diameter][diameter];

			/*
			 * Building coords matrix
			 */

			for (int i = 0; i < diameter; i++) {
				for (int j = 0; j < diameter; j++) {
					coordsMatrix[i][j] = -1;
				}
			}

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

			Couple<Integer, Integer>[] hexagons = new Couple[nbHexagons];

			ArrayList<Integer> candidats = new ArrayList<Integer>();
			candidats.add(0);

			hexagons[0] = new Couple<Integer, Integer>(0, 0);

			int[] checkedHexagons = new int[nbHexagons];
			checkedHexagons[0] = 1;

			while (candidats.size() > 0) {

				int candidat = candidats.get(0);

				int x1 = hexagons[candidat].getX();
				int y1 = hexagons[candidat].getY();

				for (int i = 0; i < 6; i++) {

					int neighbor = dualGraph[candidat][i];

					if (neighbor != -1) {
						if (checkedHexagons[neighbor] == 0) {

							int x2, y2;

							if (i == 0) {
								x2 = x1;
								y2 = y1 - 1;
							}

							else if (i == 1) {
								x2 = x1 + 1;
								y2 = y1;
							}

							else if (i == 2) {
								x2 = x1 + 1;
								y2 = y1 + 1;
							}

							else if (i == 3) {
								x2 = x1;
								y2 = y1 + 1;
							}

							else if (i == 4) {
								x2 = x1 - 1;
								y2 = y1;
							}

							else {
								x2 = x1 - 1;
								y2 = y1 - 1;
							}

							hexagons[neighbor] = new Couple<Integer, Integer>(x2, y2);
							candidats.add(neighbor);
							checkedHexagons[neighbor] = 1;
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

	}

	public void exportToGraphFile(File file) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		writer.write("p DIMACS " + nbNodes + " " + nbEdges + " " + nbHexagons + "\n");

		for (int i = 0; i < nbNodes; i++) {
			for (int j = (i + 1); j < nbNodes; j++) {
				if (adjacencyMatrix[i][j] == 1) {

					Node u = nodesRefs[i];
					Node v = nodesRefs[j];

					writer.write("e " + u.getX() + "_" + u.getY() + " " + v.getX() + "_" + v.getY() + "\n");
				}
			}
		}

		for (int i = 0; i < nbHexagons; i++) {

			int[] hexagon = hexagons[i];
			writer.write("h ");

			for (int j = 0; j < 6; j++) {

				Node u = nodesRefs[hexagon[j]];
				writer.write(u.getX() + "_" + u.getY() + " ");
			}

			writer.write("\n");
		}

		writer.close();
	}

	public void exportProperties(File file) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		writer.write("molecule_name\t" + description + "\n");
		writer.write("nb_carbons\t" + nbNodes + "\n");
		writer.write("nb_hydrogens\t" + this.getNbHydrogens() + "\n");
		writer.write("nb_hexagons\t" + nbHexagons + "\n");

		String nbKekuleStructures = Double.toString(getNbKekuleStructures()).split(Pattern.quote("."))[0];

		writer.write(
				new String(new String("nb_kekule_structures\t" + nbKekuleStructures).getBytes(), StandardCharsets.UTF_8)
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
			for (int i = 0; i < stats.length; i++)
				writer.write("C" + (i + 1) + " : " + stats[i] + "\n");
		}

		writer.close();

	}

	public BenzenoidSolution buildBenzenoidSolution() {

		ModelPropertySet modelPropertySet = new ModelPropertySet();
		modelPropertySet.getBySubject("hexagons").addExpression(new BinaryNumericalExpression("hexagons", "=", nbHexagons));
//		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();
//		criterions.add(new GeneratorCriterion(Subject.NB_HEXAGONS, Operator.EQ, Integer.toString(nbHexagons)));

		String name = toString();

		String[] split = name.split(Pattern.quote("-"));

		GeneralModel model = new GeneralModel(modelPropertySet);

		for (String s : split) {

			int u = Integer.parseInt(s);
			model.getProblem().arithm(model.getVG()[u], "=", 1).post();
		}

		ResultSolver result = model.solve();

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

		while (candidats.size() > 0) {

			int candidat = candidats.get(0);

			for (int i = 0; i < 6; i++) {
				int neighbor = dualGraph[candidat][i];
				if (neighbor != -1 && checkedHexagons[neighbor] == 0) {

					checkedHexagons[neighbor] = 1;

					int x = hexagonsCoords[candidat].getX();
					int y = hexagonsCoords[candidat].getY();

					int x2, y2;

					if (i == 0) {
						x2 = x;
						y2 = x - 1;
					}

					else if (i == 1) {
						x2 = x + 1;
						y2 = y;
					}

					else if (i == 2) {
						x2 = x + 1;
						y2 = y + 1;
					}

					else if (i == 3) {
						x2 = x;
						y2 = y + 1;
					}

					else if (i == 4) {
						x2 = x - 1;
						y2 = y;
					}

					else {
						x2 = x - 1;
						y2 = y - 1;
					}

					hexagonsCoords[neighbor] = new Couple<>(x2, y2);
					candidats.add(neighbor);
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

	public static Irregularity computeParameterOfIrregularity(Molecule molecule) {

		if (molecule.getNbHexagons() == 1)
			return null;

		int[] N = new int[4];
		int[] checkedNodes = new int[molecule.getNbNodes()];

		ArrayList<Integer> V = new ArrayList<Integer>();

		for (int u = 0; u < molecule.getNbNodes(); u++) {
			int degree = molecule.degree(u);
			if (degree == 2 && !V.contains(u)) {
				V.add(u);
				checkedNodes[u] = 0;
			}

			else if (degree != 2)
				checkedNodes[u] = -1;
		}

		ArrayList<Integer> candidats = new ArrayList<Integer>();

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

			while (candidats.size() > 0) {

				int candidat = candidats.get(0);

				for (int i = 0; i < molecule.getNbNodes(); i++) {
					if (molecule.getAdjacencyMatrix()[candidat][i] == 1 && checkedNodes[i] == 0) {

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
			irregularity = Molecule.computeParameterOfIrregularity(this);

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

			SubGraph subGraph = new SubGraph(getAdjacencyMatrix(), disabledVertices, degrees, PerfectMatchingType.DET);

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
		return adjacencyMatrix[i][j] == 1;
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
	public int compareTo(Molecule arg0) {
		return comparator.compare(this, arg0);
	}

	public void setClarCoverSolution(ClarCoverSolution clarCoverSolution) {
		this.clarCoverSolution = clarCoverSolution;
		clarCoverGroup = new ClarCoverGroup(this, clarCoverSolution);
	}

	public ClarCoverSolution getClarCoverSolution() {
		return clarCoverSolution;
	}

	public ClarCoverGroup getClarCoverGroup() {
		return clarCoverGroup;
	}

	public RBOGroup getRBOGroup() {
		return rboGroup;
	}

	public void setRBOGroup(RBOGroup rboGroup) {
		this.rboGroup = rboGroup;
	}

	private int[][] buildCoordsMatrix(int nbCrowns, int diameter) {

		int[][] coordsMatrix = new int[diameter][diameter];
		for (int i = 0; i < diameter; i++) {
			for (int j = 0; j < diameter; j++) {
				coordsMatrix[i][j] = -1;
			}
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

	public boolean areNeighbors(int hexagon1, int hexagon2) {

		for (int i = 0; i < 6; i++) {
			if (dualGraph[hexagon1][i] == hexagon2)
				return true;
		}

		return false;
	}

	public Fragment convertToPattern(int xShift, int yShift) {

		@SuppressWarnings("unchecked")
		Couple<Integer, Integer>[] shiftCoords = new Couple[nbHexagons];
		int nbNodes = nbHexagons;

		for (int i = 0; i < nbHexagons; i++)
			// shiftCoords[i] = new Couple<>(hexagonsCoords[i].getY() + 0,
			// hexagonsCoords[i].getX() + 0);
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
			for (int j = 0; j < 6; j++)
				neighbors[i][j] = -1;

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

		int[] labels = new int[nbHexagons];

		for (int i = 0; i < nbNodes; i++)
			labels[i] = 2;

		return new Fragment(matrix, labels, nodes, null, null, neighbors, 0);
	}

	public ArrayList<String> translations(Fragment pattern, int diameter, int[][] coordsMatrix,
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
//					StringBuilder name = new StringBuilder();
//					for (int i = 0; i < shiftedCoords.length; i++) {
//						Node node = shiftedCoords[i];
//						int hexagon = coordsMatrix[node.getX()][node.getY()];
//						name.append(hexagon);
//						if (i < shiftedCoords.length - 1)
//							name.append("-");
//					}
//					names.add(name.toString());

					ArrayList<Integer> hexagons = new ArrayList<>();
					for (int i = 0; i < shiftedCoords.length; i++) {
						Node node = shiftedCoords[i];
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

		int nbCrowns = (int) Math.floor((((double) ((double) nbHexagons + 1)) / 2.0) + 1.0);

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

		Fragment pattern = convertToPattern(xShift, yShift);
		ArrayList<Fragment> rotations = pattern.computeRotations();

		for (Fragment f : rotations) {
			ArrayList<String> rotationsNames = translations(f, diameter, coordsMatrix, topBorder, leftBorder);
			for (String name : rotationsNames) {
				if (!names.contains(name))
					names.add(name);
			}
		}

		// for (String name : names)
		// System.out.println(name);

		return names;
	}

	@SuppressWarnings("unchecked")
	private void buildHexagonsCoords2() {
		int[][] dualGraph = this.getDualGraph();

		int[] checkedHexagons = new int[this.getNbHexagons()];

		ArrayList<Integer> candidates = new ArrayList<Integer>();
		candidates.add(0);

		hexagonsCoords = new Couple[nbHexagons];

		checkedHexagons[0] = 1;
		hexagonsCoords[0] = new Couple<Integer, Integer>(0, 0);

		// centersCoords[0] = new Couple<Double, Double>(40.0, 40.0);

		while (candidates.size() > 0) {

			int candidate = candidates.get(0);

			for (int i = 0; i < 6; i++) {

				int n = dualGraph[candidate][i];
				if (n != -1) {
					if (checkedHexagons[n] == 0) {

						int x = hexagonsCoords[candidate].getX();
						int y = hexagonsCoords[candidate].getY();

						// double xCenter = centersCoords[candidate].getX();
						// double yCenter = centersCoords[candidate].getY();

						if (i == 0) {

							x += 0;
							y += -1;

							// xCenter += 26.0;
							// yCenter -= 43.5;
						}

						else if (i == 1) {

							x += 1;
							y += 0;

							// xCenter += 52.0;
							// yCenter += 0.0;
						}

						else if (i == 2) {

							x += 1;
							y += 1;

							// xCenter += 26.0;
							// yCenter += 43.5;
						}

						else if (i == 3) {

							x += 0;
							y += 1;

							// xCenter -= 26.0;
							// yCenter += 43.5;
						}

						else if (i == 4) {

							x += -1;
							y += 0;

							// xCenter -= 52.0;
							// yCenter += 0.0;
						}

						else if (i == 5) {

							x += -1;
							y += -1;

							// xCenter -= 26.0;
							// yCenter -= 43.5;
						}

						checkedHexagons[n] = 1;
						hexagonsCoords[n] = new Couple<Integer, Integer>(x, y);
						// centersCoords[n] = new Couple<Double, Double>(xCenter, yCenter);
						candidates.add(n);
					}
				}
			}

			candidates.remove(candidates.get(0));
		}

	}

	//"/find_ims2d_1a_by_name"
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

			if (results.size() > 0) {
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

			if (results.size() > 0) {
				IRSpectraEntry content = IRSpectraEntry.buildQueryContent(results.get(0));
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

			if (results.size() > 0) {
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

	public static ArrayList<Molecule> union(ArrayList<Molecule> molecules1, ArrayList<Molecule> molecules2) {

		ArrayList<Molecule> molecules = new ArrayList<>();

		for (Molecule molecule : molecules1) {
			if (!molecules.contains(molecule))
				molecules.add(molecule);
		}

		for (Molecule molecule : molecules2) {
			if (!molecules.contains(molecule))
				molecules.add(molecule);
		}

		return molecules;
	}

	public static ArrayList<Molecule> intersection(ArrayList<Molecule> molecules1, ArrayList<Molecule> molecules2) {

		ArrayList<Molecule> molecules = new ArrayList<>();

		for (Molecule molecule : molecules1) {
			if (molecules2.contains(molecule))
				molecules.add(molecule);
		}

		return molecules;
	}

	public static ArrayList<Molecule> diff(ArrayList<Molecule> molecules1, ArrayList<Molecule> molecules2) {

		ArrayList<Molecule> molecules = new ArrayList<>();

		for (Molecule molecule : molecules1) {
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

		Molecule molecule = (Molecule) obj;

		for (String name : molecule.getNames()) {
			if (this.getNames().contains(name))
				return true;
		}

		return false;
	}

	public void setClarCoverSolutions(ArrayList<ClarCoverSolution> clarCoverSolutions) {
		this.clarCoverSolutions = clarCoverSolutions;
		radicalarGroup = new RadicalarClarCoverGroup(this, clarCoverSolutions);
	}

	public ArrayList<ClarCoverSolution> getClarCoverSolutions() {
		return clarCoverSolutions;
	}

	public RadicalarClarCoverGroup getRadicalarGroup() {
		return radicalarGroup;
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

	public IMS2D1AGroup getIMS2D1AGroup() {

		getIms2d1a();

		if (ims2d1aGroup == null) {
			// ims2d1a =
			// "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";
			ims2d1aGroup = new IMS2D1AGroup(ims2d1a);
		}

		return ims2d1aGroup;
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
}
