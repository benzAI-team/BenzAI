package generator.criterions.symmetries;

public class Mirror120HorizontalSymmetryGeneratorCriterion extends SymmetryGeneratorCriterion {

	@Override
	public int optimizeNbHexagons() {
		return -1;
	}

	@Override
	public int optimizeNbCrowns(int upperBoundNbHexagons) {
		return (upperBoundNbHexagons + 4) / 3;
	}

}
