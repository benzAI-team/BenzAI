package properties.solver;

import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxNbSolutionsCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class SolutionNumberProperty extends SolverProperty {

	SolutionNumberProperty() {
		super("solution_number", "Number of solutions", new SolutionNumberSpecifier());
	}

	@Override
	public HBoxCriterion makeHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxNbSolutionsCriterion(parent, choiceBoxCriterion);
	}

}
