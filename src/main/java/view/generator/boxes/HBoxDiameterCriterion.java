package view.generator.boxes;

import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.BinaryNumericalExpression;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxDiameterCriterion extends HBoxBoundingCriterion {

	public HBoxDiameterCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {
		if (isValid()) 
			modelPropertySet.getById("diameter").addExpression(new BinaryNumericalExpression("diameter", getOperatorChoiceBox().getValue(), Integer.decode(getFieldValue().getText())));
	}
}
