package view.generator.boxes;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import generator.properties.PropertySet;
import modelProperty.ModelProperty;
import modelProperty.ModelPropertySet;
import modelProperty.expression.BinaryNumericalExpression;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxDiameterCriterion extends ClassicalHBoxCriterion{

	public HBoxDiameterCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {
		if (isValid()) 
			modelPropertySet.getById("diameter").addExpression(new BinaryNumericalExpression("diameter", operatorChoiceBox.getValue(), Integer.decode(fieldValue.getText())));
	}
}
