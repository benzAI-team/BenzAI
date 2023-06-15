package solveur;

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
import benzenoid.OrientedCycle;
import parsers.GraphParser;
import utils.EdgeSet;
import utils.Interval;
import utils.SubMolecule;
import utils.Utils;

public enum RandicMethod {
	;

	private static int [][] globalMatrix;
	
	private static int getTopLeftHexagon(Benzenoid molecule, ArrayList<Integer> hexagonsCycle) {
		
		Node topLeftNode = null;
		int topLeftHexagon = -1;
		
		for (Integer hexagon : hexagonsCycle) {
			
			Node node = molecule.getNodeRef(molecule.getHexagons()[hexagon][0]);
			
			if (topLeftNode == null) {
				topLeftNode = node;
				topLeftHexagon = hexagon;
			}
			
			else if (node.compareTo(topLeftNode) < 0) {
				topLeftNode = node;
				topLeftHexagon = hexagon;
			}
		}
		
		return topLeftHexagon;
	}
	
	private static EdgeSet computeStraightEdges(Benzenoid molecule, int [][] cycle) {
		
		List<Node> firstVertices = new ArrayList<>();
		List<Node> secondVertices = new ArrayList<>();
		
		for (int i = 0 ; i < cycle.length ; i ++) {
			for (int j = (i + 1) ; j < cycle[i].length ; j++) {
				
				if (cycle[i][j] == 1) {
					
					Node u = molecule.getNodesCoordinates()[i];
					Node v = molecule.getNodesCoordinates()[j];
					
					if (u.getX() == v.getX()) {
						firstVertices.add(u);
						secondVertices.add(v);
					}
				}
			}
		}
		
		return new EdgeSet(firstVertices, secondVertices);
	}
	
	private static List<Interval> computeIntervals(EdgeSet edges){
		
		List<Interval> intervals = new ArrayList<>();
	
		int [] edgesOK = new int [edges.size()];
		
		for (int i = 0 ; i < edges.size() ; i ++) {
			if (edgesOK[i] == 0) {
				edgesOK[i] = 1;
				Node u1 = edges.getFirstVertices().get(i);
				Node v1 = edges.getSecondVertices().get(i);
				
				int y1 = Math.min(u1.getY(), v1.getY());
				int y2 = Math.max(u1.getY(), v1.getY());
				
				List<NodeSameLine> sameLineNodes = new ArrayList<>();
				
				for (int j = (i+1) ; j < edges.size() ; j++) {
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
									
				for (int j = 0 ; j < sameLineNodes.size() ; j += 2) {
						
					NodeSameLine nsl1 = sameLineNodes.get(j);
					NodeSameLine nsl2 = sameLineNodes.get(j+1);
						
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
	
	
	
	private static ArrayList<Integer> getHexagons(Benzenoid molecule, int [][] cycle) {
		
		EdgeSet verticalEdges = computeStraightEdges(molecule, cycle);
		ArrayList<Interval> intervals = (ArrayList<Interval>) computeIntervals(verticalEdges);
		Collections.sort(intervals);
		
		ArrayList<Integer> hexagons = new ArrayList<>();

		for (Interval interval : intervals){

			int [] hexagonsCount = new int [molecule.getNbHexagons()];

			for (int x = interval.x1() ; x <= interval.x2() ; x += 2){
				int u1 = molecule.getMatrixCoordinates().get(x, interval.y1());
				int u2 = molecule.getMatrixCoordinates().get(x, interval.y2());

				for (Integer hexagon : molecule.getHexagonsVertices().get(u1)) {
					hexagonsCount[hexagon] ++;
					if (hexagonsCount[hexagon] == 4)
						hexagons.add(hexagon);
				}

				for (Integer hexagon : molecule.getHexagonsVertices().get(u2)){
					hexagonsCount[hexagon] ++;
					if (hexagonsCount[hexagon] == 4)
						hexagons.add(hexagon);
				}
			}
		}

		return hexagons;
	}
	
	private static boolean containsNode(int [][] cycle, int node) {
		
		for (int i = 0 ; i < cycle[node].length ; i++) {
			if (cycle[node][i] == 1) 
				return true;
		}
		
		return false;
	}
	
	private static int cycleSize(int [][] cycle) {
		
		int size = 0;
		
		for (int i = 0 ; i < cycle.length ; i++) {
			if (containsNode(cycle, i))
				size ++;
		}
		
		return size;
	}
	
	private static OrientedCycle buildOrientedCycle(Benzenoid molecule, int [][] cycle) {
		
		OrientedCycle orientedCycle = new OrientedCycle(molecule);
		ArrayList<Integer> hexagonsCycle = getHexagons(molecule, cycle);
		int [] topLeftHexagon = molecule.getHexagons()[getTopLeftHexagon(molecule, hexagonsCycle)];
		
		int [] checkedNodes = new int [molecule.getNbCarbons()];
		
		for (int u = 0; u < molecule.getNbCarbons() ; u++) {
			
			if (!containsNode(cycle, u))
				checkedNodes[u] = -1;
		}
		
		int u0 = topLeftHexagon[0];
		int u1 = topLeftHexagon[5];
		
		checkedNodes[u0] = 0;
		checkedNodes[u1] = 1;
		
		orientedCycle.addArc(u0, u1);
		int nbNodes = 1;
		
		int u = u1;
		
		while (nbNodes < cycleSize(cycle)) {
			
			for (int v = 0; v < molecule.getNbCarbons() ; v++) {
				
				if (u == u1) {
					
					if (checkedNodes[v] == 0 && v != u0 && cycle[u][v] == 1) {
						checkedNodes[v] = 1;
						orientedCycle.addArc(u, v);
						u = v;
						nbNodes ++;
					}
				}
				
				else {
					
					if (checkedNodes[v] == 0 && cycle[u][v] == 1) {
						
						checkedNodes[v] = 1;
						orientedCycle.addArc(u, v);
						u = v;
						nbNodes ++;
					}
				}
			}
		}
		
		
		return orientedCycle;
	}
	
	private static SubMolecule substractCycleAndInterior(Benzenoid molecule, int [][] cycle) {
		
		int [][] newGraph = new int [molecule.getNbCarbons()][molecule.getNbCarbons()];
		int [] vertices = new int [molecule.getNbCarbons()];
		int [] subGraphVertices = new int[molecule.getNbCarbons()];
		
		List<Integer> hexagons = getHexagons(molecule, cycle);
		
		for (Integer hexagon : hexagons) {
			int [] nodes = molecule.getHexagons()[hexagon];

			for (int node : nodes) vertices[node] = 1;
		}
		
		int subGraphNbNodes = 0;
		
		int nbEdges = 0;
		
		for (int u = 0; u < molecule.getNbCarbons() ; u++) {
			if (vertices[u] == 0) {
				for (int v = (u+1); v < molecule.getNbCarbons() ; v++) {
					if (vertices[v] == 0) {
						newGraph[u][v] = molecule.getEdgeMatrix()[u][v];
						newGraph[v][u] = molecule.getEdgeMatrix()[v][u];
						
						if (molecule.getEdgeMatrix()[u][v] == 1)
							nbEdges ++;
						
						if (subGraphVertices[u] == 0) {
								subGraphVertices[u] = 1;
								subGraphNbNodes ++;
						}
						
						if (subGraphVertices[v] == 0) {
								subGraphVertices[v] = 1;
								subGraphNbNodes ++;
						}
					}
				}
			}
		}
		
		return new SubMolecule(subGraphNbNodes, nbEdges, molecule.getNbCarbons(), newGraph);
	}
	
	private static void treatCycle(Benzenoid molecule, int [][] cycle) {
		
		EdgeSet verticalEdges = computeStraightEdges(molecule, cycle);
		ArrayList<Interval> intervals = (ArrayList<Interval>) computeIntervals(verticalEdges);
		Collections.sort(intervals);
		int cycleConfiguration = Utils.identifyCycle(molecule, intervals);
		int dependantCycleConfiguration = Utils.identifyDependantCycle(molecule, intervals);
		
		if (cycleConfiguration != -1 || dependantCycleConfiguration != -1) {
			
			SubMolecule subMolecule = substractCycleAndInterior(molecule, cycle);
			int nbPerfectMatchings = PerfectMatchingSolver.computeNbPerfectMatching(subMolecule);
		
			int MC = 2;
		
			OrientedCycle orientedCycle = buildOrientedCycle(molecule, cycle);
		
			for (int i = 0 ; i < orientedCycle.matrixSize() ; i++) {
				for (int j = 0 ; j < orientedCycle.matrixSize() ; j ++) {
				
					if (orientedCycle.containsArc(i, j))
						globalMatrix[i][j] += MC * nbPerfectMatchings;
				}
			}
		}
	}
	
	private static void solve(Benzenoid molecule) {
		
		globalMatrix = new int[molecule.getNbCarbons()][molecule.getNbCarbons()];
		
		int [] firstVertices = new int [molecule.getNbBonds()];
		int [] secondVertices = new int [molecule.getNbBonds()];
		
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
		for (int i = 0; i < molecule.getNbCarbons() ; i++) {
			for (int j = (i+1); j < molecule.getNbCarbons() ; j++) {

				if (molecule.getEdgeMatrix()[i][j] == 1) {
					boolEdges[index] = model.boolVar("(" + i + "--" + j + ")");
					model.edgeChanneling(g, boolEdges[index], i, j).post();
					firstVertices[index] = i;
					secondVertices[index] = j;
					index ++;
				}
			}
		}
			
		model.minDegree(g, 2).post();
		model.maxDegree(g, 2).post();
		model.connected(g).post();
			
		model.or(
			model.and(model.nbNodes(g, model.intVar(6)), model.sum(boolEdges, "=", 6)),
			model.and(model.nbNodes(g, model.intVar(10)), model.sum(boolEdges, "=", 10)),
			model.and(model.nbNodes(g, model.intVar(14)), model.sum(boolEdges, "=", 14)),
			model.and(model.nbNodes(g, model.intVar(18)), model.sum(boolEdges, "=", 18))
		).post();
			
		model.getSolver().setSearch(new IntStrategy(boolEdges, new FirstFail(model), new IntDomainMin()));
		Solver solver = model.getSolver();

		Solution solution;
			
		while(solver.solve()){
			solution = new Solution(model);
			solution.record();
					
			int [][] cycle = new int [molecule.getNbCarbons()][molecule.getNbCarbons()];
			
			for (int i = 0 ; i < boolEdges.length ; i++) {
				if (solution.getIntVal(boolEdges[i]) == 1) {
					
					cycle[firstVertices[i]][secondVertices[i]] = 1;
					cycle[secondVertices[i]][firstVertices[i]] = 1;
				}
			}
			
			treatCycle(molecule, cycle);
		}
		
		int [][] matrixSolution = new int [molecule.getNbCarbons()][molecule.getNbCarbons()];
		
		for (int i = 0; i < molecule.getNbCarbons() ; i ++) {
			for (int j = 0; j < molecule.getNbCarbons() ; j++) {
				
				int sens1 = globalMatrix[i][j];
				int sens2 = globalMatrix[j][i];
				int result = Math.abs(sens1 - sens2);
				
				matrixSolution[i][j] = result;
				matrixSolution[j][i] = result;
			}
		}
		
		for (int i = 0; i < molecule.getNbCarbons() ; i ++) {
			for (int j = (i+1); j < molecule.getNbCarbons() ; j++) {
				
				if (matrixSolution[i][j] != 0) {
					System.out.println("(" + i + " - " + j + ") -> " + matrixSolution[i][j]);
				}
			}
		}
	}
	
	private static void usage() {
		System.out.println("USAGE: java -jar RandicSolver.jar ${filename}");
	}
	
	public static void main(String [] args) {
		
		if (args.length == 0)
			usage();
		
		else {
		
			Benzenoid molecule = GraphParser.parseUndirectedGraph(args[0], null, false);
			assert molecule != null;
			solve(molecule);
		}
	}
}
