package solveur;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.UndirectedGraphVar;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;
import benzenoid.Benzenoid;
import benzenoid.Node;
import benzenoid.NodeSameLine;
import benzenoid.SubGraph;
import parsers.GraphParser;
import solveur.Aromaticity.RIType;
import utils.EdgeSet;
import utils.Interval;
import utils.SubMolecule;
import utils.Utils;

public enum LinAlgorithm {
	;

	public enum PerfectMatchingType {
		CHOCO, DET
	}

	private static final int MAX_CYCLE_SIZE = 4;
	public static int[][][] energies = new int[127][11][4];
	public static double [][] circuits;
	public static double [] circuitCount;
	private static boolean verbose = false;

	public static void computeResonanceEnergy(Benzenoid molecule, PerfectMatchingType type) {

		energies = Utils.initEnergies();
		circuits = new double[molecule.getNbHexagons()][MAX_CYCLE_SIZE];
		circuitCount = new double [energies.length];

		int[] firstVertices = new int[molecule.getNbBonds()];
		int[] secondVertices = new int[molecule.getNbBonds()];

		Model model = new Model("Cycles");

		UndirectedGraph GLB = new UndirectedGraph(model, molecule.getNbCarbons(), SetType.BITSET, false);
		UndirectedGraph GUB = new UndirectedGraph(model, molecule.getNbCarbons(), SetType.BITSET, false);

		for (int i = 0; i < molecule.getNbCarbons(); i++) {
			GUB.addNode(i);

			for (int j = (i + 1); j < molecule.getNbCarbons(); j++) {
				if (molecule.getEdgeMatrix()[i][j] == 1) {
					GUB.addEdge(i, j);
				}
			}
		}

		UndirectedGraphVar g = model.graphVar("g", GLB, GUB);

		BoolVar[] boolEdges = new BoolVar[molecule.getNbBonds()];

		int index = 0;
		for (int i = 0; i < molecule.getNbCarbons(); i++) {
			for (int j = (i + 1); j < molecule.getNbCarbons(); j++) {

				if (molecule.getEdgeMatrix()[i][j] == 1) {
					boolEdges[index] = model.boolVar("(" + i + "--" + j + ")");
					model.edgeChanneling(g, boolEdges[index], i, j).post();
					firstVertices[index] = i;
					secondVertices[index] = j;
					index++;
				}
			}
		}

		model.minDegree(g, 2).post();
		model.maxDegree(g, 2).post();
		model.connected(g).post();

		model.or(model.and(model.nbNodes(g, model.intVar(6)), model.sum(boolEdges, "=", 6)),
				model.and(model.nbNodes(g, model.intVar(10)), model.sum(boolEdges, "=", 10)),
				model.and(model.nbNodes(g, model.intVar(14)), model.sum(boolEdges, "=", 14)),
				model.and(model.nbNodes(g, model.intVar(18)), model.sum(boolEdges, "=", 18)),
				model.and(model.nbNodes(g, model.intVar(22)), model.sum(boolEdges, "=", 22)),
				model.and(model.nbNodes(g, model.intVar(26)), model.sum(boolEdges, "=", 26))).post();

		model.getSolver().setSearch(new IntStrategy(boolEdges, new FirstFail(model), new IntDomainMin()));
		Solver solver = model.getSolver();

		Solution solution;

		while (solver.solve()) {
			solution = new Solution(model);
			solution.record();

			ArrayList<Integer> cycle = new ArrayList<>();

			for (int i = 0; i < boolEdges.length; i++) {
				if (solution.getIntVal(boolEdges[i]) == 1) {
					cycle.add(firstVertices[i]);
					cycle.add(secondVertices[i]);
				}
			}

			treatCycle(molecule, cycle, type);
		}
	}

	public static void printResults(long time) {

		System.out.println("\nLOCAL ENERGY:");

		long [] globalEnergy = new long[MAX_CYCLE_SIZE];

		for (int i = 0; i < circuits.length; i++) {
			System.out.print("H" + i + " : ");

			for (int j = 0; j < MAX_CYCLE_SIZE; j++) {

				System.out.print(circuits[i][j] + " ");
				globalEnergy[j] += circuits[i][j];
			}

			System.out.println();
		}

		System.out.print("\nGLOBAL ENERGY: ");

		for (long l : globalEnergy) System.out.print(l + " ");

		System.out.print("\n# Resolution time: " + time + " ms.");
	}

	public static EdgeSet computeStraightEdges(Benzenoid molecule, ArrayList<Integer> cycle) {

		List<Node> firstVertices = new ArrayList<>();
		List<Node> secondVertices = new ArrayList<>();

		for (int i = 0; i < cycle.size() - 1; i += 2) {
			int uIndex = cycle.get(i);
			int vIndex = cycle.get(i + 1);

			Node u = molecule.getNodesCoordinates()[uIndex];
			Node v = molecule.getNodesCoordinates()[vIndex];

			if (u.getX() == v.getX()) {
				firstVertices.add(u);
				secondVertices.add(v);
			}
		}

		return new EdgeSet(firstVertices, secondVertices);
	}

	public static List<Interval> computeIntervals(EdgeSet edges) {

		List<Interval> intervals = new ArrayList<>();

		int[] edgesOK = new int[edges.size()];

		for (int i = 0; i < edges.size(); i++) {
			if (edgesOK[i] == 0) {
				edgesOK[i] = 1;
				Node u1 = edges.getFirstVertices().get(i);
				Node v1 = edges.getSecondVertices().get(i);

				int y1 = Math.min(u1.getY(), v1.getY());
				int y2 = Math.max(u1.getY(), v1.getY());

				List<NodeSameLine> sameLineNodes = new ArrayList<>();

				for (int j = (i + 1); j < edges.size(); j++) {
					if (edgesOK[j] == 0) {
						Node u2 = edges.getFirstVertices().get(j);
						Node v2 = edges.getSecondVertices().get(j);

						int y3 = Math.min(u2.getY(), v2.getY());
						int y4 = Math.max(u2.getY(), v2.getY());

						if (y1 == y3 && y2 == y4) {
							edgesOK[j] = 1;
							sameLineNodes.add(new NodeSameLine(j, u2.getX()));
						}
					}
				}

				sameLineNodes.add(new NodeSameLine(i, u1.getX()));
				Collections.sort(sameLineNodes);

				for (int j = 0; j < sameLineNodes.size(); j += 2) {

					NodeSameLine nsl1 = sameLineNodes.get(j);
					NodeSameLine nsl2 = sameLineNodes.get(j + 1);

					Node n1 = edges.getFirstVertices().get(nsl1.getIndex());
					Node n2 = edges.getSecondVertices().get(nsl1.getIndex());
					Node n3 = edges.getFirstVertices().get(nsl2.getIndex());
					Node n4 = edges.getSecondVertices().get(nsl2.getIndex());

					intervals.add(new Interval(n1, n2, n3, n4));
				}

			}
		}

		return intervals;
	}

	public static SubMolecule substractCycleAndInterior(Benzenoid molecule,
                                                        ArrayList<Interval> intervals) {

		int[][] newGraph = new int[molecule.getNbCarbons()][molecule.getNbCarbons()];
		int[] vertices = new int[molecule.getNbCarbons()];
		int[] subGraphVertices = new int[molecule.getNbCarbons()];

		List<Integer> hexagons = getHexagons(molecule, intervals);

		for (Integer hexagon : hexagons) {
			int[] nodes = molecule.getHexagons()[hexagon];

			for (int node : nodes) vertices[node] = 1;
		}

		int subGraphNbNodes = 0;

		int nbEdges = 0;

		for (int u = 0; u < molecule.getNbCarbons(); u++) {
			if (vertices[u] == 0) {
				for (int v = (u + 1); v < molecule.getNbCarbons(); v++) {
					if (vertices[v] == 0) {
						newGraph[u][v] = molecule.getEdgeMatrix()[u][v];
						newGraph[v][u] = molecule.getEdgeMatrix()[v][u];

						if (molecule.getEdgeMatrix()[u][v] == 1)
							nbEdges++;

						if (subGraphVertices[u] == 0) {
							subGraphVertices[u] = 1;
							subGraphNbNodes++;
						}

						if (subGraphVertices[v] == 0) {
							subGraphVertices[v] = 1;
							subGraphNbNodes++;
						}
					}
				}
			}
		}

		return new SubMolecule(subGraphNbNodes, nbEdges, molecule.getNbCarbons(), newGraph);
	}

	public static boolean intervalsOnSameLine(Interval i1, Interval i2) {
		return (i1.y1() == i2.y1() && i1.y2() == i2.y2());
	}

	public static List<Integer> getHexagons(Benzenoid molecule,
                                            ArrayList<Interval> intervals) {
		List<Integer> hexagons = new ArrayList<>();

		for (Interval interval : intervals) {

			int[] hexagonsCount = new int[molecule.getNbHexagons()];

			for (int x = interval.x1(); x <= interval.x2(); x += 2) {
				int u1 = molecule.getMatrixCoordinates().get(x, interval.y1());
				int u2 = molecule.getMatrixCoordinates().get(x, interval.y2());

				if (u1 == -1 || u2 == -1)
					return new ArrayList<>();
				
				for (Integer hexagon : molecule.getHexagonsVertices().get(u1)) {
					hexagonsCount[hexagon]++;
					if (hexagonsCount[hexagon] == 4)
						hexagons.add(hexagon);
				}

				for (Integer hexagon : molecule.getHexagonsVertices().get(u2)) {
					hexagonsCount[hexagon]++;
					if (hexagonsCount[hexagon] == 4)
						hexagons.add(hexagon);
				}
			}
		}

		return hexagons;
	}

	public static String displayCycle(ArrayList<Integer> cycle) {
		ArrayList<Integer> list = new ArrayList<>();
		for (Integer i : cycle) {
			if (!list.contains(i))
				list.add(i);
		}
		Collections.sort(list);
		return list.toString();
	}

	public static void treatCycle(Benzenoid molecule, ArrayList<Integer> cycle, PerfectMatchingType type) {

		EdgeSet verticalEdges = computeStraightEdges(molecule, cycle);
		ArrayList<Interval> intervals = (ArrayList<Interval>) computeIntervals(verticalEdges);
		Collections.sort(intervals);
		int cycleConfiguration = Utils.identifyCycle(molecule, intervals);

		if (cycleConfiguration != -1) {

			circuitCount[cycleConfiguration]++;

			ArrayList<Integer> hexagons = (ArrayList<Integer>) getHexagons(molecule, intervals);

			if (hexagons.size() > 0) {

				double nbPerfectMatchings = 0;

				ArrayList<Integer> circuitForDeterminant = new ArrayList<>();
				int[] checkedCarbons = new int[molecule.getNbCarbons()];
				for (Integer hexagon : hexagons) {
					for (int i = 0; i < 6; i++) {
						int u = molecule.getHexagons()[hexagon][i];
						if (checkedCarbons[u] == 0) {
							checkedCarbons[u] = 1;
							circuitForDeterminant.add(u);
						}
					}
				}

				SubGraph subGraph = RispoliAlgorithm.removeCircuit(molecule, circuitForDeterminant, type);

				switch (type) {

				case DET:

					case CHOCO:
						nbPerfectMatchings = subGraph.getNbPerfectMatchings();
					break;

				}

				if (verbose)
					System.out.println(hexagons + " -> " + nbPerfectMatchings);

				int[][] energiesCycle = energies[cycleConfiguration];

				for (int idHexagon = 0; idHexagon < hexagons.size(); idHexagon++) {

					int hexagon = hexagons.get(idHexagon);
					for (int size = 0; size < 4; size++) {

						if (energiesCycle[idHexagon][size] != 0)
							circuits[hexagon][size] += (double)(energiesCycle[idHexagon][size]) * nbPerfectMatchings;
					
						
					}
				}
			
			}

		}
	}

	public static Aromaticity solve(Benzenoid molecule, PerfectMatchingType type) throws IOException {
		computeResonanceEnergy(molecule, type);
		return new Aromaticity(molecule, circuits, RIType.OPTIMIZED);
	}

	private static void printHeader(String filename, PerfectMatchingType type) {

		System.out.println("# Lin algorithm");
		System.out.println("# Molecule file: " + filename);
		System.out.print("# Perfect matching method: ");

		switch (type) {

		case DET:
			System.out.println("Rispoli algorithm");
			break;

		case CHOCO:
			System.out.println("CP model");
			break;
		}

		System.out.println("# Verbose: " + verbose);

	}

	public static void main(String[] args) throws IOException {

		if (args.length < 2) {
			System.err.println("ERROR: invalid argument(s)");
			System.err.println(
					"USAGE: java -jar LinAlgorithm.jar ${input_file_name} ${matching_computation_type)");
			System.err.println("${matching_computation_type}: 0 (matrix determinant), 1 (choco)");
			System.exit(1);
		}

		String path = args[0];

		PerfectMatchingType type;

		if ("0".equals(args[1]))
			type = PerfectMatchingType.DET;

		else
			type = PerfectMatchingType.CHOCO;

		verbose = args.length >= 3 && ("-v".equals(args[2]) || "--verbose".equals(args[2]));

		printHeader(path, type);

		Benzenoid molecule = GraphParser.parseUndirectedGraph(path, null, false);

		long begin = System.currentTimeMillis();
		assert molecule != null;
		computeResonanceEnergy(molecule, type);
		long end = System.currentTimeMillis();
		long time = end - begin;
		printResults(time);

	}
}
