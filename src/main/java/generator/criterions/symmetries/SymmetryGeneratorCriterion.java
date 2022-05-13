package generator.criterions.symmetries;

import java.util.ArrayList;
import java.util.Map;

import generator.criterions.GeneratorCriterion2;

public abstract class SymmetryGeneratorCriterion extends GeneratorCriterion2 {
	public SymmetryGeneratorCriterion() {
		super(Operator.NONE, "");
	}

	@Override
	public void buildMap(Map<String, ArrayList<GeneratorCriterion2>> criterionsMap) {
		if (criterionsMap.get("symmetries") == null)
			criterionsMap.put("symmetries", new ArrayList<>());
		criterionsMap.get("symmetries").add(this);
	}
}
