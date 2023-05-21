package view.generator.boxes;

import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.ParameterizedExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

import java.util.Objects;

public class HBoxNbKekuleStructuresCriterion extends HBoxBoundingCriterion {

	
	public HBoxNbKekuleStructuresCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void updateValidity() {
		
		if ("Min".equals(getOperatorChoiceBox().getValue()) || "Max".equals(getOperatorChoiceBox().getValue())) {
			setValid(true);
			this.getChildren().remove(getFieldValue());
			removeWarningIconAndDeleteButton();
			addDeleteButton();
		}
		
		else if (!Utils.isNumber(getFieldValue().getText()) || getOperatorChoiceBox().getValue() == null) {
			setValid(false);
			this.getChildren().remove(getFieldValue());
			removeWarningIconAndDeleteButton();
			this.getChildren().addAll(getFieldValue(), getWarningIcon(), getDeleteButton());
		}

		else {
			setValid(true);
			removeWarningIconAndDeleteButton();
			addDeleteButton();
		}
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {		
		if (isValid()) {		
				String operator = getOperatorChoiceBox().getValue();
				if (!Objects.equals(operator, "min") && !Objects.equals(operator, "max"))
					modelPropertySet.getById("kekule").addExpression(new BinaryNumericalExpression("kekuleNumber", operator, Integer.decode(getFieldValue().getText())));
				else 
					modelPropertySet.getById("kekule").addExpression(new ParameterizedExpression("kekuleNumber", operator));
			}
	}
}
