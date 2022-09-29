package solveur;

import java.io.File;
import java.util.ArrayList;

import molecules.Molecule;
import molecules.RBO;
import molecules.SubGraph;
import parsers.GraphParser;
import solveur.LinAlgorithm.PerfectMatchingType;

public class RBOSolver {

	public static RBO RBO(Molecule molecule) {

		double[][] statistics = new double[molecule.getNbNodes()][molecule.getNbNodes()];
		double[] RBO = new double[molecule.getNbHexagons()];

		double nbKekuleStructures = molecule.getNbKekuleStructures();

		if (nbKekuleStructures == 0)
			return new RBO(molecule, statistics, RBO);

		for (int i = 0; i < molecule.getNbNodes(); i++) {
			for (int j = (i + 1); j < molecule.getNbNodes(); j++) {
				if (molecule.getAdjacencyMatrix()[i][j] == 1) {

					ArrayList<Integer> toRemove = new ArrayList<>();
					toRemove.add(i);
					toRemove.add(j);
					SubGraph subGraph = RispoliAlgorithm.removeCircuit(molecule, toRemove, PerfectMatchingType.DET);
					double nbPerfectMatchings = subGraph.getNbPerfectMatchings();

					statistics[i][j] += nbPerfectMatchings;
					statistics[j][i] += nbPerfectMatchings;

				}
			}
		}

		for (int i = 0; i < molecule.getNbNodes(); i++) {
			for (int j = 0; j < molecule.getNbNodes(); j++) {
				statistics[i][j] = statistics[i][j] / nbKekuleStructures;
			}
		}

		for (int i = 0; i < molecule.getNbHexagons(); i++) {

			double sum = 0;

			int[] hexagon = molecule.getHexagon(i);

			for (int j = 0; j < 6; j++) {
				int u = hexagon[j];
				int v = hexagon[(j + 1) % 6];

				sum += statistics[u][v];
			}

			RBO[i] = sum;
		}

		return new RBO(molecule, statistics, RBO);
	}
	
	private static void usage() {
		System.out.println("usage: java -jar jarfile file.graph_coord");
	}
	
	public static void main(String [] args) {
		
		if (args.length != 1)
			usage();
		
		else {
			Molecule molecule = GraphParser.parseUndirectedGraph(new File(args[0]));
			long begin = System.currentTimeMillis();
			RBO rbo = RBO(molecule);
			long end = System.currentTimeMillis();
			for (int i = 0 ; i < molecule.getNbHexagons() ; i++) {
				System.out.println(i + " " + rbo.getRBO()[i]);
			}
			long time = end - begin;
			System.out.println(time + " ms.");
		}
		
	}
}
