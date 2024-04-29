package properties.checkers;

import properties.ModelProperty;
import benzenoid.Benzenoid;

public class ConcealedNonKekuleanChecker extends Checker {

	@Override
	public boolean checks(Benzenoid molecule, ModelProperty property) {
		return molecule.getNbKekuleStructures() == 0 && molecule.colorShift() == 0;
	}

}
