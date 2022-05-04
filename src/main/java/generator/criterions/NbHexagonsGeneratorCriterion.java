package generator.criterions;

import java.util.ArrayList;
import java.util.Map;

public class NbHexagonsGeneratorCriterion extends GeneratorCriterion2 {

	public NbHexagonsGeneratorCriterion(Operator operator, String value) {
		super(operator, value);
	}

	@Override
	public int optimizeNbHexagons() {

		int nbHexagons = -1;

		if (isUpperBound()) {
			nbHexagons = Integer.parseInt(value);

			if (operator == Operator.LT)
				nbHexagons--;
		}

		return nbHexagons;
	}

	@Override
	public int optimizeNbCrowns(int upperBoundNbHexagons) {
		return -1;
	}

	@Override
	public void buildMap(Map<String, ArrayList<GeneratorCriterion2>> criterionsMap) {
		if (criterionsMap.get("hexagons") == null)
			criterionsMap.put("hexagons", new ArrayList<>());
		criterionsMap.get("hexagons").add(this);
	}

}
