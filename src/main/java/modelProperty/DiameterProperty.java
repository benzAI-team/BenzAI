package modelProperty;

import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;
import modules.DiameterModule;
import modules.Module;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxDiameterCriterion;

public class DiameterProperty extends ModelProperty {

	public DiameterProperty() {
		super("diameter", "Diameter", new DiameterModule());
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
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
				diameterMax = diameterMax < binaryNumericalExpression.getValue() ? diameterMax : binaryNumericalExpression.getValue();
		}
		return diameterMax != Integer.MAX_VALUE ? (int) Math.floor(((double) diameterMax + 3.0) / 2.0) : Integer.MAX_VALUE;	
	}

}
