package view.filtering.criterions;

import molecules.Molecule;

public class ConcealedNonKekuleanCriterion extends FilteringCriterion {

	@Override
	public Boolean checksCriterion(Molecule molecule) {
		return molecule.getNbKekuleStructures() == 0.0 && molecule.colorShift() == 0;
	}

	@Override
	public String toString() {
		return "ConcealedNonKekuleanCriterion";
	}

}
