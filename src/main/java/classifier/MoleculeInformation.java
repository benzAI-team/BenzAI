package classifier;

import molecules.Benzenoid;

public class MoleculeInformation {

	private final Benzenoid molecule;
	
	private final String moleculeName;
	private final String pathToGraphFile = "NONE";
	private final String pathToLogFile = "NONE";


	public MoleculeInformation(String moleculeName, Benzenoid molecule) {
		this.molecule = molecule;
		this.moleculeName = moleculeName;
	}
	
	/*
	 * Getters & Setters
	 */
	
	public String getMoleculeName() {
		return moleculeName;
	}
	
	String getPathToGraphFile() {
		return pathToGraphFile;
	}

	String getPathToLogFile() {
		return pathToLogFile;
	}

	public Benzenoid getMolecule() {
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
