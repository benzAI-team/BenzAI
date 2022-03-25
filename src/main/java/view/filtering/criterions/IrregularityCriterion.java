package view.filtering.criterions;

import molecules.Molecule;

public class IrregularityCriterion extends FilteringCriterion {

	private FilteringOperator operator;
	private double irregularity;

	public IrregularityCriterion(FilteringOperator operator, double irregularity) {
		this.operator = operator;
		this.irregularity = irregularity;
	}

	@Override
	public Boolean checksCriterion(Molecule molecule) {

		if (molecule.getNbHexagons() == 1)
			return false;

		switch (operator) {

		case LEQ:
			return molecule.getIrregularity().getXI() <= irregularity;

		case LT:
			return molecule.getIrregularity().getXI() < irregularity;

		case EQ:
			return molecule.getIrregularity().getXI() == irregularity;

		case NEQ:
			return molecule.getIrregularity().getXI() != irregularity;

		case GT:
			return molecule.getIrregularity().getXI() > irregularity;

		case GEQ:
			return molecule.getIrregularity().getXI() >= irregularity;

		default:
			return null;

		}
	}

	@Override
	public String toString() {
		return "IrregularityCriterion";
	}
}
