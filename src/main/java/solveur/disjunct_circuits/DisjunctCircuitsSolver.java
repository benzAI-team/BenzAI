package solveur.disjunct_circuits;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.UndirectedGraphVar;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;

import molecules.Molecule;
import molecules.Node;
import molecules.NodeSameLine;
import molecules.SubGraph;
import parsers.GraphParser;
import solveur.LinAlgorithm;
import solveur.LinAlgorithm.PerfectMatchingType;
import utils.Couple;
import utils.EdgeSet;
import utils.Interval;

public class DisjunctCircuitsSolver {

	private static int depth;
	private static int[] indexes;

	private static CircuitModel[] models;

	private static ArrayList<ArrayList<ArrayList<Integer>>> disjunctCycles;
	private static ArrayList<ArrayList<Integer>> curentCycles;

	private static Stack<ArrayList<Integer>> deletedHexagons;

	private static SubGraph subGraph;

	private static double nbKekuleStructures;

	private static void treatCycle(Molecule molecule, SubGraph subGraph, ArrayList<Integer> cycle) {

		int[][] circuit = new int[molecule.getNbNodes()][molecule.getNbNodes()];

		int[] checkedNodes = new int[molecule.getNbNodes()];

		for (int i = 0; i < molecule.getNbNodes(); i++)
			checkedNodes[i] = 1;

		for (Integer i : cycle)
			checkedNodes[i] = 0;

		EdgeSet verticalEdges = computeStraightEdges(molecule, cycle);
		ArrayList<Interval> intervals = computeIntervals(molecule, subGraph, cycle,
				verticalEdges);
		Collections.sort(intervals);

		ArrayList<Integer> hexagons = (ArrayList<Integer>) getHexagons(molecule, cycle, intervals);
		Collections.sort(hexagons);

		ArrayList<Integer> hexagonsBorder = getHexagonsBorderCycle(molecule, cycle, hexagons);

		int[] checkedHexagons = new int[molecule.getNbHexagons()];

		for (int i = 0; i < molecule.getNbHexagons(); i++)
			checkedHexagons[i] = 1;

		for (Integer i : hexagonsBorder)
			checkedHexagons[i] = 0;

		int nbCheckedHexagons = 0;

		while (nbCheckedHexagons < hexagonsBorder.size()) {

			int hexagonIndex = findTopLeftHexagon(molecule, hexagonsBorder, checkedHexagons);
			int[] hexagon = molecule.getHexagon(hexagonIndex);

			int u = hexagon[0];
			int v = hexagon[5];

			checkedHexagons[hexagonIndex] = 1;
			nbCheckedHexagons++;

			circuit[u][v]++;
			checkedNodes[u] = 1;

			u = v;

			while (checkedNodes[v] != 1) {
				checkedNodes[u] = 1;

				Node nu = molecule.getNodeRef(u);
				Node nv1 = null;
				Node nv2 = null;

				for (Integer n : cycle) {

					if (molecule.getEdgeMatrix()[u][n] == 1) {

						if (nv1 == null)
							nv1 = molecule.getNodeRef(n);

						else
							nv2 = molecule.getNodeRef(n);
					}
				}
				
				
			}
		}
	}

	private static Node findConfiguration(Node nu, Node nv1, Node nv2) {
		
		int x = nu.getX();
		int y = nu.getY();
		
		int x1 = nv1.getX();
		int y1 = nv1.getY();
		
		int x2 = nv2.getX();
		int y2 = nv2.getY();
		
		/*
		 * Case 1
		 */
		
		if (x1 == x-1 && y1 == y + 1 && x2 == x + 1 && y2 == y + 1)
			return nv1;
		
		else if (x2 == x - 1 && y2 == y + 1 && x1 == x + 1 && y1 == y + 1)
			return nv2;
		
		/*
		 * Case 2
		 */
		
		else if (x1 == x - 1 && y1 == y -1 && x2 == x && y2 == y + 1)
			return nv1;
		
		else if (x2 == x - 1 && y2 == y - 1 && x1 == x && y1 == y + 1)
			return nv2;
		
		/*
		 * Case 3
		 */
		
		else if (x1 == x && y1 == y - 1 && x2 == x - 1 && y2 == y + 1)
			return nv1;
		
		else if (x2 == x && y2 == y - 1 && x1 == x - 1 && y1 == y + 1)
			return nv2;

		/*
		 * Case 4
		 */
		
		else if (x1 == x - 1 && y1 == y - 1 && x2 == x + 1 && y2 == y - 1)
			return nv2;
		
		else if (x2 == x - 1 && y2 == y - 1 && x1 == x + 1 && y1 == y  - 1)
			return nv1;

		/*
		 * Case 5
		 */
		
		else if (x1 == x && y1 == y - 1 && x2 == x + 1 && y2 == y + 1)
			return nv2;
		
		else if (x2 == x && y2 == y - 1 && x1 == x + 1 && y1 == y + 1)
			return nv1;
		
		/*
		 * Case 6
		 */
		
		else if (x1 == x && y1 == y + 1 && x2 == x + 1 && y2 == y - 1)
			return nv1;
		
		else if (x2 == x && y2 == y + 1 && x1 == x + 1 && y1 == y - 1)
			return nv2;
		
		return null;
	}
	
	private static int findTopLeftHexagon(Molecule molecule, ArrayList<Integer> hexagonsBorder, int[] checkedHexagons) {

		int hexagon = -1;

		int minY = Integer.MAX_VALUE;
		int minX = Integer.MAX_VALUE;

		ArrayList<Integer> highierHexagons = new ArrayList<>();

		for (Integer i : hexagonsBorder) {
			if (checkedHexagons[i] == 0) {
				Couple<Integer, Integer> coords = molecule.getHexagonsCoords()[i];

				if (minY < coords.getY())
					minY = coords.getY();
			}
		}

		for (Integer i : hexagonsBorder) {
			if (checkedHexagons[i] == 0) {
				Couple<Integer, Integer> coords = molecule.getHexagonsCoords()[i];
				if (coords.getY() == minY)
					highierHexagons.add(i);
			}
		}

		for (Integer i : highierHexagons) {
			if (checkedHexagons[i] == 0) {
				Couple<Integer, Integer> coords = molecule.getHexagonsCoords()[i];
				if (coords.getX() < minX) {
					minX = coords.getX();
					hexagon = i;
				}
			}
		}

		return hexagon;
	}

	private static ArrayList<Integer> getHexagonsBorderCycle(Molecule molecule, ArrayList<Integer> cycle,
			ArrayList<Integer> allHexagons) {

		ArrayList<Integer> hexagons = new ArrayList<>();
		int[] checkedNodes = new int[molecule.getNbNodes()];

		for (Integer u : cycle) {
			if (checkedNodes[u] == 0) {
				checkedNodes[u] = 1;
				for (int i = 0; i < molecule.getNbHexagons(); i++) {
					int[] hexagon = molecule.getHexagon(i);
					for (int j = 0; j < hexagon.length; j++) {
						if (hexagon[j] == u) {
							if (!hexagons.contains(j))
								hexagons.add(j);
						}
					}
				}
			}
		}

		return hexagons;
	}

	private static void displayCycle(Molecule molecule) {

		System.out.print("{ ");

		for (ArrayList<Integer> cycle : curentCycles) {

			EdgeSet verticalEdges = computeStraightEdges(molecule, cycle);
			ArrayList<Interval> intervals = computeIntervals(molecule, subGraph, cycle,
					verticalEdges);
			Collections.sort(intervals);

			ArrayList<Integer> hexagons = (ArrayList<Integer>) getHexagons(molecule, cycle, intervals);
			Collections.sort(hexagons);

			System.out.print("{");

			for (Integer h : hexagons) {
				System.out.print(h + ", ");
			}

			System.out.print("} ");
		}

		System.out.println("}");
	}

	@SuppressWarnings("unchecked")
	private static void removeHexagons(Molecule molecule, ArrayList<Integer> cycle) {
		EdgeSet verticalEdges = computeStraightEdges(molecule, cycle);
		ArrayList<Interval> intervals = computeIntervals(molecule, subGraph, cycle,
				verticalEdges);
		Collections.sort(intervals);

		ArrayList<Integer> hexagons = (ArrayList<Integer>) getHexagons(molecule, cycle, intervals);
		Collections.sort(hexagons);

		if (hexagons.size() == 2 && hexagons.contains(1))
			System.out.print("");

		ArrayList<Integer> hexagonsToRemove = new ArrayList<>();
		int minHexagon = hexagons.get(0);

		for (int i = 0; i < minHexagon; i++) {
			if (!subGraph.isHexagonDisabled(i))
				hexagonsToRemove.add(i);
		}

		hexagonsToRemove.addAll(hexagons);

		int[][] dualGraph = molecule.getDualGraph();

		for (Integer hexagon : hexagons) {
			for (int i = 0; i < 6; i++) {
				int index = dualGraph[hexagon][i];
				if (index != -1) {
					if (!hexagonsToRemove.contains(index) && !subGraph.isHexagonDisabled(index)) {
						hexagonsToRemove.add(index);
					}
				}
			}
		}

		for (Integer hexagon : hexagonsToRemove)
			if (!subGraph.isHexagonDisabled(hexagon))
				subGraph.disableHexagon(hexagon);

		deletedHexagons.add((ArrayList<Integer>) hexagonsToRemove.clone());
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<ArrayList<ArrayList<Integer>>> computeDisjunctCycles(Molecule molecule,
			ArrayList<Integer> mainCycle, int maxNbCycles) {

		/*
		 * Initializing structures
		 */

		depth = 0;
		indexes = new int[maxNbCycles];

		disjunctCycles = new ArrayList<>();
		curentCycles = new ArrayList<>();
		curentCycles.add(mainCycle);

		deletedHexagons = new Stack<>();

		subGraph = buildSubGraph(molecule);

		removeHexagons(molecule, mainCycle);

		nbKekuleStructures = SubGraph.nbKekuleStructures(subGraph);
		models = new CircuitModel[maxNbCycles];

		/*
		 * Main loop
		 */

		while (true) {

			if (models[depth] == null) {
				if (subGraph.getNbDisabledHexagons() < subGraph.getNbTotalHexagons())
					models[depth] = new CircuitModel(subGraph, Integer.toString(depth));
			}

			CircuitModel model = models[depth];

			ArrayList<Integer> chosenCycle = null;

			if (model != null)
				chosenCycle = model.nextCycle();

			if (chosenCycle == null) {
				backtrack();
				if (depth < 0)
					break;
			}

			else {

				nbKekuleStructures = SubGraph.nbKekuleStructures(subGraph);
				if (nbKekuleStructures == 0) {
					backtrack();
					if (depth < 0)
						break;
				}

				else {
					indexes[depth]++;
					depth++;

					curentCycles.add(chosenCycle);
					ArrayList<ArrayList<Integer>> disjunctCycle = (ArrayList<ArrayList<Integer>>) curentCycles.clone();
					disjunctCycles.add(disjunctCycle);

					displayCycle(molecule);

					removeHexagons(molecule, chosenCycle);
				}
			}
		}

		return disjunctCycles;
	}

	public static void backtrack() {

		ArrayList<Integer> hexagonsToRestore = deletedHexagons.pop();

		for (Integer hexagon : hexagonsToRestore)
			if (subGraph.isHexagonDisabled(hexagon))
				subGraph.enableHexagon(hexagon);

		indexes[depth] = 0;
		models[depth] = null;
		depth--;

		curentCycles.remove(curentCycles.size() - 1);

		nbKekuleStructures = SubGraph.nbKekuleStructures(subGraph);
	}

	public static List<Integer> getHexagons(Molecule molecule, ArrayList<Integer> cycle,
			ArrayList<Interval> intervals) {
		List<Integer> hexagons = new ArrayList<Integer>();

		for (Interval interval : intervals) {

			int[] hexagonsCount = new int[molecule.getNbHexagons()];

			for (int x = interval.x1(); x <= interval.x2(); x += 2) {
				int u1 = molecule.getCoords().get(x, interval.y1());
				int u2 = molecule.getCoords().get(x, interval.y2());

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

	public static ArrayList<Interval> computeIntervals(Molecule molecule, SubGraph subGraph, ArrayList<Integer> cycle,
			EdgeSet edges) {

		ArrayList<Interval> intervals = new ArrayList<Interval>();

		int[] edgesOK = new int[edges.size()];

		for (int i = 0; i < edges.size(); i++) {
			if (edgesOK[i] == 0) {
				edgesOK[i] = 1;
				Node u1 = edges.getFirstVertices().get(i);
				Node v1 = edges.getSecondVertices().get(i);

				int y1 = Math.min(u1.getY(), v1.getY());
				int y2 = Math.max(u1.getY(), v1.getY());

				List<NodeSameLine> sameLineNodes = new ArrayList<NodeSameLine>();

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

	/**
	 * Returns the index'th cycle of subGraph, null otherwise
	 * 
	 * @param subGraph
	 * @param index
	 * @return
	 */
	public static ArrayList<Integer> getCycle(SubGraph subGraph, int index) {

		int nbNodes = subGraph.getNbEnabledNodes();
		int nbEdges = subGraph.getNbEdges();

		int[] firstVertices = new int[nbEdges];
		int[] secondVertices = new int[nbEdges];

		Model model = new Model("Cycles");

		int[] correspondances1 = new int[subGraph.getNbNodes()];
		int[] correspondances2 = new int[subGraph.getNbEnabledNodes()];

		for (int i = 0; i < subGraph.getNbNodes(); i++) {
			correspondances1[i] = -1;
			if (i < correspondances2.length)
				correspondances2[i] = -1;
		}

		UndirectedGraph GLB = new UndirectedGraph(model, nbNodes, SetType.BITSET, false);
		UndirectedGraph GUB = new UndirectedGraph(model, nbNodes, SetType.BITSET, false);

		int indexNode = 0;
		for (int i = 0; i < subGraph.getNbNodes(); i++) {
			if (!subGraph.isDisabled(i)) {
				GUB.addNode(indexNode);
				correspondances1[i] = indexNode;
				correspondances2[indexNode] = i;
				indexNode++;
			}
		}

		for (int i = 0; i < subGraph.getNbNodes(); i++) {
			for (int j = (i + 1); j < subGraph.getNbNodes(); j++) {
				if (subGraph.containsEdge(i, j)) {
					int u = correspondances1[i];
					int v = correspondances1[j];
					GUB.addEdge(u, v);
				}
			}
		}

		UndirectedGraphVar g = model.graphVar("g", GLB, GUB);
		BoolVar[] boolEdges = new BoolVar[nbEdges];

		int edgeIndex = 0;
		for (int i = 0; i < subGraph.getNbNodes(); i++) {
			if (!subGraph.isDisabled(i)) {

				for (int j = (i + 1); j < subGraph.getNbNodes(); j++) {
					if (!subGraph.isDisabled(j) && subGraph.containsEdge(i, j)) {

						int u = correspondances1[i];
						int v = correspondances1[j];

						boolEdges[edgeIndex] = model.boolVar("edge[" + i + "][" + j + "]");
						model.edgeChanneling(g, boolEdges[edgeIndex], u, v).post();
						firstVertices[edgeIndex] = i;
						secondVertices[edgeIndex] = j;
						edgeIndex++;
					}
				}
			}
		}

		model.minDegree(g, 2).post();
		model.maxDegree(g, 2).post();
		model.connected(g).post();

		int sum = 2;
		int maxCycleSize = (int) Math.ceil((double) nbNodes / 2.0);

		Constraint[] or = new Constraint[maxCycleSize];
		for (int i = 0; i < maxCycleSize; i++) {
			sum += 4;
			or[i] = model.and(model.nbNodes(g, model.intVar(sum)), model.sum(boolEdges, "=", sum));
		}

		model.or(or).post();

		model.getSolver().setSearch(new IntStrategy(boolEdges, new FirstFail(model), new IntDomainMin()));
		Solver solver = model.getSolver();
		Solution solution;
		int indexSolution = 0;

		while (solver.solve()) {

			solution = new Solution(model);
			solution.record();

			if (indexSolution == index) {
				ArrayList<Integer> cycle = new ArrayList<Integer>();

				for (int i = 0; i < boolEdges.length; i++) {
					if (solution.getIntVal(boolEdges[i]) == 1) {
						cycle.add(firstVertices[i]);
						cycle.add(secondVertices[i]);
					}
				}
				return cycle;
			}

			indexSolution++;
		}

		return null;
	}

	public static EdgeSet computeStraightEdges(Molecule molecule, ArrayList<Integer> cycle) {

		List<Node> firstVertices = new ArrayList<Node>();
		List<Node> secondVertices = new ArrayList<Node>();

		for (int i = 0; i < cycle.size() - 1; i += 2) {
			int uIndex = cycle.get(i);
			int vIndex = cycle.get(i + 1);

			Node u = molecule.getNodesRefs()[uIndex];
			Node v = molecule.getNodesRefs()[vIndex];

			if (u.getX() == v.getX()) {
				firstVertices.add(u);
				secondVertices.add(v);
			}
		}

		return new EdgeSet(firstVertices, secondVertices);
	}

	public static SubGraph buildSubGraph(Molecule molecule) {

		int[][] matrix = molecule.getEdgeMatrix();
		int[] disabledVertices = new int[molecule.getNbNodes()];

		int[] degrees = new int[molecule.getNbNodes()];
		for (int i = 0; i < molecule.getNbNodes(); i++)
			degrees[i] = molecule.getDegrees()[i];

		PerfectMatchingType type = PerfectMatchingType.DET;

		return new SubGraph(molecule, matrix, disabledVertices, degrees, type);
	}

	public static void solve(Molecule molecule, PerfectMatchingType type) throws IOException {

		int[] firstVertices = new int[molecule.getNbEdges()];
		int[] secondVertices = new int[molecule.getNbEdges()];

		Model model = new Model("Cycles");

		UndirectedGraph GLB = new UndirectedGraph(model, molecule.getNbNodes(), SetType.BITSET, false);
		UndirectedGraph GUB = new UndirectedGraph(model, molecule.getNbNodes(), SetType.BITSET, false);

		for (int i = 0; i < molecule.getNbNodes(); i++) {
			GUB.addNode(i);

			for (int j = (i + 1); j < molecule.getNbNodes(); j++) {
				if (molecule.getEdgeMatrix()[i][j] == 1) {
					GUB.addEdge(i, j);
				}
			}
		}

		UndirectedGraphVar g = model.graphVar("g", GLB, GUB);

		BoolVar[] boolEdges = new BoolVar[molecule.getNbEdges()];

		int index = 0;
		for (int i = 0; i < molecule.getNbNodes(); i++) {
			for (int j = (i + 1); j < molecule.getNbNodes(); j++) {

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

		int maxCycleSize = (int) Math.ceil((double) molecule.getNbNodes() / 2.0);
		int sum = 2;

		Constraint[] or = new Constraint[maxCycleSize];
		for (int i = 0; i < maxCycleSize; i++) {
			sum += 4;
			or[i] = model.and(model.nbNodes(g, model.intVar(sum)), model.sum(boolEdges, "=", sum));
		}

		model.or(or).post();

		model.getSolver().setSearch(new IntStrategy(boolEdges, new FirstFail(model), new IntDomainMin()));
		Solver solver = model.getSolver();

		Solution solution;

		ArrayList<ArrayList<ArrayList<Integer>>> circuits = new ArrayList<>();

		while (solver.solve()) {
			solution = new Solution(model);
			solution.record();

			ArrayList<Integer> cycle = new ArrayList<Integer>();

			for (int i = 0; i < boolEdges.length; i++) {
				if (solution.getIntVal(boolEdges[i]) == 1) {
					cycle.add(firstVertices[i]);
					cycle.add(secondVertices[i]);
				}
			}

			ArrayList<ArrayList<ArrayList<Integer>>> disjunctCircuits = computeDisjunctCycles(molecule, cycle,
					maxCycleSize);
			circuits.addAll(disjunctCircuits);
		}
	}

	public static void main(String[] args) {

		Molecule molecule = GraphParser.parseUndirectedGraph(new File("C:\\Users\\adrie\\Desktop\\coronene.graph"));
		try {
			solve(molecule, PerfectMatchingType.DET);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
