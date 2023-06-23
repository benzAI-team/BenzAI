package view.generator.boxes;

import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.ParameterizedExpression;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

import java.util.Objects;

public class HBoxNbHydrogensCriterion extends HBoxBoundingCriterion {

	public HBoxNbHydrogensCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
		getOperatorChoiceBox().getItems().addAll("even", "odd");
	}

	@Override
	public void updateValidity() {
		if ("even".equals(getOperatorChoiceBox().getValue()) || "odd".equals(getOperatorChoiceBox().getValue())) {
			setValid(true);
			setBounding(false);
			this.getChildren().remove(getFieldValue());
			removeWarningIconAndDeleteButton();
			addDeleteButton();
			getPane().refreshGenerationPossibility();
		}
		else
			super.updateValidity();
	}

	//TODO initialize

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {
		if (isValid()) {
			String operator = getOperatorChoiceBox().getValue();
			if (!Objects.equals(operator, "even") && !Objects.equals(operator, "odd"))
				modelPropertySet.getById("hydrogens").addExpression(new BinaryNumericalExpression("hydrogens", operator, Integer.decode(getFieldValue().getText())));
			else 
				modelPropertySet.getById("hydrogens").addExpression(new ParameterizedExpression("hydrogens", operator));
		}
	}
}
