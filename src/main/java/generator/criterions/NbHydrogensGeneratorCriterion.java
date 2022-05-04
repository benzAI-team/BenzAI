package generator.criterions;

import java.util.ArrayList;
import java.util.Map;

public class NbHydrogensGeneratorCriterion extends GeneratorCriterion2 {

	public NbHydrogensGeneratorCriterion(Operator operator, String value) {
		super(operator, value);
	}

	@Override
	public int optimizeNbHexagons() {
		int nbHexagons = -1;

		if (isUpperBound()) {

			int nbHydrogens = Integer.parseInt(value);

			if (operator == Operator.LT)
				nbHydrogens--;

			nbHexagons = (int) Math.ceil(((nbHydrogens - 8) / 2.0) + 2.0);
		}

		return nbHexagons;
	}

	@Override
	public int optimizeNbCrowns(int upperBoundNbHexagons) {
		return -1;
	}

	@Override
	public void buildMap(Map<String, ArrayList<GeneratorCriterion2>> criterionsMap) {
		if (criterionsMap.get("hydrogens") == null)
			criterionsMap.put("hydrogens", new ArrayList<>());
		criterionsMap.get("hydrogens").add(this);
	}

}
