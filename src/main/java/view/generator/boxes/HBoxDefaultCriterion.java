package view.generator.boxes;

import generator.properties.model.ModelPropertySet;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxDefaultCriterion extends HBoxModelCriterion {

	public HBoxDefaultCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
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
