package classifier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Classifier {
	
	@SuppressWarnings("unused")
	private File listMoleculesFile;
	
	protected HashMap<String, MoleculeInformation> moleculesInformations;
	
	public Classifier(HashMap<String, MoleculeInformation> moleculesInformations) {
		this.moleculesInformations = moleculesInformations;
	}
	
	public abstract ArrayList<PAHClass> classify(); 
}
