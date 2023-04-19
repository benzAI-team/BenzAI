package view.generator.boxes;

import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.ParameterizedExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxNbKekuleStructuresCriterion extends ClassicalHBoxCriterion {

	
	public HBoxNbKekuleStructuresCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void checkValidity() {
		
		if (operatorChoiceBox.getValue().equals("Min") || operatorChoiceBox.getValue().equals("Max")) {
			setValid(true);
			this.getChildren().remove(fieldValue);
			this.getChildren().remove(getWarningIcon());
			this.getChildren().remove(getDeleteButton());
			this.getChildren().add(getDeleteButton());
		}
		
		else if (!Utils.isNumber(fieldValue.getText()) || operatorChoiceBox.getValue() == null) {
			setValid(false);
			this.getChildren().remove(fieldValue);
			this.getChildren().remove(getWarningIcon());
			this.getChildren().remove(getDeleteButton());
			this.getChildren().addAll(fieldValue, getWarningIcon(), getDeleteButton());
		}

		else {
			setValid(true);
			this.getChildren().remove(getWarningIcon());
			this.getChildren().remove(getDeleteButton());
			this.getChildren().addAll(getDeleteButton());
		}
    
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {		
		if (isValid()) {		
				String operator = operatorChoiceBox.getValue();	
				if (operator != "min" && operator != "max")
					modelPropertySet.getById("kekule").addExpression(new BinaryNumericalExpression("kekuleNumber", operator, Integer.decode(fieldValue.getText())));			
				else 
					modelPropertySet.getById("kekule").addExpression(new ParameterizedExpression("kekuleNumber", operator));
			}
	}
}
