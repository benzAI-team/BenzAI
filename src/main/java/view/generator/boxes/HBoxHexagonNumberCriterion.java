package view.generator.boxes;

import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.BinaryNumericalExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxHexagonNumberCriterion extends ClassicalHBoxCriterion {
	
	public HBoxHexagonNumberCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void updateValidity() {
		if (! Utils.isNumber(fieldValue.getText()) || operatorChoiceBox.getValue() == null) {
			setValid(false);
			removeWarningIconAndDeleteButton();
			addWarningIconAndDeleteButton();
		}
		
		else {
			setValid(true);
			removeWarningIconAndDeleteButton();
			this.getChildren().add(getDeleteButton());
		}
		getPane().refreshGenerationPossibility();
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {	
		if (isValid())
			modelPropertySet.getById("hexagons").addExpression(new BinaryNumericalExpression("hexagons", operatorChoiceBox.getValue(), Integer.decode(fieldValue.getText())));
	}

}
