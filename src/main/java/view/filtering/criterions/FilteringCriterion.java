package view.filtering.criterions;

import java.util.ArrayList;

import molecules.Molecule;

public abstract class FilteringCriterion {

	public abstract Boolean checksCriterion(Molecule molecule);

	public static boolean checksCriterions(Molecule molecule, ArrayList<FilteringCriterion> criterions) {

		for (FilteringCriterion criterion : criterions) {
			if (!criterion.checksCriterion(molecule))
				return false;
		}
		return true;
	}

	public abstract String toString();
}
