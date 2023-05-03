package generator.properties.model;

import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;
import generator.properties.model.filters.CarbonNumberFilter;
import constraints.CarbonNumberConstraint;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxNbCarbonsCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class CarbonNumberProperty extends ModelProperty {

	public CarbonNumberProperty() {
		super("carbons", "Number of carbons", new CarbonNumberConstraint(), new CarbonNumberFilter());
	}

	@Override
	public int computeHexagonNumberUpperBound() {
		int minCarbonNumber = Integer.MAX_VALUE;
		for(PropertyExpression binaryNumericalExpression : getExpressions()) {
			String  operator = ((BinaryNumericalExpression)binaryNumericalExpression).getOperator();
			int carbonNumber = ((BinaryNumericalExpression)binaryNumericalExpression).getValue();

			if (isBoundingOperator(operator)) {
				if ("<".equals(operator))
					carbonNumber--;
				if(carbonNumber < minCarbonNumber)
					minCarbonNumber = carbonNumber;
			}
		}
		return minCarbonNumber != Integer.MAX_VALUE ? (int)(Math.ceil((((double)minCarbonNumber - 6.0) / 4.0) + 1.0)) : Integer.MAX_VALUE;
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxNbCarbonsCriterion(parent, choiceBoxCriterion);
	}
}
