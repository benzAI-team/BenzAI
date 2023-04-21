package classifier;

import java.util.ArrayList;
import java.util.HashMap;

public class PAHClass {

	private final String title;
	private final ArrayList<String> moleculesNames;
	private final HashMap<String, MoleculeInformation> moleculesInformations;

	public PAHClass(String title, HashMap<String, MoleculeInformation> moleculesInformations) {
		this.title = title;
		this.moleculesInformations = moleculesInformations;
		moleculesNames = new ArrayList<String>();
	}

	public void addMolecule(String molecule) {
		moleculesNames.add(molecule);
	}

	public String getTitle() {
		return title;
	}

	public String getGraphFilename(int index) {
		return moleculesInformations.get(moleculesNames.get(index)).getPathToGraphFile();
	}

	public String getLogFilename(int index) {
		return moleculesInformations.get(moleculesNames.get(index)).getPathToLogFile();
	}

	public HashMap<String, MoleculeInformation> getMoleculesInformations() {
		return moleculesInformations;
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();

		builder.append(title + "\n");

		for (String molecule : moleculesNames)
			builder.append(moleculesInformations.get(molecule).getPathToGraphFile() + "\n");

		return builder.toString();
	}

	public int size() {
		return moleculesNames.size();
	}

	public ArrayList<String> getMoleculesNames() {
		return moleculesNames;
	}
}
