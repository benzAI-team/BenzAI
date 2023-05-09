package generator.properties.model;

import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;
import generator.properties.model.filters.DiameterFilter;
import constraints.DiameterConstraint;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;
import view.generator.boxes.HBoxDiameterCriterion;

public class DiameterProperty extends ModelProperty {

	public DiameterProperty() {
		super("diameter", "Diameter", new DiameterConstraint(), new DiameterFilter());
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxDiameterCriterion(parent, choiceBoxCriterion);
	}

	@Override
	public int computeHexagonNumberUpperBound() {
		int nbCrowns = computeNbCrowns();
		return nbCrowns != Integer.MAX_VALUE ? 3 * nbCrowns * (nbCrowns - 1) + 1 : Integer.MAX_VALUE;
	}
	
	@Override
	public int computeNbCrowns() {
		int diameterMax = Integer.MAX_VALUE;
		for(PropertyExpression expression : this.getExpressions()) {
			BinaryNumericalExpression binaryNumericalExpression = (BinaryNumericalExpression)expression;
			if(binaryNumericalExpression.hasUpperBound())
				diameterMax = Math.min(diameterMax, binaryNumericalExpression.getValue());
		}
		return Math.min(Integer.MAX_VALUE , (int) Math.floor(((double) diameterMax + 3.0) / 2.0));
	}

}
