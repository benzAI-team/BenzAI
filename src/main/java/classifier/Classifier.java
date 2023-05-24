package classifier;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Classifier {
	
	@SuppressWarnings("unused")
	private File listMoleculesFile;
	
	HashMap<String, MoleculeInformation> moleculesInformations;
	
	Classifier(HashMap<String, MoleculeInformation> moleculesInformations) {
		this.moleculesInformations = moleculesInformations;
	}
	
	public abstract ArrayList<PAHClass> classify(); 
}
