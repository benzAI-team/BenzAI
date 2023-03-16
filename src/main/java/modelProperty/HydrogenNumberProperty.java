package modelProperty;

import generator.GeneratorCriterion;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;
import modelProperty.testers.HydrogenNumberTester;
import modules.CarbonNumberModule;
import modules.HydrogenNumberModule;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxNbCarbonsCriterion;
import view.generator.boxes.HBoxNbHydrogensCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HydrogenNumberProperty extends ModelProperty {
	public HydrogenNumberProperty() {
		super("hydrogens", "Number of hydrogens", new HydrogenNumberModule(), new HydrogenNumberTester());
	}

	@Override
	public int computeHexagonNumberUpperBound() {
		int minHydrogenNumber = Integer.MAX_VALUE;
		for(PropertyExpression binaryNumericalExpression : getExpressions()) {
			String  operator = ((BinaryNumericalExpression)binaryNumericalExpression).getOperator();
			int hydrogenNumber = ((BinaryNumericalExpression)binaryNumericalExpression).getValue();

			if (GeneratorCriterion.isBoundingOperator(operator)) {
				if (operator == "<")
					hydrogenNumber--;
				if(hydrogenNumber < minHydrogenNumber)
					minHydrogenNumber = hydrogenNumber;
			}
		}
		return minHydrogenNumber != Integer.MAX_VALUE ? (int)(Math.ceil(((minHydrogenNumber - 8) / 2.0) + 2.0)) : Integer.MAX_VALUE;
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxNbHydrogensCriterion(parent, choiceBoxCriterion);
	}

}
