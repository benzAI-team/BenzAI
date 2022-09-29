package solveur;

import java.io.File;
import java.util.ArrayList;

import molecules.Molecule;
import parsers.GraphParser;
import solution.ClarCoverSolution;

public class ClarCoverResonanceEnergy {

	private static void usage() {
		System.out.println("usage : java -jar jarfile.jar file.graph_coord");
	}

	public static void main(String[] args) {

		if (args.length == 0)
			usage();
		else {
			Molecule molecule = GraphParser.parseUndirectedGraph(new File(args[0]));
			long begin = System.currentTimeMillis();
			ArrayList<ClarCoverSolution> solutions = ClarCoverSolver.solve(molecule);
			long end = System.currentTimeMillis(); 
			long time = end - begin;
			molecule.setClarCoverSolutions(solutions);
			int[] clarValues = molecule.resonanceEnergyClar();

			double [] clarValuesNormalised = new double[clarValues.length];
			double nbCover = solutions.size();
			
			for (int i = 0 ; i < clarValuesNormalised.length ; i++)
				clarValuesNormalised[i] = clarValues[i] / nbCover;
			
			for (int i = 0; i < clarValues.length; i++)
				System.out.println(i + " " + clarValuesNormalised[i]);
			
			System.out.println(nbCover + " Clar's covers");
			
			System.out.println(time + " ms.");
		}
	}
}
