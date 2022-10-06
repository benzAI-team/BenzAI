package modelProperty;

import modules.FragmentModule;
import modules.Module;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxPatternCriterion;

public class FragmentProperty extends ModelProperty {

	public FragmentProperty() {
		super("fragment", "Pattern properties", new FragmentModule());
	}

	@Override
	public HBoxCriterion getHBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxPatternCriterion(parent, choiceBoxCriterion, this);
	}

}
