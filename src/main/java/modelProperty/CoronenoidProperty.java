package modelProperty;

import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;
import modelProperty.testers.CoronenoidTester;
import modules.CoronenoidModule;
import modules.Module;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxCoronenoidCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class CoronenoidProperty extends ModelProperty {


	public CoronenoidProperty() {
		super("coronenoid", "Coronenoid", new CoronenoidModule(), new CoronenoidTester());
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxCoronenoidCriterion(parent, choiceBoxCriterion);
	}

	@Override
	public int computeNbCrowns() {
		int nbCrownsMax = Integer.MAX_VALUE;
		for(PropertyExpression expression : this.getExpressions()) {
			BinaryNumericalExpression binaryNumericalExpression = (BinaryNumericalExpression)expression;
			if(binaryNumericalExpression.hasUpperBound())
				nbCrownsMax = nbCrownsMax < binaryNumericalExpression.getValue() ? nbCrownsMax : binaryNumericalExpression.getValue();
		}
		return nbCrownsMax;
	}
	
	@Override
	public int computeHexagonNumberUpperBound() {
		int nbCrownsMax = Integer.MAX_VALUE;
		for(PropertyExpression expression : this.getExpressions()) {
			BinaryNumericalExpression binaryNumericalExpression = (BinaryNumericalExpression)expression;
			if(binaryNumericalExpression.hasUpperBound())
				nbCrownsMax = nbCrownsMax < binaryNumericalExpression.getValue() ? nbCrownsMax : binaryNumericalExpression.getValue();
		}
		return nbCrownsMax != Integer.MAX_VALUE ? 3 * nbCrownsMax * (nbCrownsMax - 1) + 1 : Integer.MAX_VALUE;
	}

}
