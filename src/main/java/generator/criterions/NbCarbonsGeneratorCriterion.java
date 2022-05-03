package generator.criterions;

public class NbCarbonsGeneratorCriterion extends GeneratorCriterion2 {

	public NbCarbonsGeneratorCriterion(Operator operator, String value) {
		super(operator, value);
	}

	@Override
	public int optimizeNbHexagons() {

		int nbHexagons = -1;

		if (isUpperBound()) {

			int nbCarbons = Integer.parseInt(value);

			if (operator == Operator.LT)
				nbCarbons--;

			nbHexagons = (int) Math.ceil(((nbCarbons - 6.0) / 4.0) + 1.0);
		}

		return nbHexagons;
	}

	@Override
	public int optimizeNbCrowns(int upperBoundNbHexagons) {
		return -1;
	}

}
