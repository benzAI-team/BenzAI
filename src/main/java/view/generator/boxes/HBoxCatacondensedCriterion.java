package view.generator.boxes;

import properties.PropertySet;
import properties.expression.PropertyExpression;
import properties.expression.SubjectExpression;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxCatacondensedCriterion extends HBoxModelCriterion {

	public HBoxCatacondensedCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
		setValid(true);
	}

	@Override
	public void updateValidity() {
		setValid(true);
		setBounding(false);
		getPane().refreshGlobalValidity();
	}

	@Override
	protected void initialize() {
		addDeleteButton();
	}


	@Override
	public void assign(PropertyExpression propertyExpression) {
	}

	@Override
	public void initEventHandling() {}

	@Override
	public void addPropertyExpression(PropertySet modelPropertySet) {
		 modelPropertySet.getById("catacondensed").addExpression(new SubjectExpression("catacondensed"));
	}
}
