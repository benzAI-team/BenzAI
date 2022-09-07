package view.generator.boxes;

import modelProperty.ModelPropertySet;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public class HBoxDefaultCriterion extends HBoxCriterion {

	public HBoxDefaultCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void checkValidity() {
		setValid(false);
	}

	@Override
	protected void initialize() {
		this.getChildren().addAll(getWarningIcon(), getDeleteButton());
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {

	}

	
}
