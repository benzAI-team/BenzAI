package classifier;

import molecules.Molecule;

public class MoleculeInformation {

	private final Molecule molecule;
	
	private final String moleculeName;
	private final String pathToGraphFile = "NONE";
	private String pathToLogFile = "NONE";


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

	public String getPathToLogFile() {
		return pathToLogFile;
	}

	public Molecule getMolecule() {
		return molecule;
	}
	
	/*
	 * Class methods
	 */

	@Override
	public String toString() {
		return moleculeName + "\n" + pathToGraphFile + "\n" + pathToLogFile;
	}
}
