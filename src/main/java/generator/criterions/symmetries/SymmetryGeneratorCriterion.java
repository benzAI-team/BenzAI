package generator.criterions.symmetries;

import java.util.ArrayList;
import java.util.Map;

import generator.criterions.GeneratorCriterion;

public abstract class SymmetryGeneratorCriterion extends GeneratorCriterion {
	public SymmetryGeneratorCriterion() {
		super("", "");
	}

//	@Override
//	public void buildMap(Map<String, ArrayList<GeneratorCriterion>> criterionsMap) {
//		if (criterionsMap.get("symmetries") == null)
//			criterionsMap.put("symmetries", new ArrayList<>());
//		criterionsMap.get("symmetries").add(this);
//	}
}
