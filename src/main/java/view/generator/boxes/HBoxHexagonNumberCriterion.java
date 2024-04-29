package view.generator.boxes;

import properties.PropertySet;
import properties.expression.BinaryNumericalExpression;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxHexagonNumberCriterion extends HBoxBoundingCriterion {
	
	public HBoxHexagonNumberCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	public void addPropertyExpression(PropertySet modelPropertySet) {
		if (isValid())
			modelPropertySet.getById("hexagons").addExpression(new BinaryNumericalExpression("hexagons", getOperatorChoiceBox().getValue(), Integer.decode(getFieldValue().getText())));
	}

}
