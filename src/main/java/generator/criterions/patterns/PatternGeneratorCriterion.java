package generator.criterions.patterns;

import generator.criterions.GeneratorCriterion2;
import generator.fragments.FragmentResolutionInformations;

public abstract class PatternGeneratorCriterion extends GeneratorCriterion2 {

	protected FragmentResolutionInformations patternsInformations;

	public PatternGeneratorCriterion(FragmentResolutionInformations patternsInformations) {
		super(Operator.NONE, "");
		this.patternsInformations = patternsInformations;
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
