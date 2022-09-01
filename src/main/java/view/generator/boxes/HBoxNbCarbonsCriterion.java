package view.generator.boxes;

import java.util.ArrayList;
import generator.GeneratorCriterion;
import modelProperty.ModelPropertySet;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.ParameterizedExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public class HBoxNbCarbonsCriterion extends ClassicalHBoxCriterion{

	public HBoxNbCarbonsCriterion(GeneratorPane generatorPane, ChoiceBoxCriterion choiceBoxCriterion) {
		super(generatorPane, choiceBoxCriterion);
		operatorChoiceBox.getItems().addAll("even", "odd");
	}

	@Override
	public void checkValidity() {
		
		if (operatorChoiceBox.getValue().equals("even") || operatorChoiceBox.getValue().equals("odd")) {
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
		
		getGeneratorPane().refreshGenerationPossibility();
		
	}
	
	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {	
		if (isValid()) {
			String operator = operatorChoiceBox.getValue();	
			if (operator != "even" && operator != "odd")
				modelPropertySet.getBySubject("carbons").addExpression(new BinaryNumericalExpression("carbons", operator, Integer.decode(fieldValue.getText())));			
			else 
				modelPropertySet.getBySubject("carbons").addExpression(new ParameterizedExpression("carbons", operator));
		}
	}

}
