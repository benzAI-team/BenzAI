package generator.properties.model;

import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;
import generator.properties.model.filters.HexagonNumberFilter;
import constraints.HexagonNumberConstraint;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;
import view.generator.boxes.HBoxHexagonNumberCriterion;

public class HexagonNumberProperty extends ModelProperty {

	HexagonNumberProperty() {
		super("hexagons", "Number of hexagons", new HexagonNumberConstraint(), new HexagonNumberFilter());
	}

	@Override
	public int computeHexagonNumberUpperBound() {

		int hexagonNumberMin = Integer.MAX_VALUE;
		for (PropertyExpression binaryNumericalExpression : this.getExpressions()) {
			String operator = ((BinaryNumericalExpression)binaryNumericalExpression).getOperator();
			int hexagonNumber = ((BinaryNumericalExpression)binaryNumericalExpression).getValue();
			if (isBoundingOperator(operator)) {
				if ("<".equals(operator))
					hexagonNumber--;
				hexagonNumberMin = Math.min(hexagonNumber, hexagonNumberMin);
			}	
		}
		return hexagonNumberMin;
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxHexagonNumberCriterion(parent, choiceBoxCriterion);
	}
}
