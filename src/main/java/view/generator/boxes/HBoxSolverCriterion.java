package view.generator.boxes;

import generator.properties.solver.SolverPropertySet;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public abstract class HBoxSolverCriterion extends HBoxCriterion {

	public HBoxSolverCriterion(GeneratorPane generatorPane, ChoiceBoxCriterion choiceBoxCriterion) {
		super(generatorPane, choiceBoxCriterion);
	}

	@Override
	protected void checkValidity() {
	}

	@Override
	protected void initialize() {
	}

	abstract public void addPropertyExpression(SolverPropertySet propertySet);

}
