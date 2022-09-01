package view.generator.boxes;

import java.util.ArrayList;
import generator.GeneratorCriterion;
import modelProperty.ModelPropertySet;
import modelProperty.expression.BinaryNumericalExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public class HBoxHexagonNumberCriterion extends ClassicalHBoxCriterion {
	
	public HBoxHexagonNumberCriterion(GeneratorPane generatorPane, ChoiceBoxCriterion choiceBoxCriterion) {
		super(generatorPane, choiceBoxCriterion);
	}

	@Override
	protected void checkValidity() {
		
		if (! Utils.isNumber(fieldValue.getText()) || operatorChoiceBox.getValue() == null) {
			setValid(false);
			this.getChildren().remove(getWarningIcon());
			this.getChildren().remove(deleteButton);
			this.getChildren().addAll(getWarningIcon(), deleteButton);
		}
		
		else {
			setValid(true);
			this.getChildren().remove(getWarningIcon());
			this.getChildren().remove(deleteButton);
			this.getChildren().add(deleteButton);
		}
		
		getGeneratorPane().refreshGenerationPossibility();
	}
	
	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {	
		if (isValid())
			modelPropertySet.getBySubject("hexagons").addExpression(new BinaryNumericalExpression("hexagons", operatorChoiceBox.getValue(), Integer.decode(fieldValue.getText())));
	}

}
