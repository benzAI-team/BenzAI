package new_classifier;

import java.util.ArrayList;
import java.util.HashMap;

import molecules.Benzenoid;

public abstract class NewClassifier {

	protected ArrayList<Benzenoid> molecules;

	public NewClassifier(ArrayList<Benzenoid> molecules) {
		this.molecules = molecules;
	}

	public abstract HashMap<String, ArrayList<Benzenoid>> classify();
}
