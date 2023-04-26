package generator.properties.solver;

import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxNbSolutionsCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class SolutionNumberProperty extends SolverProperty {

	public SolutionNumberProperty() {
		super("solution_number", "Number of solutions", new SolutionNumberSpecifier());
	}

	@Override
	public HBoxCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxNbSolutionsCriterion(parent, choiceBoxCriterion);
	}

}
