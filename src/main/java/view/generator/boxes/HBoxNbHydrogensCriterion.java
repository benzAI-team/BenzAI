package view.generator.boxes;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import modelProperty.ModelPropertySet;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.ParameterizedExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public class HBoxNbHydrogensCriterion extends ClassicalHBoxCriterion{

	public HBoxNbHydrogensCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
		operatorChoiceBox.getItems().addAll("EVEN", "ODD");
	}

	@Override
	public void checkValidity() {
		
		if (operatorChoiceBox.getValue().equals("EVEN") || operatorChoiceBox.getValue().equals("ODD")) {
			setValid(true);
			this.getChildren().remove(fieldValue);
			this.getChildren().remove(getWarningIcon());
			this.getChildren().remove(deleteButton);
			this.getChildren().add(deleteButton);
		}
		
		else if (! Utils.isNumber(fieldValue.getText()) || operatorChoiceBox.getValue() == null) {
			setValid(false);
			this.getChildren().remove(getWarningIcon());
			this.getChildren().remove(deleteButton);
			this.getChildren().remove(fieldValue);
			this.getChildren().addAll(fieldValue, getWarningIcon(), deleteButton);
		}
		
		else {
			setValid(true);
			this.getChildren().remove(getWarningIcon());
			this.getChildren().remove(deleteButton);
			this.getChildren().remove(fieldValue);
			this.getChildren().addAll(fieldValue, deleteButton);
		}
		
		getGeneratorPane().refreshGenerationPossibility();
	}
	
	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {
		if (isValid()) {
			String operator = operatorChoiceBox.getValue();	
			if (operator != "even" && operator != "odd")
				modelPropertySet.getBySubject("hydrogens").addExpression(new BinaryNumericalExpression("hydrogens", operator, Integer.decode(fieldValue.getText())));			
			else 
				modelPropertySet.getBySubject("hydrogens").addExpression(new ParameterizedExpression("hydrogens", operator));
		}
	}
}
