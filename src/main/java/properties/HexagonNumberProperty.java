package properties;

import properties.expression.BinaryNumericalExpression;
import properties.expression.PropertyExpression;
import properties.filters.HexagonNumberFilter;
import constraints.HexagonNumberConstraint;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;
import view.generator.boxes.HBoxHexagonNumberCriterion;

public class HexagonNumberProperty extends ModelProperty {

	public HexagonNumberProperty() {
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
	public HBoxModelCriterion makeHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxHexagonNumberCriterion(parent, choiceBoxCriterion);
	}
}
