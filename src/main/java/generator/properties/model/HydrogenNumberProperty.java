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
	HydrogenNumberProperty() {
		super("hydrogens", "Number of hydrogens", new HydrogenNumberConstraint(), new HydrogenNumberFilter());
	}
	/***
	 * Compute the max number of hexagons from the (max) number of hydrogens.
	 * The structure that minimize the number of hydrogens for a given number
	 * of hexagons is the coronenoid case where #hex = 3n(n - 1)+1 and
	 * #hydro = 6n where n is the number of crowns,
	 * so #hex = #hydro / 2 (#hydro/6 - 1) + 1
	 */
	@Override
	public int computeHexagonNumberUpperBound() {
		int minHydrogenNumber = Integer.MAX_VALUE;
		for(PropertyExpression propertyExpression : getExpressions()) {
			if (propertyExpression instanceof BinaryNumericalExpression) {
				String operator = ((BinaryNumericalExpression) propertyExpression).getOperator();
				int hydrogenNumber = ((BinaryNumericalExpression) propertyExpression).getValue();
				if (isBoundingOperator(operator)) {
					if ("<".equals(operator))
						hydrogenNumber--;
					minHydrogenNumber = Math.min(hydrogenNumber, minHydrogenNumber);
				}
			}
		}
		return Math.min((int)(Math.ceil((minHydrogenNumber / 2.0)  * (minHydrogenNumber / 6.0 - 1.0) + 1.0)) , Integer.MAX_VALUE);
	}

	@Override
	public HBoxModelCriterion makeHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxNbHydrogensCriterion(parent, choiceBoxCriterion);
	}

}
