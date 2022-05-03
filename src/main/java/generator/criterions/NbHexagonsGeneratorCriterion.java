package generator.criterions;

public class NbHexagonsGeneratorCriterion extends GeneratorCriterion2 {

	public NbHexagonsGeneratorCriterion(Operator operator, String value) {
		super(operator, value);
	}

	@Override
	public int optimizeNbHexagons() {

		int nbHexagons = -1;

		if (isUpperBound()) {
			nbHexagons = Integer.parseInt(value);

			if (operator == Operator.LT)
				nbHexagons--;
		}

		return nbHexagons;
	}

	@Override
	public int optimizeNbCrowns(int upperBoundNbHexagons) {

		return -1;
	}

}
