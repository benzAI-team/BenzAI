package view.filtering.criterions;

import molecules.Molecule;

public class NbKekuleStructuresCriterion extends FilteringCriterion {

	private FilteringOperator operator;
	private double nbKekuleStructures;

	public NbKekuleStructuresCriterion(FilteringOperator operator, double nbKekuleStructures) {
		this.operator = operator;
		this.nbKekuleStructures = nbKekuleStructures;
	}

	@Override
	public Boolean checksCriterion(Molecule molecule) {
		switch (operator) {

		case LEQ:
			return molecule.getNbKekuleStructures() <= nbKekuleStructures;

		case LT:
			return molecule.getNbKekuleStructures() < nbKekuleStructures;

		case EQ:
			return molecule.getNbKekuleStructures() == nbKekuleStructures;

		case NEQ:
			return molecule.getNbKekuleStructures() != nbKekuleStructures;

		case GT:
			return molecule.getNbKekuleStructures() > nbKekuleStructures;

		case GEQ:
			return molecule.getNbKekuleStructures() >= nbKekuleStructures;

		default:
			return null;

		}
	}

	@Override
	public String toString() {
		return "NbKekuleStructuresCriterion";
	}
}
