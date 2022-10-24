package modelProperty;

import modules.PatternModule;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxPatternCriterion;

public class PatternProperty extends ModelProperty {

	public PatternProperty() {
		super("pattern", "Pattern properties", new PatternModule());
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxPatternCriterion(parent, choiceBoxCriterion, this);
	}

}
