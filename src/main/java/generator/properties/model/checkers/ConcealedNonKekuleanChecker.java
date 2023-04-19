package generator.properties.model.checkers;

import generator.properties.model.ModelProperty;
import molecules.Molecule;

public class ConcealedNonKekuleanChecker extends Checker {

	@Override
	public boolean checks(Molecule molecule, ModelProperty property) {
		return molecule.getNbKekuleStructures() == 0 && molecule.colorShift() == 0;
	}

}
