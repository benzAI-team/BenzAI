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

	CarbonNumberProperty() {
		super("carbons", "Number of carbons", new CarbonNumberConstraint(), new CarbonNumberFilter());
	}

	/***
	 * Compute the max number of hexagons from the (max)number of carbons.
	 * The structure that minimize thz number of carbons for a given number
	 * of hexagons is the coronenoid case where #hex = 3n(n - 1)+1 and
	 * #carbons = 6n2 where n is the number of crowns,
	 * so #hex = #carbons /2 - 3 sqrt(#carbons/6) +1
	 */
	@Override
	public int computeHexagonNumberUpperBound() {
		int minCarbonNumber = Integer.MAX_VALUE;
		for(PropertyExpression binaryNumericalExpression : getExpressions()) {
			String  operator = ((BinaryNumericalExpression)binaryNumericalExpression).getOperator();
			int carbonNumber = ((BinaryNumericalExpression)binaryNumericalExpression).getValue();

			if (isBoundingOperator(operator)) {
				if ("<".equals(operator))
					carbonNumber--;
				minCarbonNumber = Math.min(carbonNumber, minCarbonNumber);
			}
		}
		//return Math.min((int)(Math.ceil((((double)minCarbonNumber - 6.0) / 2.0) + 1.0)), Integer.MAX_VALUE);
		return (int) Math.min(Math.ceil((double) minCarbonNumber / 2.0 - 3.0 * Math.sqrt(minCarbonNumber / 6.0) + 1.0), Integer.MAX_VALUE);
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxNbCarbonsCriterion(parent, choiceBoxCriterion);
	}
}
