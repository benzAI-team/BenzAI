package generator.criterions.patterns;

import java.util.ArrayList;
import java.util.Map;

import generator.criterions.GeneratorCriterion;
import generator.patterns.PatternResolutionInformations;

public abstract class PatternGeneratorCriterion extends GeneratorCriterion {

	protected PatternResolutionInformations patternsInformations;

	public PatternGeneratorCriterion(PatternResolutionInformations patternsInformations) {
		super(Operator.NONE, "");
		this.patternsInformations = patternsInformations;
	}

	public PatternResolutionInformations getPatternsInformations() {
		return patternsInformations;
	}

	@Override
	public int optimizeNbHexagons() {
		return -1;
	}

	@Override
	public int optimizeNbCrowns(int upperBoundNbHexagons) {
		return -1;
	}

	@Override
	public void buildMap(Map<String, ArrayList<GeneratorCriterion>> criterionsMap) {
		if (criterionsMap.get("pattern") == null)
			criterionsMap.put("pattern", new ArrayList<>());
		criterionsMap.get("pattern").add(this);
	}

}
