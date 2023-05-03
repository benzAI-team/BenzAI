package generator.properties.model;

import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;
import generator.properties.model.filters.HydrogenNumberFilter;
import constraints.HydrogenNumberConstraint;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxNbHydrogensCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HydrogenNumberProperty extends ModelProperty {
	public HydrogenNumberProperty() {
		super("hydrogens", "Number of hydrogens", new HydrogenNumberConstraint(), new HydrogenNumberFilter());
	}

	@Override
	public int computeHexagonNumberUpperBound() {
		int minHydrogenNumber = Integer.MAX_VALUE;
		for(PropertyExpression binaryNumericalExpression : getExpressions()) {
			String  operator = ((BinaryNumericalExpression)binaryNumericalExpression).getOperator();
			int hydrogenNumber = ((BinaryNumericalExpression)binaryNumericalExpression).getValue();

			if (isBoundingOperator(operator)) {
				if ("<".equals(operator))
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
