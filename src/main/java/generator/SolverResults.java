package generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.chocosolver.solver.Solver;

import generator.patterns.Pattern;
import javafx.scene.Group;
import molecules.Molecule;
import solution.BenzenoidSolution;

public class SolverResults {

	private Solver solver;

	private final ArrayList<BenzenoidSolution> solutions = new ArrayList<BenzenoidSolution>();
	private final ArrayList<String> descriptions = new ArrayList<String>();
	private final ArrayList<ArrayList<Integer>> verticesSolutions = new ArrayList<ArrayList<Integer>>();
	private final ArrayList<Integer> nbCrowns = new ArrayList<Integer>();
	private ArrayList<Molecule> molecules = new ArrayList<Molecule>();
	private int[] hexagonsCorrespondances;
	
	private ArrayList<Pattern> nogoodsFragments = new ArrayList<>();

	/*
	 * JavaFX Components
	 */

	ArrayList<Group> clarCovers = new ArrayList<>();
	ArrayList<Group> RBOs = new ArrayList<>();

	private int nbTotalSolutions;

	private long time;
	private long isomorphismTime;

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

	public ArrayList<Integer> getVerticesSolution(int index) {
		return verticesSolutions.get(index);
	}

	public void setNbTotalSolution(int nbTotalSolutions) {
		this.nbTotalSolutions = nbTotalSolutions;
	}

	public int getNbTotalSolutions() {
		return nbTotalSolutions;
	}

	public ArrayList<ArrayList<Integer>> getVerticesSolutions() {
		return verticesSolutions;
	}

	public ArrayList<Integer> getNbCrowns() {
		return nbCrowns;
	}

	public int getCrown(int index) {
		return nbCrowns.get(index);
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

	public void setIsomorphismTime(long isomorphismTime) {
		this.isomorphismTime = isomorphismTime;
	}

	public long getTime() {
		return time;
	}

	public long getIsomorphismTime() {
		return isomorphismTime;
	}

	public ArrayList<Group> getClarCovers() {
		return clarCovers;
	}

	public void addClarCover(Group clarCover) {
		clarCovers.add(clarCover);
	}

	public ArrayList<Group> getRBOs() {
		return RBOs;
	}

	public void addRBO(Group RBO) {
		RBOs.add(RBO);
	}

	public void setClarCovers(ArrayList<Group> clarCovers) {
		this.clarCovers = clarCovers;
	}

	public void setRBOs(ArrayList<Group> RBOs) {
		this.RBOs = RBOs;
	}

	public void setHexagonsCorrespondances(int[] hexagonsCorrespondances) {
		this.hexagonsCorrespondances = hexagonsCorrespondances;
	}

	public int[] getHexagonsCorrespondances() {
		return hexagonsCorrespondances;
	}

	public void export(String outputFilename) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFilename)));

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
		float timeIsoSec = (float) (isomorphismTime / 1000.0);
		float ratio = solver.getNodeCount() / timeSec;

		writer.write("#Solver statistics\n");
		writer.write(verticesSolutions.size() + " benzenoids\n");
		writer.write(nbTotalSolutions + " total solutions\n");
		writer.write("Resolution time : " + timeSec + "s\n");
		writer.write("jGraphT time : " + timeIsoSec + "s.\n");
		writer.write(solver.getNodeCount() + " nodes (" + ratio + " n/s)\n");
		writer.write(solver.getBackTrackCount() + " backtracks\n");
		writer.write(solver.getFailCount() + " fails\n");
		writer.write(solver.getRestartCount() + " restarts\n");

		writer.close();
	}

	public void setSolver(Solver solver) {
		this.solver = solver;
	}
	
	public void setNogoodsFragments(ArrayList<Pattern> nogoodsFragments) {
		this.nogoodsFragments = nogoodsFragments;
	}
	
	public ArrayList<Pattern> getNogoodsFragments() {
		return nogoodsFragments;
	}

	public ArrayList<Molecule> getMolecules() {
		return molecules;
	}

	public void setMolecules(ArrayList<Molecule> molecules) {
		this.molecules = molecules;
	}

	public void addMolecule(Molecule molecule) {
		molecules.add(molecule);
		
	}
}
