package generator.criterions.symmetries;

public class Rotation60MirrorSymmetryCriterion extends SymmetryGeneratorCriterion {

	@Override
	public int optimizeNbHexagons() {
		return -1;
	}

	@Override
	public int optimizeNbCrowns(int upperBoundNbHexagons) {
		return (upperBoundNbHexagons + 10) / 6;
	}

}
