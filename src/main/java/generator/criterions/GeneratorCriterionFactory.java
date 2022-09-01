package generator.criterions;

import generator.GeneratorCriterion;

public class GeneratorCriterionFactory {

	public static GeneratorCriterion build(String name, String operator, int value) {
		return new GeneratorCriterion(name, operator, value);
	}
}
