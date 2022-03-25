package view.filtering.criterions;

import molecules.Molecule;

public class NbHexagonsCriterion extends FilteringCriterion {

	private FilteringOperator operator;
	private int nbHexagons;

	public NbHexagonsCriterion(FilteringOperator operator, int nbHexagons) {
		this.operator = operator;
		this.nbHexagons = nbHexagons;
	}

	@Override
	public Boolean checksCriterion(Molecule molecule) {
		switch (operator) {

		case LEQ:
			return molecule.getNbHexagons() <= nbHexagons;

		case LT:
			return molecule.getNbHexagons() < nbHexagons;

		case EQ:
			return molecule.getNbHexagons() == nbHexagons;

		case NEQ:
			return molecule.getNbHexagons() != nbHexagons;

		case GT:
			return molecule.getNbHexagons() > nbHexagons;

		case GEQ:
			return molecule.getNbHexagons() >= nbHexagons;

		default:
			return null;

		}
	}

	@Override
	public String toString() {
		return "NbHexagonsCriterion";
	}
}
