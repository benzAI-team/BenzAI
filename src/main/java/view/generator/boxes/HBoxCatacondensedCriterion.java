package view.generator.boxes;

import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.SubjectExpression;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxCatacondensedCriterion extends HBoxModelCriterion {

	public HBoxCatacondensedCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
		setValid(true);
	}

	@Override
	protected void checkValidity() {
		setValid(true);
		getPane().refreshGenerationPossibility();
	}

	@Override
	protected void initialize() {
		this.getChildren().add(getDeleteButton());
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {
		 modelPropertySet.getById("catacondensed").addExpression(new SubjectExpression("catacondensed"));
	}
}
