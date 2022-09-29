package solveur;

import parsers.GraphParser;
import solveur.LinAlgorithm.PerfectMatchingType;

import java.io.File;
import molecules.Molecule;
import molecules.SubGraph;

public class ExpeKekule {

	private static void usage() {
		System.out.println("java -jar jarfile.jar file.graph_coord");
	}
	
	public static void main(String [] args) {
		
		if (args.length == 0)
			usage();
		
		else {
			Molecule molecule = GraphParser.parseUndirectedGraph(new File(args[0]));
			
			long begin = System.currentTimeMillis();
			double nbKekuleStructures = molecule.getNbKekuleStructures();
			long end = System.currentTimeMillis();
			long timeRispoli = end - begin;
			
			System.out.println("Rispoli");
			System.out.println(nbKekuleStructures);
			System.out.println(timeRispoli + " ms.");
			System.out.println("\n");
			
			int [] d = new int [molecule.getNbNodes()];
			
			begin = System.currentTimeMillis();
			SubGraph fg = new SubGraph(molecule.getAdjacencyMatrix(), d, molecule.getDegrees(), PerfectMatchingType.CHOCO);	
			nbKekuleStructures = (double) fg.getNbPerfectMatchings();
			end = System.currentTimeMillis();
			long timeSum = end - begin;
			
			System.out.println("Sum");
			System.out.println(nbKekuleStructures);
			System.out.println(timeSum + " ms.");
			System.out.println("\n");
			
			begin = System.currentTimeMillis();
			nbKekuleStructures = PerfectMatchingSolver.computeKekuleStructuresAllDiffConstraint(molecule, "AC_REGIN");
			end = System.currentTimeMillis();
			long timeRegin = end - begin;
			
			System.out.println("Regin");
			System.out.println(nbKekuleStructures);
			System.out.println(timeRegin + " ms.");
			System.out.println("\n");
			
		}
	}
	
}
