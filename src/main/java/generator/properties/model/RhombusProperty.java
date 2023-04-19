package generator.properties.model;

import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;
import generator.properties.model.filters.RhombusFilter;
import constraints.RhombusConstraint;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxRhombusCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class RhombusProperty extends ModelProperty {

	public RhombusProperty() {
		super("rhombus", "Rhombus", new RhombusConstraint(), new RhombusFilter());
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxRhombusCriterion(parent, choiceBoxCriterion) ;
	}

	/***
	 * 
	 * @return the max size of the rhombus according to the given property expressions 
	 */
	private int computeSizeMax() {
		int sizeMax = Integer.MAX_VALUE;
		for(PropertyExpression expression : this.getExpressions()) {
			BinaryNumericalExpression binaryNumericalExpression = (BinaryNumericalExpression)expression;
			if(binaryNumericalExpression.hasUpperBound())
				sizeMax = sizeMax < binaryNumericalExpression.getValue() ? sizeMax : binaryNumericalExpression.getValue();
		}
		return sizeMax;
	}
	/***
	 * 
	 */
	@Override
	public int computeHexagonNumberUpperBound() {
		int sizeMax = computeSizeMax();
		return sizeMax != Integer.MAX_VALUE ? sizeMax * sizeMax : Integer.MAX_VALUE;
	}
	
	/***
	 * 
	 */
	@Override
	public int computeNbCrowns() {
		return computeSizeMax();
	}

}
