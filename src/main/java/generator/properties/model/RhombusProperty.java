package generator.properties.model;

import constraints.RhombusConstraint;
import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;
import generator.properties.model.filters.RhombusFilter;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxRhombusCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class RhombusProperty extends ModelProperty {

	RhombusProperty() {
		super("rhombus", "Rhombus", new RhombusConstraint(), new RhombusFilter());
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxRhombusCriterion(parent, choiceBoxCriterion) ;
	}

	/***
	 * @return the max size of the rhombus according to the given property expressions 
	 */
	private int computeSizeMax() {
//		int sizeMax = Integer.MAX_VALUE;
//		for(PropertyExpression expression : this.getExpressions()) {
//			BinaryNumericalExpression binaryNumericalExpression = (BinaryNumericalExpression)expression;
//			if(binaryNumericalExpression.hasUpperBound())
//				sizeMax = Math.min(sizeMax, binaryNumericalExpression.getValue());
//		}
		return this.getExpressions().stream()
				.filter(PropertyExpression::hasUpperBound)
				.reduce(Integer.MAX_VALUE, (acc, expression) -> Math.min(acc, ((BinaryNumericalExpression)expression).getValue()), Math::min);
	}
	/***
	 * 
	 */
	@Override
	public int computeHexagonNumberUpperBound() {
		int sizeMax = computeSizeMax();
		return Math.min(Integer.MAX_VALUE, sizeMax * sizeMax);
	}
	
	/***
	 * 
	 */
	@Override
	public int computeNbCrowns() {
		return computeSizeMax();
	}

}
