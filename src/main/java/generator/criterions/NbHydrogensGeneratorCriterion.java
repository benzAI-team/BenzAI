package generator.criterions;

public class NbHydrogensGeneratorCriterion extends GeneratorCriterion2 {

	public NbHydrogensGeneratorCriterion(Operator operator, String value) {
		super(operator, value);
	}

	@Override
	public int optimizeNbHexagons() {
		int nbHexagons = -1;

		if (isUpperBound()) {

			int nbHydrogens = Integer.parseInt(value);

			if (operator == Operator.LT)
				nbHydrogens--;

			nbHexagons = (int) Math.ceil(((nbHydrogens - 8) / 2.0) + 2.0);
		}

		return nbHexagons;
	}

	@Override
	public int optimizeNbCrowns() {
		// TODO Auto-generated method stub
		return -1;
	}

}
