package view.generator.boxes;

import generator.properties.solver.SolverPropertySet;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public abstract class HBoxSolverCriterion extends HBoxCriterion {

	HBoxSolverCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	public abstract void addPropertyExpression(SolverPropertySet propertySet);

}
