package view.filtering.criterions;

import molecules.Molecule;
import solveur.Aromaticity;

public class AromaticityFilteringCriterion extends FilteringCriterion {

	@Override
	public Boolean checksCriterion(Molecule molecule) {
		Aromaticity aromaticity = molecule.getAromaticity();

		for (double d : aromaticity.getLocalAromaticity())
			if (d == 0.0)
				return false;

		return true;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
