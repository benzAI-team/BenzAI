package solveur.disjunct_circuits;

import java.util.ArrayList;

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

import molecules.SubGraph;

public class CircuitModel extends Model {

	/*
	 * Benzenoid's attributes
	 */

	private final SubGraph subGraph;

	private int[] firstVertices;
	private int[] secondVertices;

	private int[] correspondances1;

	/*
	 * Model's attributes
	 */

	private final String name;

	private Solver solver;
	private UndirectedGraphVar graphVariable;

	private BoolVar[] boolEdges;

	public CircuitModel(SubGraph subGraph, String name) {
		super("Circuits");
		this.subGraph = subGraph;
		this.name = name;
		initializeVariables();
		postConstraints();
	}

	private void initializeVariables() {

		int nbNodes = subGraph.getNbEnabledNodes();
		int nbEdges = subGraph.getNbEdges();

		firstVertices = new int[nbEdges];
		secondVertices = new int[nbEdges];

		correspondances1 = new int[subGraph.getNbNodes()];
		int[] correspondances2 = new int[subGraph.getNbEnabledNodes()];

		UndirectedGraph GLB = new UndirectedGraph(this, nbNodes, SetType.BITSET, false);
		UndirectedGraph GUB = new UndirectedGraph(this, nbNodes, SetType.BITSET, false);

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

		graphVariable = this.graphVar("graph", GLB, GUB);

		boolEdges = new BoolVar[nbEdges];
	}

	private void postConstraints() {

		int nbNodes = subGraph.getNbEnabledNodes();

		int edgeIndex = 0;
		for (int i = 0; i < subGraph.getNbNodes(); i++) {
			if (!subGraph.isDisabled(i)) {

				for (int j = (i + 1); j < subGraph.getNbNodes(); j++) {
					if (!subGraph.isDisabled(j) && subGraph.containsEdge(i, j)) {

						int u = correspondances1[i];
						int v = correspondances1[j];

						boolEdges[edgeIndex] = this.boolVar("edge[" + i + "][" + j + "]");
						this.edgeChanneling(graphVariable, boolEdges[edgeIndex], u, v).post();
						firstVertices[edgeIndex] = i;
						secondVertices[edgeIndex] = j;
						edgeIndex++;
					}
				}
			}
		}

		this.minDegree(graphVariable, 2).post();
		this.maxDegree(graphVariable, 2).post();
		this.connected(graphVariable).post();

		int sum = 2;
		int maxCycleSize = (int) Math.ceil((double) nbNodes / 2.0);

		Constraint[] or = new Constraint[maxCycleSize];
		for (int i = 0; i < maxCycleSize; i++) {
			sum += 4;
			or[i] = this.and(this.nbNodes(graphVariable, this.intVar(sum)), this.sum(boolEdges, "=", sum));
		}

		this.or(or).post();

		solver = this.getSolver();
		solver.setSearch(new IntStrategy(boolEdges, new FirstFail(this), new IntDomainMin()));

	}

	public ArrayList<Integer> nextCycle() {

		boolean result = solver.solve();

		if (result) {
			Solution solution = new Solution(this);
			solution.record();

			ArrayList<Integer> cycle = new ArrayList<>();

			for (int i = 0; i < boolEdges.length; i++) {
				if (solution.getIntVal(boolEdges[i]) == 1) {
					cycle.add(firstVertices[i]);
					cycle.add(secondVertices[i]);
				}
			}
			return cycle;
		}

		return null;
	}

	@Override
	public String toString() {
		return "CircuitModel::" + name;
	}
}
