package modelProperty;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import javafx.scene.control.ScrollPane;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;
import modelProperty.testers.CarbonNumberTester;
import modules.CarbonNumberModule;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxNbCarbonsCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class CarbonNumberProperty extends ModelProperty {

	public CarbonNumberProperty() {
		super("carbons", "Number of carbons", new CarbonNumberModule(), new CarbonNumberTester());
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
	public HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxNbCarbonsCriterion(parent, choiceBoxCriterion);
	}
}
