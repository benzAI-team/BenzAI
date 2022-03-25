package classifier;

import molecules.Molecule;
import parsers.GraphParser;

public class MoleculeInformation {

	private Molecule molecule;
	
	private String moleculeName;
	private String pathToGraphFile = "NONE";
	private String pathToLogFile = "NONE";
	
	
	public MoleculeInformation(String moleculeName) {
		this.moleculeName = moleculeName;
	}
	
	public MoleculeInformation(String moleculeName, Molecule molecule) {
		this.molecule = molecule;
		this.moleculeName = moleculeName;
	}
	
	/*
	 * Getters & Setters
	 */
	
	public String getMoleculeName() {
		return moleculeName;
	}
	
	public String getPathToGraphFile() {
		return pathToGraphFile;
	}
	
	public void setPathToGraphFile(String pathToGraphFile) {
		this.pathToGraphFile = pathToGraphFile;
		buildMolecule();
	}
	
	public String getPathToLogFile() {
		return pathToLogFile;
	}
	
	public void setPathToLogFile(String pathToLogFile) {
		this.pathToLogFile = pathToLogFile;
	}
	
	public Molecule getMolecule() {
		return molecule;
	}
	
	/*
	 * Class methods
	 */
	
	private void buildMolecule() {
		molecule = GraphParser.parseUndirectedGraph(pathToGraphFile, null, false);
	}
	
	@Override
	public String toString() {
		return moleculeName + "\n" + pathToGraphFile + "\n" + pathToLogFile;
	}
}
