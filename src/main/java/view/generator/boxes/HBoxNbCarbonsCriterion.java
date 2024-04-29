package view.generator.boxes;

import properties.PropertySet;
import properties.expression.BinaryNumericalExpression;
import properties.expression.ParameterizedExpression;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

import java.util.Objects;

public class HBoxNbCarbonsCriterion extends HBoxBoundingCriterion {

	public HBoxNbCarbonsCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
		getOperatorChoiceBox().getItems().addAll("even", "odd");
	}

	@Override
	public void updateValidity() {
		if ("even".equals(getOperatorChoiceBox().getValue()) || "odd".equals(getOperatorChoiceBox().getValue())) {
			setValid(true);
			setBounding(false);
			this.getChildren().remove(getFieldValue());
			removeWarningIconAndDeleteButton();
			addDeleteButton();
			getPane().refreshGlobalValidity();
		}
		else
			super.updateValidity();
	}

	//@Override
	//public void assign(PropertyExpression propertyExpression) {
		// TODO changer
	//}

	@Override
	public void addPropertyExpression(PropertySet modelPropertySet) {
		if (isValid()) {
			String operator = getOperatorChoiceBox().getValue();
			if (!Objects.equals(operator, "even") && !Objects.equals(operator, "odd"))
				modelPropertySet.getById("carbons").addExpression(new BinaryNumericalExpression("carbons", operator, Integer.decode(getFieldValue().getText())));
			else 
				modelPropertySet.getById("carbons").addExpression(new ParameterizedExpression("carbons", operator));
		}
	}
}
