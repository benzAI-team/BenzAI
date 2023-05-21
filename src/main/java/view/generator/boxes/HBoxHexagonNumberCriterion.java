package view.generator.boxes;

import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.BinaryNumericalExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxHexagonNumberCriterion extends HBoxBoundingCriterion {
	
	public HBoxHexagonNumberCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void updateValidity() {
		if (! Utils.isNumber(getFieldValue().getText()) || getOperatorChoiceBox().getValue() == null) {
			setValid(false);
			removeWarningIconAndDeleteButton();
			addWarningIconAndDeleteButton();
		}
		
		else {
			setValid(true);
			removeWarningIconAndDeleteButton();
			addDeleteButton();
		}
		getPane().refreshGenerationPossibility();
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {	
		if (isValid())
			modelPropertySet.getById("hexagons").addExpression(new BinaryNumericalExpression("hexagons", getOperatorChoiceBox().getValue(), Integer.decode(getFieldValue().getText())));
	}

}
