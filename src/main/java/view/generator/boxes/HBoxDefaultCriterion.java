package view.generator.boxes;

import properties.PropertySet;
import properties.expression.PropertyExpression;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxDefaultCriterion extends HBoxModelCriterion {

	public HBoxDefaultCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	public void updateValidity() {
		setValid(false);
		setBounding(false);
	}

	@Override
	protected void initialize() {
		addWarningIconAndDeleteButton();
	}

	@Override
	public void assign(PropertyExpression propertyExpression) {}

	@Override
	public void initEventHandling() {}

	@Override
	public void addPropertyExpression(PropertySet modelPropertySet) {}

	
}
