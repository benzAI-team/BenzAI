package generator.criterions.symmetries;

public class VerticalSymmetryGeneratorCriterion extends SymmetryGeneratorCriterion {

	@Override
	public int optimizeNbHexagons() {
		return -1;
	}

	@Override
	public int optimizeNbCrowns(int upperBoundNbHexagons) {
		return -1;
	}

}
