package new_classifier;

import java.util.ArrayList;
import java.util.HashMap;

import molecules.Molecule;

public abstract class NewClassifier {

	protected ArrayList<Molecule> molecules;

	public NewClassifier(ArrayList<Molecule> molecules) {
		this.molecules = molecules;
	}

	public abstract HashMap<String, ArrayList<Molecule>> classify();
}
