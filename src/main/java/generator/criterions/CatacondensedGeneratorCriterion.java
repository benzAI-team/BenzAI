package generator.criterions;

public class CatacondensedGeneratorCriterion extends GeneratorCriterion2 {

	public CatacondensedGeneratorCriterion() {
		super(Operator.NONE, "");
	}

	@Override
	public int optimizeNbHexagons() {
		return -1;
	}

	@Override
	public int optimizeNbCrowns(int upperBoundNbHexagons) {
		return -1;
	}

}
