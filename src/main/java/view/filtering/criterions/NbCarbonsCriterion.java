package view.filtering.criterions;

import molecules.Molecule;

public class NbCarbonsCriterion extends FilteringCriterion {

	private FilteringOperator operator;
	private int nbCarbons;

	public NbCarbonsCriterion(FilteringOperator operator, int nbCarbons) {
		this.operator = operator;
		this.nbCarbons = nbCarbons;
	}

	@Override
	public Boolean checksCriterion(Molecule molecule) {
		switch (operator) {

		case LEQ:
			return molecule.getNbNodes() <= nbCarbons;

		case LT:
			return molecule.getNbNodes() < nbCarbons;

		case EQ:
			return molecule.getNbNodes() == nbCarbons;

		case NEQ:
			return molecule.getNbNodes() != nbCarbons;

		case GT:
			return molecule.getNbNodes() > nbCarbons;

		case GEQ:
			return molecule.getNbNodes() >= nbCarbons;

		case EVEN:
			return molecule.getNbNodes() % 2 == 0;

		case ODD:
			return molecule.getNbNodes() % 2 == 1;

		default:
			return null;

		}
	}

	@Override
	public String toString() {
		return "NbCarbonsCriterion";
	}
}
