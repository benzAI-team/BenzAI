package view.filtering.criterions;

import java.util.ArrayList;

import molecules.Molecule;
import view.generator.boxes.HBoxCriterion;

public abstract class FilteringCriterion {

	public abstract Boolean checksCriterion(Molecule molecule);

	public static boolean checksCriterions(Molecule molecule, ArrayList<HBoxCriterion> arrayList) {

		for (HBoxCriterion criterion : arrayList) {
			if (!criterion.checksCriterion(molecule))
				return false;
		}
		return true;
	}

	public abstract String toString();
}
