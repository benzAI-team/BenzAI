package generator.properties.model;

import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;
import generator.properties.model.filters.CoronoidFilter;
import constraints.CoronoidConstraint;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxCoronoidCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class CoronoidProperty extends ModelProperty {

	public CoronoidProperty() {
		super("coronoid", "Coronoid", new CoronoidConstraint(), new CoronoidFilter());
	}

	@Override
	public HBoxModelCriterion makeHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxCoronoidCriterion(parent, choiceBoxCriterion) ;
	}

	@Override
	public int computeNbCrowns() {
		int nbMinHoles = 0;
		for(PropertyExpression expression : this.getExpressions()) {
			BinaryNumericalExpression binaryNumericalExpression = (BinaryNumericalExpression)expression;
			if(binaryNumericalExpression.hasLowerBound())
				nbMinHoles = Math.max(nbMinHoles, binaryNumericalExpression.getValue());
		}

		int nbCrowns;
		int hexagonUpperBound = ((ModelPropertySet) this.getPropertySet()).getHexagonNumberUpperBound();
		if (hexagonUpperBound > 4 * nbMinHoles)
					nbCrowns = (hexagonUpperBound + 2 - 4 * nbMinHoles) / 2;
		else
			nbCrowns = 1;
		return nbCrowns;	
	}
}
