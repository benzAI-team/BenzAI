package view.generator.boxes;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import generator.properties.PropertySet;
import modelProperty.ModelProperty;
import modelProperty.ModelPropertySet;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.ParameterizedExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxNbHydrogensCriterion extends ClassicalHBoxCriterion{

	public HBoxNbHydrogensCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
		operatorChoiceBox.getItems().addAll("EVEN", "ODD");
	}

	@Override
	public void checkValidity() {
		
		if (operatorChoiceBox.getValue().equals("EVEN") || operatorChoiceBox.getValue().equals("ODD")) {
			setValid(true);
			this.getChildren().remove(fieldValue);
			this.getChildren().remove(getWarningIcon());
			this.getChildren().remove(getDeleteButton());
			this.getChildren().add(getDeleteButton());
		}
		
		else if (! Utils.isNumber(fieldValue.getText()) || operatorChoiceBox.getValue() == null) {
			setValid(false);
			this.getChildren().remove(getWarningIcon());
			this.getChildren().remove(getDeleteButton());
			this.getChildren().remove(fieldValue);
			this.getChildren().addAll(fieldValue, getWarningIcon(), getDeleteButton());
		}
		
		else {
			setValid(true);
			this.getChildren().remove(getWarningIcon());
			this.getChildren().remove(getDeleteButton());
			this.getChildren().remove(fieldValue);
			this.getChildren().addAll(fieldValue, getDeleteButton());
		}
		
		getPane().refreshGenerationPossibility();
	}
	
	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {
		if (isValid()) {
			String operator = operatorChoiceBox.getValue();	
			if (operator != "even" && operator != "odd")
				modelPropertySet.getById("hydrogens").addExpression(new BinaryNumericalExpression("hydrogens", operator, Integer.decode(fieldValue.getText())));			
			else 
				modelPropertySet.getById("hydrogens").addExpression(new ParameterizedExpression("hydrogens", operator));
		}
	}
}
