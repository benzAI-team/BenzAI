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
	public int optimizeNbCrowns() {

		int nbHexagons = optimizeNbHexagons();
		int nbCrowns = -1;

		if (isUpperBound()) {
			nbCrowns = (int) Math.floor((((double) ((double) nbHexagons + 1)) / 2.0) + 1.0);

			if (nbHexagons % 2 == 1)
				nbCrowns--;
		}

		return nbCrowns;
	}

}
