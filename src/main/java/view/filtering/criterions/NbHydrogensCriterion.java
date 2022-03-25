package view.filtering.criterions;

import molecules.Molecule;

public class NbHydrogensCriterion extends FilteringCriterion {

	private FilteringOperator operator;
	private int nbHydrogens;

	public NbHydrogensCriterion(FilteringOperator operator, int nbHydrogens) {
		this.operator = operator;
		this.nbHydrogens = nbHydrogens;
	}

	@Override
	public Boolean checksCriterion(Molecule molecule) {
		switch (operator) {

		case LEQ:
			return molecule.getNbHydrogens() <= nbHydrogens;

		case LT:
			return molecule.getNbHydrogens() < nbHydrogens;

		case EQ:
			return molecule.getNbHydrogens() == nbHydrogens;

		case NEQ:
			return molecule.getNbHydrogens() != nbHydrogens;

		case GT:
			return molecule.getNbHydrogens() > nbHydrogens;

		case GEQ:
			return molecule.getNbHydrogens() >= nbHydrogens;

		case EVEN:
			return molecule.getNbHydrogens() % 2 == 0;

		case ODD:
			return molecule.getNbHydrogens() % 2 == 1;

		default:
			return null;

		}
	}

	@Override
	public String toString() {
		return "NbHydrogensCriterion";
	}
}
