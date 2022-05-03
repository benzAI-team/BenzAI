package generator.criterions;

public class DiameterGeneratorCriterion extends GeneratorCriterion2 {

	public DiameterGeneratorCriterion(Operator operator, String value) {
		super(operator, value);
	}

	@Override
	public int optimizeNbHexagons() {
		return -1;
	}

	@Override
	public int optimizeNbCrowns() {
		return -1;
	}

}
