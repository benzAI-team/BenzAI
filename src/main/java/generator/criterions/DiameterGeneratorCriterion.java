package generator.criterions;

import java.util.ArrayList;
import java.util.Map;

public class DiameterGeneratorCriterion extends GeneratorCriterion2 {

	public DiameterGeneratorCriterion(Operator operator, String value) {
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
	public void buildMap(Map<String, ArrayList<GeneratorCriterion2>> criterionsMap) {
		if (criterionsMap.get("diameter") == null)
			criterionsMap.put("diameter", new ArrayList<>());
		criterionsMap.get("diameter").add(this);
	}
}
