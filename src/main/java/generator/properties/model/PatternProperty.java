package generator.properties.model;

import constraints.PatternCollectionConstraint;
import constraints.PatternConstraint;
import generator.properties.model.filters.PatternFilter;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxPatternCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class PatternProperty extends ModelProperty {

	PatternProperty() {
		super("pattern", "Pattern properties", new PatternCollectionConstraint(), new PatternFilter());
	}

	@Override
	public HBoxModelCriterion makeHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxPatternCriterion(parent, choiceBoxCriterion, this);
	}


	/**
	 * @return The maximal number of crowns according to the patterns' properties.
	 */
	@Override
	public int computeNbCrowns() {
//		ArrayList<Pattern> patterns = ((PatternExpression) this.getExpressions().get(0)).getPatternsInformations().getPatterns();
//		int diameterSum = patterns.stream().mapToInt(pattern -> pattern.computeGridDiameter() + 1).sum();
//		int nbPositiveNodes = patterns.stream().mapToInt(Pattern::getNbPositiveNodes).sum();
//		int nbHexagons = ((ModelPropertySet) this.getPropertySet()).getHexagonNumberUpperBound();
//		int patternNbCrowns = (diameterSum + nbHexagons - nbPositiveNodes + 2) / 2;
//
//		return (nbHexagons >= nbPositiveNodes) ? patternNbCrowns : 1;
		// TODO The computation should take into account the property (existence, interaction and exclusion)
		return 4;
	}

	public void addConstraint (PatternConstraint patternConstraint) {
		((PatternCollectionConstraint) getConstraint()).addPatternConstraint(patternConstraint);
	}
}
