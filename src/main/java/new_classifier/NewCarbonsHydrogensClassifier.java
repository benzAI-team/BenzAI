package new_classifier;

import java.util.ArrayList;
import java.util.HashMap;

import benzenoid.Benzenoid;

public class NewCarbonsHydrogensClassifier extends NewClassifier {

	public NewCarbonsHydrogensClassifier(ArrayList<Benzenoid> molecules) {
		super(molecules);
	}

	@Override
	public HashMap<String, ArrayList<Benzenoid>> classify() {

		HashMap<String, ArrayList<Benzenoid>> map = new HashMap<>();

		for (Benzenoid molecule : molecules) {

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
