package view.generator.boxes;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import modelProperty.ModelProperty;
import modelProperty.ModelPropertySet;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.ParameterizedExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public class HBoxNbKekuleStructuresCriterion extends ClassicalHBoxCriterion {

	
	public HBoxNbKekuleStructuresCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion, ModelProperty modelProperty) {
		super(parent, choiceBoxCriterion, modelProperty);
		operatorChoiceBox.getItems().addAll("Min", "Max");
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
					modelPropertySet.getById("kekuleNumber").addExpression(new BinaryNumericalExpression("kekuleNumber", operator, Integer.decode(fieldValue.getText())));			
				else 
					modelPropertySet.getById("kekuleNumber").addExpression(new ParameterizedExpression("kekuleNumber", operator));
			}
	}

}
