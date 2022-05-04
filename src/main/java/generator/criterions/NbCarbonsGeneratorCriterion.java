package generator.criterions;

import java.util.ArrayList;
import java.util.Map;

public class NbCarbonsGeneratorCriterion extends GeneratorCriterion2 {

	public NbCarbonsGeneratorCriterion(Operator operator, String value) {
		super(operator, value);
	}

	@Override
	public int optimizeNbHexagons() {

		int nbHexagons = -1;

		if (isUpperBound()) {

			int nbCarbons = Integer.parseInt(value);

			if (operator == Operator.LT)
				nbCarbons--;

			nbHexagons = (int) Math.ceil(((nbCarbons - 6.0) / 4.0) + 1.0);
		}

		return nbHexagons;
	}

	@Override
	public int optimizeNbCrowns(int upperBoundNbHexagons) {
		return -1;
	}

	@Override
	public void buildMap(Map<String, ArrayList<GeneratorCriterion2>> criterionsMap) {
		if (criterionsMap.get("carbons") == null)
			criterionsMap.put("carbons", new ArrayList<>());
		criterionsMap.get("carbons").add(this);
	}

}
