package view.generator.boxes;

import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.ParameterizedExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

import java.util.Objects;

public class HBoxNbKekuleStructuresCriterion extends ClassicalHBoxCriterion {

	
	public HBoxNbKekuleStructuresCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void updateValidity() {
		
		if ("Min".equals(operatorChoiceBox.getValue()) || "Max".equals(operatorChoiceBox.getValue())) {
			setValid(true);
			this.getChildren().remove(fieldValue);
			removeWarningIconAndDeleteButton();
			addDeleteButton();
		}
		
		else if (!Utils.isNumber(fieldValue.getText()) || operatorChoiceBox.getValue() == null) {
			setValid(false);
			this.getChildren().remove(fieldValue);
			removeWarningIconAndDeleteButton();
			this.getChildren().addAll(fieldValue, getWarningIcon(), getDeleteButton());
		}

		else {
			setValid(true);
			removeWarningIconAndDeleteButton();
			addDeleteButton();
		}
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {		
		if (isValid()) {		
				String operator = operatorChoiceBox.getValue();	
				if (!Objects.equals(operator, "min") && !Objects.equals(operator, "max"))
					modelPropertySet.getById("kekule").addExpression(new BinaryNumericalExpression("kekuleNumber", operator, Integer.decode(fieldValue.getText())));			
				else 
					modelPropertySet.getById("kekule").addExpression(new ParameterizedExpression("kekuleNumber", operator));
			}
	}
}
