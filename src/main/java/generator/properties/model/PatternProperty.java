package generator.properties.model;

import constraints.PatternConstraint;
import generator.patterns.Pattern;
import generator.properties.model.expression.PatternExpression;
import generator.properties.model.filters.PatternFilter;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxPatternCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class PatternProperty extends ModelProperty {

	PatternProperty() {
		super("pattern", "Pattern properties", new PatternConstraint(), new PatternFilter());
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxPatternCriterion(parent, choiceBoxCriterion, this);
	}

	@Override
	public int computeNbCrowns(){
		Pattern pattern = ((PatternExpression)getExpressions().get(0)).getPatternsInformations().getPatterns().get(0);
		int patternHexagonNumber = pattern.getNbNodes();
		int patternDiameter = 4; // TODO campute real value from pattern
		int hexagonNumberUpperBound = computeHexagonNumberUpperBound();
		return (hexagonNumberUpperBound - patternHexagonNumber + patternDiameter) / 2 + 2;
	}
}
