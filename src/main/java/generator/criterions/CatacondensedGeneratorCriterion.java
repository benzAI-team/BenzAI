package generator.criterions;

import java.util.ArrayList;
import java.util.Map;

public class CatacondensedGeneratorCriterion extends GeneratorCriterion {

	public CatacondensedGeneratorCriterion() {
		super(Operator.NONE, "");
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
		if (criterionsMap.get("catacondensed") == null)
			criterionsMap.put("catacondensed", new ArrayList<>());
		criterionsMap.get("catacondensed").add(this);
	}

}
