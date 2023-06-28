package generator;

import benzenoid.Benzenoid;
import org.chocosolver.solver.Solver;
import solution.BenzenoidSolution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SolverResults {

	private Solver solver;

	private final ArrayList<BenzenoidSolution> solutions = new ArrayList<>();
	private final ArrayList<String> descriptions = new ArrayList<>();
	private final ArrayList<ArrayList<Integer>> verticesSolutions = new ArrayList<>();
	private final ArrayList<Integer> nbCrowns = new ArrayList<>();
	private ArrayList<Benzenoid> molecules = new ArrayList<>();

	/*
	 * JavaFX Components
	 */


	private int nbTotalSolutions;

	private long time;

	/*
	 * Getters and setters
	 */

	public ArrayList<BenzenoidSolution> getSolutions() {
		return solutions;
	}

	public ArrayList<String> getDescriptions() {
		return descriptions;
	}

	public void addSolution(BenzenoidSolution solution, String description, int crowns) {
		solutions.add(solution);
		descriptions.add(description);
		nbCrowns.add(crowns);
	}

	public void addVerticesSolution(ArrayList<Integer> solution) {
		verticesSolutions.add(solution);
	}

	public void setNbTotalSolution(int nbTotalSolutions) {
		this.nbTotalSolutions = nbTotalSolutions;
	}

	public ArrayList<ArrayList<Integer>> getVerticesSolutions() {
		return verticesSolutions;
	}

	public ArrayList<Integer> getNbCrowns() {
		return nbCrowns;
	}

	/*
	 * Class methods
	 */

	public void addResult(SolverResults solverResults) {
		solutions.addAll(solverResults.getSolutions());
		descriptions.addAll(solverResults.getDescriptions());
		verticesSolutions.addAll(solverResults.getVerticesSolutions());
		nbCrowns.addAll(solverResults.getNbCrowns());
		molecules.addAll(solverResults.getMolecules());
	}

	public int size() {
		return solutions.size();
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public void export(String outputFilename) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename));

		for (int i = 0; i < verticesSolutions.size(); i++) {

			ArrayList<Integer> vertices = verticesSolutions.get(i);
			String description = descriptions.get(i);

			for (int j = 0; j < vertices.size(); j++) {
				if (vertices.get(j) == 1)
					writer.write(j + " ");
			}

			writer.write("\n");

			writer.write(description + "\n");

		}

		float timeSec = (float) (time / 1000.0);
		float ratio = solver.getNodeCount() / timeSec;

		writer.write("#Solver statistics\n");
		writer.write(verticesSolutions.size() + " benzenoids\n");
		writer.write(nbTotalSolutions + " total solutions\n");
		writer.write("Resolution time : " + timeSec + "s\n");
		writer.write(solver.getNodeCount() + " nodes (" + ratio + " n/s)\n");
		writer.write(solver.getBackTrackCount() + " backtracks\n");
		writer.write(solver.getFailCount() + " fails\n");
		writer.write(solver.getRestartCount() + " restarts\n");

		writer.close();
	}

	public void setSolver(Solver solver) {
		this.solver = solver;
	}
	
	public void setNogoodsFragments() {
	}

	public ArrayList<Benzenoid> getMolecules() {
		return molecules;
	}

	public void setMolecules(ArrayList<Benzenoid> molecules) {
		this.molecules = molecules;
	}

	public void addMolecule(Benzenoid molecule) {
		molecules.add(molecule);
		
	}

	public int getNbTotalSolutions() {
		return nbTotalSolutions;
	}

}
