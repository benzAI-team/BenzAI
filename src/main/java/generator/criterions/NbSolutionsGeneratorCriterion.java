package generator.criterions;

import java.util.ArrayList;
import java.util.Map;

public class NbSolutionsGeneratorCriterion extends GeneratorCriterion {

	public NbSolutionsGeneratorCriterion(Operator operator, String value) {
		super(operator, value);
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
		if (criterionsMap.get("solutions") == null)
			criterionsMap.put("solutions", new ArrayList<>());
		criterionsMap.get("solutions").add(this);
	}
}
