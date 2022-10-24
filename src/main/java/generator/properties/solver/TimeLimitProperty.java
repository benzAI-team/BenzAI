package generator.properties.solver;

import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxTimeoutCriterion;

public class TimeLimitProperty extends SolverProperty {

	public TimeLimitProperty() {
		super("timeout", "Time limit", new TimeLimitSpecifier());
	}

	@Override
	public HBoxCriterion getHBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxTimeoutCriterion(parent, choiceBoxCriterion);
	}

}
