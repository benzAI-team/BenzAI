package view.generator.boxes;

import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.ParameterizedExpression;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

import java.util.Objects;

public class HBoxNbKekuleStructuresCriterion extends HBoxBoundingCriterion {

	
	public HBoxNbKekuleStructuresCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	public void updateValidity() {
		super.updateValidity();
		setBounding(false);
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
