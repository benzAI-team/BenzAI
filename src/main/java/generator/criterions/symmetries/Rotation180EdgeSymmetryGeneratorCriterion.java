package generator.criterions.symmetries;

public class Rotation180EdgeSymmetryGeneratorCriterion extends SymmetryGeneratorCriterion {

	@Override
	public int optimizeNbHexagons() {
		return -1;
	}

	@Override
	public int optimizeNbCrowns(int upperBoundNbHexagons) {
		return -1;
	}

}
