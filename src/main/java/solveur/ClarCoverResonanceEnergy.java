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
			ArrayList<ClarCoverSolution> solutions = ClarCoverSolver.solve(molecule);
			molecule.setClarCoverSolutions(solutions);
			int[] clarValues = molecule.resonanceEnergyClar();

			for (int i = 0; i < clarValues.length; i++)
				System.out.println(i + " " + clarValues[i]);
		}
	}
}