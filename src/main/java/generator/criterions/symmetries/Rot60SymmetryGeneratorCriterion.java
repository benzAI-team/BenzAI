package generator.criterions.symmetries;

public class Rot60SymmetryGeneratorCriterion extends SymmetryGeneratorCriterion {

	@Override
	public int optimizeNbHexagons() {
		return -1;
	}

	@Override
	public int optimizeNbCrowns(int upperBoundNbHexagons) {
		return (upperBoundNbHexagons + 10) / 6;
	}

}
