package new_classifier;

import java.util.ArrayList;
import java.util.HashMap;

import molecules.Molecule;

public class NewCarbonsHydrogensClassifier extends NewClassifier {

	public NewCarbonsHydrogensClassifier(ArrayList<Molecule> molecules) {
		super(molecules);
	}

	@Override
	public HashMap<String, ArrayList<Molecule>> classify() {

		HashMap<String, ArrayList<Molecule>> map = new HashMap<>();

		for (Molecule molecule : molecules) {

			String key = molecule.getNbNodes() + "_carbons_" + molecule.getNbHydrogens() + "_hydrogens";

			if (map.get(key) == null) {
				map.put(key, new ArrayList<>());
				map.get(key).add(molecule);
			}

			else
				map.get(key).add(molecule);
		}

		return map;
	}

}
