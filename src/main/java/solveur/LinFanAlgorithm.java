package solveur;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.BoolVar;

import benzenoid.Edge;
import benzenoid.Benzenoid;
import benzenoid.UndirPonderateGraph;
import parsers.GraphParser;
import solveur.Aromaticity.RIType;

public enum LinFanAlgorithm {
    ;

    private static int indexStructure = 0;

	private static final int MAX_CIRCUIT_SIZE = 20;
	private static final double [] rCount = new double[MAX_CIRCUIT_SIZE];
	private static double [][] localAromaticity;

	public static boolean containsNode(ArrayList<Edge> path, int v) {
		for (Edge edge : path) {
			if (edge.getU() == v || edge.getV() == v)
				return true;
		}
		return false;
	}

	public static int getNbDoubleBounds(int[] hexagonDoubleBounds) {
		int sum = 0;

		for (int hexagonDoubleBound : hexagonDoubleBounds)
			if (hexagonDoubleBound == 1)
				sum++;

		return sum;
	}

	public static int[] computeDoubleBounds(UndirPonderateGraph kekuleStructure, int hexagon) {

		int[][] adjacencyMatrix = kekuleStructure.getAdjacencyMatrix();
		int[] hexagonVertices = kekuleStructure.getHexagons()[hexagon];
		int[] hexagonDoubleBounds = new int[6];

		if (adjacencyMatrix[hexagonVertices[0]][hexagonVertices[1]] == 1)
			hexagonDoubleBounds[0] = 1;

		if (adjacencyMatrix[hexagonVertices[1]][hexagonVertices[2]] == 1)
			hexagonDoubleBounds[1] = 1;

		if (adjacencyMatrix[hexagonVertices[2]][hexagonVertices[3]] == 1)
			hexagonDoubleBounds[2] = 1;

		if (adjacencyMatrix[hexagonVertices[3]][hexagonVertices[4]] == 1)
			hexagonDoubleBounds[3] = 1;

		if (adjacencyMatrix[hexagonVertices[4]][hexagonVertices[5]] == 1)
			hexagonDoubleBounds[4] = 1;

		if (adjacencyMatrix[hexagonVertices[5]][hexagonVertices[0]] == 1)
			hexagonDoubleBounds[5] = 1;

		return hexagonDoubleBounds;
	}

	public static int getHexagonConfiguration(UndirPonderateGraph kekuleStructure, int hexagon) {

		int[] hexagonDoubleBounds = computeDoubleBounds(kekuleStructure, hexagon);

		int nbDoubleBounds = getNbDoubleBounds(hexagonDoubleBounds);

		if (nbDoubleBounds == 2) {

			// Case 1
			if ((hexagonDoubleBounds[3] == 1 && hexagonDoubleBounds[5] == 1)
					|| (hexagonDoubleBounds[0] == 1 && hexagonDoubleBounds[4] == 1)
					|| (hexagonDoubleBounds[1] == 1 && hexagonDoubleBounds[5] == 1)
					|| (hexagonDoubleBounds[0] == 1 && hexagonDoubleBounds[2] == 1)
					|| (hexagonDoubleBounds[1] == 1 && hexagonDoubleBounds[3] == 1)
					|| (hexagonDoubleBounds[2] == 1 && hexagonDoubleBounds[4] == 1)) {

				return 1;
			}

			// Case 2
			if ((hexagonDoubleBounds[1] == 1 && hexagonDoubleBounds[4] == 1)
					|| (hexagonDoubleBounds[2] == 1 && hexagonDoubleBounds[5] == 1)
					|| (hexagonDoubleBounds[0] == 1 && hexagonDoubleBounds[3] == 1)) {

				return 2;
			}
		}

		// Case 3
		else if (nbDoubleBounds == 1)
			return 3;

		// Case 4
		else if (nbDoubleBounds == 0)
			return 4;

		// Case 0 (3 double bounds)
		else if (nbDoubleBounds == 3)
			return 0;

		// Default case (error)
		return -1;
	}

	public static ArrayList<Edge> convertNodeSet(ArrayList<Integer> nodeSet) {

		ArrayList<Edge> path = new ArrayList<>();

		for (int i = 0; i < nodeSet.size() - 1; i++)
			path.add(new Edge(nodeSet.get(i), nodeSet.get(i + 1)));

		return path;
	}

	/**
	 * Algorithm 1
	 */
	public static List<ArrayList<Edge>> computeAllPaths(UndirPonderateGraph kekuleStructure, int a, int b) {

		List<ArrayList<Integer>> candidates = new ArrayList<>();
		ArrayList<Integer> firstCandidate = new ArrayList<>();

		firstCandidate.add(a);
		candidates.add(firstCandidate);

		while (true) {

			/*
			 * Looking for new double bounds
			 */

			int curentSize = candidates.get(0).size();
			int nbNewCandidates = 0;

			boolean reachB = false;

			while (true) {

				ArrayList<Integer> candidat = candidates.get(0);

				if (candidat.size() > curentSize)
					break;

				int u = candidat.get(candidat.size() - 1);

				for (int v = 0; v < kekuleStructure.getNbNodes(); v++) {

					if (kekuleStructure.getAdjacencyMatrix()[u][v] == 1 && !candidat.contains(v)) {

						ArrayList<Integer> newCandidat = new ArrayList<>(candidat);

						newCandidat.add(v);
						candidates.add(newCandidat);

						nbNewCandidates++;

						if (v == b)
							reachB = true;
					}
				}

				candidates.remove(0);
			}

			/*
			 * If no longer paths were found, then there is no alternating paths
			 */

			if (nbNewCandidates == 0)
				return new ArrayList<>();

			/*
			 * If we reached the node B
			 */

			if (reachB) {
				List<ArrayList<Edge>> paths = new ArrayList<>();

				for (ArrayList<Integer> candidat : candidates) {
					if (candidat.contains(b)) {
						paths.add(convertNodeSet(candidat));
					}
				}

				return paths;
			}

			/*
			 * Looking for simple bounds
			 */

			nbNewCandidates = 0;
			curentSize = candidates.get(0).size();

			//while (true) {
			while(candidates.size() > 0) {
				
				ArrayList<Integer> candidat = candidates.get(0);

				if (candidat.size() > curentSize)
					break;

				int u = candidat.get(candidat.size() - 1);

				for (int v = 0; v < kekuleStructure.getNbNodes(); v++) {

					if (kekuleStructure.getAdjacencyMatrix()[u][v] == 0 && !candidat.contains(v)) {

						ArrayList<Integer> newCandidat = new ArrayList<>(candidat);

						newCandidat.add(v);
						candidates.add(newCandidat);

						nbNewCandidates++;
					}
				}

				candidates.remove(0);
			}

			if (nbNewCandidates == 0)
				return new ArrayList<>();
		}

	}

	public static List<Edge> computeCircuitCase1(UndirPonderateGraph kekuleStructure, int hexagon) {

		int[] hexagonVertices = kekuleStructure.getHexagons()[hexagon];
		int[] hexagonDoubleBounds = computeDoubleBounds(kekuleStructure, hexagon);

		int v1 = -1;
		int v2 = -1;
		int direction = -1;

		/*
		 * Computing v1 and v2
		 */

		if (hexagonDoubleBounds[3] == 1 && hexagonDoubleBounds[5] == 1) {
			v1 = hexagonVertices[1];
			v2 = hexagonVertices[2];
			direction = 1;
		}

		else if (hexagonDoubleBounds[0] == 1 && hexagonDoubleBounds[4] == 1) {
			v1 = hexagonVertices[2];
			v2 = hexagonVertices[3];
			direction = 2;
		}

		else if (hexagonDoubleBounds[5] == 1 && hexagonDoubleBounds[1] == 1) {
			v1 = hexagonVertices[3];
			v2 = hexagonVertices[4];
			direction = 3;
		}

		else if (hexagonDoubleBounds[0] == 1 && hexagonDoubleBounds[2] == 1) {
			v1 = hexagonVertices[4];
			v2 = hexagonVertices[5];
			direction = 4;
		}

		else if (hexagonDoubleBounds[1] == 1 && hexagonDoubleBounds[3] == 1) {
			v1 = hexagonVertices[5];
			v2 = hexagonVertices[0];
			direction = 5;
		}

		else if (hexagonDoubleBounds[2] == 1 && hexagonDoubleBounds[4] == 1) {
			v1 = hexagonVertices[0];
			v2 = hexagonVertices[1];
			direction = 0;
		}

		/*
		 * Computing paths between v1 and v2
		 */

		List<ArrayList<Edge>> paths = computeAllPaths(kekuleStructure, v1, v2);

		/*
		 * Looking for a straight path
		 */

		List<Edge> straightPath = null;

		for (ArrayList<Edge> path : paths) {

//			int [] nodesPath = new int [kekuleStructure.getMaxIndex() + 1];
//			int [] curentNodes = new int[kekuleStructure.getMaxIndex() + 1];

			int[] nodesPath = new int[kekuleStructure.getNbNodes() + 1];
			int[] curentNodes = new int[kekuleStructure.getNbNodes() + 1];

			for (Edge edge : path) {
				nodesPath[edge.getU()] = 1;
				nodesPath[edge.getV()] = 1;
			}

			int curentHexagon = kekuleStructure.getDualGraph()[hexagon][direction];

			if (curentHexagon != -1) {
				while (true) {

					boolean containsAll = true;

					for (int i = 0; i < 6; i++) {
						int node = kekuleStructure.getHexagons()[curentHexagon][i];

						if (nodesPath[node] == 1)
							curentNodes[node] = 1;
						else {
							containsAll = false;
							break;
						}
					}

					if (!containsAll)
						break;

					boolean pathEquals = true;
					for (int i = 0; i < nodesPath.length; i++) {
						if (nodesPath[i] != curentNodes[i]) {
							pathEquals = false;
							break;
						}
					}

					if (pathEquals) {
						straightPath = path;
						break;
					}

					curentHexagon = kekuleStructure.getDualGraph()[curentHexagon][direction];

					if (curentHexagon == -1)
						break;
				}
			}
			if (straightPath != null)
				break;
		}

		if (straightPath != null) {
			// straightPath.add(new Edge(v1, v2));

			for (int i = 0; i < 6; i++) {
				if (i != direction) {
					int u = hexagonVertices[i];
					int v = hexagonVertices[(i + 1) % 6];

					straightPath.add(new Edge(u, v));
				}
			}

			return straightPath;
		}

		if (paths.size() == 0)
			return null;
		
		ArrayList<Edge> path = paths.get(0);
		path.add(new Edge(v1, v2));

		return path;

	}

	public static List<Edge> computeCircuitCase2(UndirPonderateGraph kekuleStructure, int hexagon) {

		int[] hexagonVertices = kekuleStructure.getHexagons()[hexagon];
		int[] hexagonDoubleBounds = computeDoubleBounds(kekuleStructure, hexagon);

		/*
		 * Computing v1 and v2
		 */

		int v1 = -1, v2 = -1;

		if (hexagonDoubleBounds[1] == 1 && hexagonDoubleBounds[4] == 1) {
			v1 = hexagonVertices[0];
			v2 = hexagonVertices[3];
		}

		else if (hexagonDoubleBounds[2] == 1 && hexagonDoubleBounds[5] == 1) {
			v1 = hexagonVertices[1];
			v2 = hexagonVertices[4];
		}

		else if (hexagonDoubleBounds[0] == 1 && hexagonDoubleBounds[3] == 1) {
			v1 = hexagonVertices[2];
			v2 = hexagonVertices[5];
		}

		/*
		 * Looking for one path between v1 and v2
		 */

		List<ArrayList<Edge>> paths = computeAllPaths(kekuleStructure, v1, v2);

		if (paths.size() == 0)
			return new ArrayList<>();

		List<Edge> path = paths.get(0);

		/*
		 * Adding 3 edges of s
		 */

		int index = -1;
		for (int i = 0; i < hexagonVertices.length; i++) {
			if (hexagonVertices[i] == v1) {
				index = i;
				break;
			}
		}

		int a1 = hexagonVertices[(index + 1) % 6];
		int a2 = hexagonVertices[(index + 2) % 6];

		path.add(new Edge(v1, a1));
		path.add(new Edge(a1, a2));
		path.add(new Edge(a2, v2));

		return path;
	}

	@SuppressWarnings("unused")
	public static List<Edge> computeCircuitCase3(UndirPonderateGraph kekuleStructure, int hexagon) {

		if (indexStructure == 9 && hexagon == 13)
			System.out.print("");

		int[] hexagonVertices = kekuleStructure.getHexagons()[hexagon];
		int[] hexagonDoubleBounds = computeDoubleBounds(kekuleStructure, hexagon);

		/*
		 * Computing v1, v2, v3, v4, v5, v6
		 */

		int v1 = -1, v2 = -1, v3 = -1, v4 = -1, v5 = -1, v6 = -1;

		if (hexagonDoubleBounds[0] == 1) {
			v1 = hexagonVertices[2];
			v2 = hexagonVertices[3];
			v3 = hexagonVertices[4];
			v4 = hexagonVertices[5];
			v5 = hexagonVertices[0];
			v6 = hexagonVertices[1];
		}

		else if (hexagonDoubleBounds[1] == 1) {
			v1 = hexagonVertices[3];
			v2 = hexagonVertices[4];
			v3 = hexagonVertices[5];
			v4 = hexagonVertices[0];
			v5 = hexagonVertices[1];
			v6 = hexagonVertices[2];
		}

		else if (hexagonDoubleBounds[2] == 1) {
			v1 = hexagonVertices[4];
			v2 = hexagonVertices[5];
			v3 = hexagonVertices[0];
			v4 = hexagonVertices[1];
			v5 = hexagonVertices[2];
			v6 = hexagonVertices[3];
		}

		else if (hexagonDoubleBounds[3] == 1) {
			v1 = hexagonVertices[5];
			v2 = hexagonVertices[0];
			v3 = hexagonVertices[1];
			v4 = hexagonVertices[2];
			v5 = hexagonVertices[3];
			v6 = hexagonVertices[4];
		}

		else if (hexagonDoubleBounds[4] == 1) {
			v1 = hexagonVertices[0];
			v2 = hexagonVertices[1];
			v3 = hexagonVertices[2];
			v4 = hexagonVertices[3];
			v5 = hexagonVertices[4];
			v6 = hexagonVertices[5];
		}

		else if (hexagonDoubleBounds[5] == 1) {
			v1 = hexagonVertices[1];
			v2 = hexagonVertices[2];
			v3 = hexagonVertices[3];
			v4 = hexagonVertices[4];
			v5 = hexagonVertices[5];
			v6 = hexagonVertices[0];
		}

		/*
		 * Computing all paths between v1 and v2
		 */

		List<ArrayList<Edge>> paths = computeAllPaths(kekuleStructure, v1, v2);

		/*
		 * Looking for a path between v1 and v2 which contains v3 and v4
		 */

		for (ArrayList<Edge> path : paths) {
			if (containsNode(path, v3) && containsNode(path, v4)) {
				path.add(new Edge(v1, v2));
				return path;
			}
		}

		List<Edge> pV1V2, pV3V4, pV2V3, pV4V1;
		int l1, l2, l3, l4;

		if (paths.size() == 0) {
			pV1V2 = null;
			l1 = 0;
		}

		else {
			pV1V2 = paths.get(0);
			l1 = pV1V2.size();
		}

		/*
		 * Computing all paths between v3 and v4
		 */

		paths = computeAllPaths(kekuleStructure, v3, v4);

		/*
		 * Looking for one path beetween v3 and v4 which contains v1 and v2
		 */

		for (ArrayList<Edge> path : paths) {
			if (containsNode(path, v1) && containsNode(path, v2)) {
				path.add(new Edge(v3, v4));
				return path;
			}
		}

		if (paths.size() == 0) {
			pV3V4 = null;
			l2 = 0;
		}

		else {
			pV3V4 = paths.get(0);
			l2 = pV3V4.size();
		}

		/*
		 * Looking for one path between v2 and v3
		 */

		paths = computeAllPaths(kekuleStructure, v2, v3);

		if (paths.size() == 0) {
			pV2V3 = null;
			l3 = 0;
		} else {
			pV2V3 = paths.get(0);
			l3 = pV2V3.size();
		}

		/*
		 * Looking for a path between v4 and v1
		 */

		paths = computeAllPaths(kekuleStructure, v4, v1);

		if (paths.size() == 0) {
			pV4V1 = null;
			l4 = 0;
		} else {
			pV4V1 = paths.get(0);
			l4 = pV4V1.size();
		}

		boolean path1 = false, path2 = false;

		if (l1 == 0 || l2 == 0) {
			path2 = true;//TODO never used
		}

		if (l3 == 0 || l4 == 0) {
			path1 = true;
		}

		if (l1 != 0 && l2 != 0 && l3 != 0 && l4 != 0) {

			path1 = l1 + l2 <= l3 + l4 - 2;
		}

		if (path1) {
			
			if (pV1V2 == null || pV3V4 == null)
				return null;

			List<Edge> path = new ArrayList<>(pV1V2);
			path.add(new Edge(v2, v3));
			path.addAll(pV3V4);
			path.add(new Edge(v4, v5));
			path.add(new Edge(v5, v6));
			path.add(new Edge(v6, v1));

			return path; // size = L1 + L2 + 4;
		}

		else {
			List<Edge> path = new ArrayList<>();
			path.add(new Edge(v1, v2));
			path.addAll(pV2V3);
			path.add(new Edge(v3, v4));
			path.addAll(pV4V1);

			return path; // size = L3 + L4 + 2
		}

		/*
		 * if (l1 + l2 <= l3 + l4 - 2) { List<Edge> path = new ArrayList<Edge>();
		 * path.addAll(pV1V2); path.add(new Edge(v2, v3)); path.addAll(pV3V4);
		 * path.add(new Edge(v4, v5)); path.add(new Edge(v5, v6)); path.add(new Edge(v6,
		 * v1));
		 * 
		 * return path; // size = L1 + L2 + 4; }
		 * 
		 * else { List<Edge> path = new ArrayList<Edge>(); path.add(new Edge(v1, v2));
		 * path.addAll(pV2V3); path.add(new Edge(v3, v4)); path.addAll(pV4V1);
		 * 
		 * return path; // size = L3 + L4 + 2 }
		 */
	}

	public static List<Edge> computeCircuitCase4(UndirPonderateGraph kekuleStructure, int hexagon) {

		if (indexStructure == 23 && hexagon == 12)
			System.out.print("");

		int[] hexagonVertices = kekuleStructure.getHexagons()[hexagon];

		/*
		 * Initializing v1, v2, v3, v4, v5, v6
		 */

		int v1 = hexagonVertices[0];
		int v2 = hexagonVertices[1];
		int v3 = hexagonVertices[2];
		int v4 = hexagonVertices[3];
		int v5 = hexagonVertices[4];
		int v6 = hexagonVertices[5];

		/*
		 * Testing configuration : P(v1, v2) - P(v3, v4) - P(v5, v6)
		 */

		boolean pathExists = true;

		List<Edge> pV1V2 = new ArrayList<>();
		List<Edge> pV3V4 = new ArrayList<>();
		List<Edge> pV5V6 = new ArrayList<>();

		int l1, l2 = 0, l3 = 0;

		List<ArrayList<Edge>> paths = computeAllPaths(kekuleStructure, v1, v2);

		if (paths.size() == 0) {
			pathExists = false;
			l1 = Integer.MAX_VALUE;
		} else {
			pV1V2 = paths.get(0);
			l1 = pV1V2.size();
		}

		if (pathExists) {

			paths = computeAllPaths(kekuleStructure, v3, v4);

			if (paths.size() == 0) {
				pathExists = false;
				l2 = Integer.MAX_VALUE;
			} else {
				pV3V4 = paths.get(0);
				l2 = pV3V4.size();
			}
		}

		if (pathExists) {

			paths = computeAllPaths(kekuleStructure, v5, v6);

			if (paths.size() == 0) {
				l3 = Integer.MAX_VALUE;
			} else {
				pV5V6 = paths.get(0);
				l3 = pV5V6.size();
			}
		}

		List<Edge> C1 = new ArrayList<>(pV1V2);
		C1.add(new Edge(v2, v3));
		C1.addAll(pV3V4);
		C1.add(new Edge(v4, v5));
		C1.addAll(pV5V6);
		C1.add(new Edge(v6, v1));

		/*
		 * Testing configuration : P(v2, v3) - P(v4, v5) - P(v6, v1)
		 */

		List<Edge> pV2V3 = new ArrayList<>();
		List<Edge> pV4V5 = new ArrayList<>();
		List<Edge> pV6V1 = new ArrayList<>();

		int l4, l5 = 0, l6 = 0;

		pathExists = true;

		paths = computeAllPaths(kekuleStructure, v2, v3);

		if (paths.size() == 0) {
			pathExists = false;
			l4 = Integer.MAX_VALUE;
		} else {
			pV2V3 = paths.get(0);
			l4 = pV2V3.size();
		}

		if (pathExists) {

			paths = computeAllPaths(kekuleStructure, v4, v5);

			if (paths.size() == 0) {
				pathExists = false;
				l5 = Integer.MAX_VALUE;
			} else {
				pV4V5 = paths.get(0);
				l5 = pV4V5.size();
			}
		}

		if (pathExists) {

			paths = computeAllPaths(kekuleStructure, v6, v1);

			if (paths.size() == 0) {
				l6 = Integer.MAX_VALUE;
			} else {
				pV6V1 = paths.get(0);
				l6 = pV6V1.size();
			}
		}

		List<Edge> C2 = new ArrayList<>(pV2V3);
		C2.add(new Edge(v3, v4));
		C2.addAll(pV4V5);
		C2.add(new Edge(v5, v6));
		C2.addAll(pV6V1);
		C2.add(new Edge(v1, v2));

		if (l1 + l2 + l3 > l4 + l5 + l6)
			return C2;
		else
			return C1;

	}

	public static void computeCircuits(UndirPonderateGraph kekuleStructure) {

		//kekuleStructure.displayDoubleBounds();
		
		for (int hexagon = 0; hexagon < kekuleStructure.getNbHexagons(); hexagon++) {

			int configuration = getHexagonConfiguration(kekuleStructure, hexagon);
			List<Edge> circuit = null;

			if (configuration == 0) {
				localAromaticity[hexagon][0]++;
			}

			else if (configuration == 1)
				circuit = computeCircuitCase1(kekuleStructure, hexagon);

			else if (configuration == 2)
				circuit = computeCircuitCase2(kekuleStructure, hexagon);

			else if (configuration == 3)
				circuit = computeCircuitCase3(kekuleStructure, hexagon);

			else
				circuit = computeCircuitCase4(kekuleStructure, hexagon);

			if (circuit != null && circuit.size() > 0) {
				
				int ri = (circuit.size() - 2) / 4;
				localAromaticity[hexagon][ri - 1]++;
			}
		}
	}

	public static Aromaticity computeEnergy(Benzenoid graph) throws IOException {

		localAromaticity = new double[graph.getNbHexagons()][MAX_CIRCUIT_SIZE];

		/*
		 * Generating all Kekulé's structures
		 */

		Model model = new Model("Kekulé Structures");

		BoolVar[] edges = new BoolVar[graph.getNbBonds()];

		for (int i = 0; i < graph.getNbBonds(); i++) {
			edges[i] = model.boolVar("edge " + (i + 1));
		}

		for (int i = 0; i < graph.getEdgeLists().size(); i++) {
			int nbAdjacentEdges = graph.getEdgeLists().get(i).size();
			BoolVar[] adjacentEdges = new BoolVar[nbAdjacentEdges];

			for (int j = 0; j < nbAdjacentEdges; j++) {
				adjacentEdges[j] = edges[graph.getEdgeLists().get(i).get(j)];
			}

			model.sum(adjacentEdges, "=", 1).post();
		}

		model.getSolver().setSearch(new IntStrategy(edges, new FirstFail(model), new IntDomainMin()));
		Solver solver = model.getSolver();

		while (solver.solve()) {
			Solution solution = new Solution(model);
			solution.record();

			int[] edgesValues = new int[graph.getNbBonds()];

			for (int j = 0; j < graph.getNbBonds(); j++) {
				edgesValues[j] = solution.getIntVal(edges[j]);
			}

			/*
			 * Computing the curent Kekulé's structure
			 */

			UndirPonderateGraph kekuleStructure = GraphParser.exportSolutionToPonderateGraph(graph, edgesValues);

			computeCircuits(kekuleStructure);

			indexStructure++;
		}

		double energy = 0;
		for (int index = 0; index < rCount.length; index++) {
			//System.out.print("(" + rCount[index] + " * R" + (index + 1) + ")");
			energy += rCount[index] * ((double) 1 / ((index + 1) * (index + 1)));
		}
		energy = energy / (double) (indexStructure);// TODO never used

		return new Aromaticity(graph, localAromaticity, RIType.NORMAL);
	}

	private static void usage() {
		System.err.println("USAGE: java -jar jarfile.jar filename");
	}
	
	public static void main(String[] args) throws IOException {

		if (args.length == 0) {
			usage();
			System.exit(1);
		}
		
		Benzenoid molecule = GraphParser.parseUndirectedGraph(args[0],
				null, false);

		long begin = System.currentTimeMillis();
		assert molecule != null;
		Aromaticity aro = computeEnergy(molecule);
		long end = System.currentTimeMillis();
		
		System.out.println("LOCAL ENERGY");
		for (int i = 0 ; i < molecule.getNbHexagons() ; i++) {	
			//System.out.println("H" + i + " : " + aro.getLocalAromaticity()[i]);
			System.out.print("H" + i + " : ");
			for (Double d : aro.getLocalCircuits()[i])
				System.out.print(d + " ");
			System.out.println();
		}
		
		System.out.println("\nkekule_structures : " + molecule.getNbKekuleStructures() + "\n");
		
		System.out.println("NORMALIZED RESULTS");
		for (int i = 0 ; i < molecule.getNbHexagons() ; i++) {	
			System.out.println("H" + i + " : " + aro.getLocalAromaticity()[i] / molecule.getNbKekuleStructures());
		}
		
		System.out.println("time : " + (end - begin) + " ms.");
	}
}
