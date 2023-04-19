package generator.properties.model;

import generator.properties.model.filters.PatternFilter;
import constraints.PatternConstraint;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxPatternCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class PatternProperty extends ModelProperty {

	public PatternProperty() {
		super("pattern", "Pattern properties", new PatternConstraint(), new PatternFilter());
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxPatternCriterion(parent, choiceBoxCriterion, this);
	}

}
