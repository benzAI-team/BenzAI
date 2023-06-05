package generator.properties.model.checkers;

import generator.properties.model.ModelProperty;
import molecules.Benzenoid;

public class ConcealedNonKekuleanChecker extends Checker {

	@Override
	public boolean checks(Benzenoid molecule, ModelProperty property) {
		return molecule.getNbKekuleStructures() == 0 && molecule.colorShift() == 0;
	}

}
