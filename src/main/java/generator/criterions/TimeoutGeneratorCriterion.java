package generator.criterions;

import java.util.ArrayList;
import java.util.Map;

public class TimeoutGeneratorCriterion extends GeneratorCriterion {

	public TimeoutGeneratorCriterion(Operator operator, String value) {
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
		if (criterionsMap.get("timeout") == null)
			criterionsMap.put("timeout", new ArrayList<>());
		criterionsMap.get("timeout").add(this);
	}

}
