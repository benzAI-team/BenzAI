package generator.properties.solver;

import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxTimeoutCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class TimeLimitProperty extends SolverProperty {

	TimeLimitProperty() {
		super("timeout", "Time limit", new TimeLimitSpecifier());
	}

	@Override
	public HBoxCriterion makeHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxTimeoutCriterion(parent, choiceBoxCriterion);
	}

}
