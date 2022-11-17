package generator.properties.solver;

import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxNbSolutionsCriterion;

public class SolutionNumberProperty extends SolverProperty {

	public SolutionNumberProperty() {
		super("solution_number", "Number of solutions", new SolutionNumberSpecifier());
	}

	@Override
	public HBoxCriterion getHBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxNbSolutionsCriterion(parent, choiceBoxCriterion);
	}

}
