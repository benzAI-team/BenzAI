package modelProperty;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;
import modules.HexagonNumberModule;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxHexagonNumberCriterion;

public class HexagonNumberProperty extends ModelProperty {

	public HexagonNumberProperty() {
		super("hexagons", new HexagonNumberModule());
	}

	@Override
	public int computeHexagonNumberUpperBound() {

		int hexagonNumberMin = Integer.MAX_VALUE;
		for (PropertyExpression binaryNumericalExpression : this.getExpressions()) {
			String operator = ((BinaryNumericalExpression)binaryNumericalExpression).getOperator();
			int hexagonNumber = ((BinaryNumericalExpression)binaryNumericalExpression).getValue();
			if (GeneratorCriterion.isBoundingOperator(operator)) {
				if (operator == "<")
					hexagonNumber--;
				if(hexagonNumber < hexagonNumberMin)
					hexagonNumberMin = hexagonNumber;
			}	
		}
		return hexagonNumberMin;
	}

	@Override
	public HBoxCriterion getHBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxHexagonNumberCriterion(parent, choiceBoxCriterion);
	}
}
