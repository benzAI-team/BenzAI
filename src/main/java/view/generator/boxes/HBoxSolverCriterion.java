package view.generator.boxes;

import generator.properties.solver.SolverPropertySet;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public abstract class HBoxSolverCriterion extends HBoxCriterion {

	public HBoxSolverCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void updateValidity() {
	}

	@Override
	protected void initialize() {
	}

	public abstract void addPropertyExpression(SolverPropertySet propertySet);

}
