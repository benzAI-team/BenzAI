package modelProperty.checkers;

import modelProperty.ModelProperty;
import molecules.Molecule;

public class ConcealedNonKekuleanChecker extends Checker {

	@Override
	public boolean checks(Molecule molecule, ModelProperty property) {
		return molecule.getNbKekuleStructures() == 0 && molecule.colorShift() == 0;
	}

}
