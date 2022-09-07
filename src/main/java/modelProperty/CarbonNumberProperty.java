package modelProperty;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import modelProperty.builders.CarbonNumberPropertyBuilder;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;
import modules.CarbonNumberModule;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxNbCarbonsCriterion;

public class CarbonNumberProperty extends ModelProperty {

	public CarbonNumberProperty() {
		super("carbons", new CarbonNumberModule());
	}

	@Override
	public int computeHexagonNumberUpperBound() {
		int minCarbonNumber = Integer.MAX_VALUE;
		for(PropertyExpression binaryNumericalExpression : getExpressions()) {
			String  operator = ((BinaryNumericalExpression)binaryNumericalExpression).getOperator();
			int carbonNumber = ((BinaryNumericalExpression)binaryNumericalExpression).getValue();

			if (GeneratorCriterion.isBoundingOperator(operator)) {
				if (operator == "<")
					carbonNumber--;
				if(carbonNumber < minCarbonNumber)
					minCarbonNumber = carbonNumber;
			}
		}
		return minCarbonNumber != Integer.MAX_VALUE ? (int)(Math.ceil((((double)minCarbonNumber - 6.0) / 4.0) + 1.0)) : Integer.MAX_VALUE;
	}

	@Override
	public HBoxCriterion getHBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxNbCarbonsCriterion(parent, choiceBoxCriterion);
	}
}
