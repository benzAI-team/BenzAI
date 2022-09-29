package modelProperty;

import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;
import modules.CoronoidModule2;
import modules.Module;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxCoronoidCriterion;
import view.generator.boxes.HBoxCriterion;

public class CoronoidProperty extends ModelProperty {

	public CoronoidProperty() {
		super("coronoid", "Coronoid", new CoronoidModule2());
	}

	@Override
	public HBoxCriterion getHBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxCoronoidCriterion(parent, choiceBoxCriterion) ;
	}

	@Override
	public int computeNbCrowns() {
		int nbMinHoles = 0;
		for(PropertyExpression expression : this.getExpressions()) {
			BinaryNumericalExpression binaryNumericalExpression = (BinaryNumericalExpression)expression;
			if(binaryNumericalExpression.hasLowerBound())
				nbMinHoles = nbMinHoles > binaryNumericalExpression.getValue() ? nbMinHoles : binaryNumericalExpression.getValue();
		}
//		if (nbMinHoles == 0)
//			nbMinHoles = 1;

		int nbCrowns;
		int hexagonUpperBound = this.getModelPropertySet().getHexagonNumberUpperBound();
		if (hexagonUpperBound > 4 * nbMinHoles)
					nbCrowns = (hexagonUpperBound + 2 - 4 * nbMinHoles) / 2;
		else
			nbCrowns = 1;
		return nbCrowns;	
	}
}
