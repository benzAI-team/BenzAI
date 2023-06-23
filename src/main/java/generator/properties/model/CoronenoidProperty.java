package generator.properties.model;

import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;
import generator.properties.model.filters.CoronenoidFilter;
import constraints.CoronenoidConstraint;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxCoronenoidCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class CoronenoidProperty extends ModelProperty {


	public CoronenoidProperty() {
		super("coronenoid", "Coronenoid", new CoronenoidConstraint(), new CoronenoidFilter());
	}

	@Override
	public HBoxModelCriterion makeHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxCoronenoidCriterion(parent, choiceBoxCriterion);
	}

	@Override
	public int computeNbCrowns() {
		int nbCrownsMax = Integer.MAX_VALUE;
		for(PropertyExpression expression : this.getExpressions()) {
			BinaryNumericalExpression binaryNumericalExpression = (BinaryNumericalExpression)expression;
			if(binaryNumericalExpression.hasUpperBound())
				nbCrownsMax = Math.min(nbCrownsMax, binaryNumericalExpression.getValue());
		}
		return nbCrownsMax;
	}
	
	@Override
	public int computeHexagonNumberUpperBound() {
		int nbCrownsMax = Integer.MAX_VALUE;
		for(PropertyExpression expression : this.getExpressions()) {
			BinaryNumericalExpression binaryNumericalExpression = (BinaryNumericalExpression)expression;
			if(binaryNumericalExpression.hasUpperBound())
				nbCrownsMax = Math.min(nbCrownsMax, binaryNumericalExpression.getValue());
		}
		return nbCrownsMax != Integer.MAX_VALUE ? 3 * nbCrownsMax * (nbCrownsMax - 1) + 1 : Integer.MAX_VALUE;
	}

}
