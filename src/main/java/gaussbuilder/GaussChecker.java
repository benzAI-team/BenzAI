package gaussbuilder;

import java.util.ArrayList;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.UndirectedGraphVar;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;

import molecules.Molecule;
import utils.Couple;
import utils.Triplet;

public enum GaussChecker {
    ;

    public static double distance(Triplet<Double, Double, Double> p1, Triplet<Double, Double, Double> p2) {

		double x1 = p1.getX();
		double y1 = p1.getY();
		double z1 = p1.getZ();

		double x2 = p2.getX();
		double y2 = p2.getY();
		double z2 = p2.getZ();

		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2));
	}
	
	public static ArrayList<ArrayList<Integer>> getCycles(Triplet<Double, Double, Double>[] carbons) {

		ArrayList<ArrayList<Integer>> cycles = new ArrayList<>();

		int[][] matrix = new int[carbons.length][carbons.length];
		int nbEdges = 0;

		for (int i = 0; i < carbons.length; i++) {
			Triplet<Double, Double, Double> c1 = carbons[i];
			for (int j = (i + 1); j < carbons.length; j++) {
				Triplet<Double, Double, Double> c2 = carbons[j];
				double distance = distance(c1, c2);

				if (distance <= 1.4518664462325324) {
					matrix[i][j] = 1;
					matrix[j][i] = 1;
					nbEdges++;
				}
			}
		}

		Model model = new Model("Cycles");

		int nbCarbons = carbons.length;

		UndirectedGraph GLB = new UndirectedGraph(model, nbCarbons, SetType.BITSET, false);
		UndirectedGraph GUB = new UndirectedGraph(model, nbCarbons, SetType.BITSET, false);

		for (int i = 0; i < nbCarbons; i++) {
			GUB.addNode(i);
			for (int j = (i + 1); j < nbCarbons; j++) {
				if (matrix[i][j] == 1)
					GUB.addEdge(i, j);
			}
		}

		int[] firstVertices = new int[nbEdges];
		int[] secondVertices = new int[nbEdges];

		UndirectedGraphVar g = model.graphVar("g", GLB, GUB);
		BoolVar[] boolEdges = new BoolVar[nbEdges];

		int index = 0;
		for (int i = 0; i < nbCarbons; i++) {
			for (int j = (i + 1); j < nbCarbons; j++) {

				if (matrix[i][j] == 1) {
					boolEdges[index] = model.boolVar("(" + i + "--" + j + ")");
					model.edgeChanneling(g, boolEdges[index], i, j).post();
					firstVertices[index] = i;
					secondVertices[index] = j;
					index++;
				}
			}
		}

		IntVar nbNodes = model.intVar("nb_nodes", 0, nbCarbons);
		model.nbNodes(g, nbNodes).post();
		model.arithm(nbNodes, "=", 6).post();

		model.minDegree(g, 2).post();
		model.maxDegree(g, 2).post();
		model.connected(g).post();

		model.getSolver().setSearch(new IntStrategy(boolEdges, new FirstFail(model), new IntDomainMin()));
		Solver solver = model.getSolver();

		Solution solution;

		while (solver.solve()) {
			solution = new Solution(model);
			solution.record();

			ArrayList<Integer> cycle = new ArrayList<Integer>();

			for (int i = 0; i < boolEdges.length; i++) {
				if (solution.getIntVal(boolEdges[i]) == 1) {
					if (!cycle.contains(firstVertices[i]))
						cycle.add(firstVertices[i]);
					if (!cycle.contains(secondVertices[i]))
						cycle.add(secondVertices[i]);
				}
			}

			cycles.add(cycle);

		}

		return cycles;
	}
	
	public static ArrayList<Couple<Integer, Integer>> checkInvalidCarbons(Molecule molecule,
			Triplet<Double, Double, Double>[] carbons) {

		ArrayList<Couple<Integer, Integer>> invalidsCarbons = new ArrayList<>();

		for (int i = 0; i < carbons.length; i++) {
			Triplet<Double, Double, Double> c1 = carbons[i];
			for (int j = (i + 1); j < carbons.length; j++) {
				if (molecule.getEdgeMatrix()[i][j] == 0) {
					Triplet<Double, Double, Double> c2 = carbons[j];
					double distance = distance(c1, c2);

					if (distance <= 1.4)
						invalidsCarbons.add(new Couple<>(i, j));
				}
			}
		}

		return invalidsCarbons;
	}
	
	public static ArrayList<Couple<Integer, Integer>> checkInvalidHydrogens(Molecule molecule,
			Triplet<Double, Double, Double>[] hydrogens) {

		ArrayList<Couple<Integer, Integer>> invalidsCarbons = new ArrayList<>();

		for (int i = 0; i < hydrogens.length; i++) {
			Triplet<Double, Double, Double> c1 = hydrogens[i];
			for (int j = (i + 1); j < hydrogens.length; j++) {
				if (molecule.getEdgeMatrix()[i][j] == 0) {
					Triplet<Double, Double, Double> c2 = hydrogens[j];
					double distance = distance(c1, c2);

					if (i == 1 && j == 4)
						System.out.print("");

					if (distance <= 1.1)
						invalidsCarbons.add(new Couple<>(i, j));
				}
			}
		}

		return invalidsCarbons;
	}
	
	public static void checkGeometry(String name, Molecule molecule, Geometry geometry) {
		
		/*
		 * Nb carbons
		 */
		
		int nbCarbonsExpected = molecule.getNbNodes();
		int nbCarbonsGot = geometry.getCarbons().length;
		boolean nbCarbons;


        nbCarbons = nbCarbonsExpected == nbCarbonsGot;
		
		/*
		 * Nb hydrogens
		 */
		
		int nbHydrogensExpected = molecule.getNbHydrogens();
		int nbHydrogensGot = geometry.getHydrogens().size();
		boolean nbHydrogens;


        nbHydrogens = nbHydrogensExpected == nbHydrogensGot;
		
		
		/*
		 * Nb Cycles
		 */
		
		int nbCyclesExpected = molecule.getNbHexagons();
		int nbCyclesGot = getCycles(geometry.getCarbons()).size();
		boolean nbCycles;


        nbCycles = nbCyclesExpected == nbCyclesGot;
		
		
		
		
		if (nbCarbons && nbHydrogens && nbCycles)
			System.out.println(name + ": [GEOMETRY OK]");
		else
			System.out.println(name + ": [GEOMETRY ERROR]");
		
		System.out.print("nb_carbons:\texpected: " + nbCarbonsExpected + "\t got: " + nbCarbonsGot + " ");
		if(nbCarbons)
			System.out.println("[OK]");
		else
			System.out.println("[ERROR]");
		
		System.out.print("nb_hydrogens:\texpected: " + nbHydrogensExpected + "\t got: " + nbHydrogensGot + " ");
		if(nbHydrogens)
			System.out.println("[OK]");
		else
			System.out.println("[ERROR]");
		
		System.out.print("nb_cycles:\texpected: " + nbCyclesExpected + "\t got: " + nbCyclesGot + " ");
		if(nbCycles)
			System.out.println("[OK]");
		else
			System.out.println("[ERROR]");
		
		System.out.println();
	}
	
}
