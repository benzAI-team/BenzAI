package solveur;

import molecules.Benzenoid;
import parsers.GraphParser;
import solution.ClarCoverSolution;

import java.io.File;
import java.util.ArrayList;

public enum ClarCoverResonanceEnergy {
    ;

    private static void usage() {
		System.out.println("usage : java -jar jarfile.jar file.graph_coord");
	}

	public static void main(String[] args) {

		if (args.length == 0)
			usage();
		else {
			Benzenoid molecule = GraphParser.parseUndirectedGraph(new File(args[0]));
			ArrayList<ClarCoverSolution> solutions = ClarCoverSolver.solve(molecule);
			molecule.setClarCoverSolutions(solutions);
			int[] clarValues = molecule.resonanceEnergyClar();

			for (int i = 0; i < clarValues.length; i++)
				System.out.println(i + " " + clarValues[i]);
		}
	}
}
